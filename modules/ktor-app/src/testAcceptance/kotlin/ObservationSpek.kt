import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isEqualToIgnoringGivenProperties
import assertk.assertions.isNotNull
import assertk.assertions.prop
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.javafreedom.kcd.adapters.rest.model.RequestObservation
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import java.io.File

class ObservationSpek : Spek({

    val spekApplication = SpekApplication()
    val client = OkHttpClient()
    val url = "http://localhost:8080/observation"

    beforeGroup {
        spekApplication.start()
    }

    afterGroup {
        spekApplication.stop()
    }

    Feature("create and reload observation") {
        lateinit var call: Call
        lateinit var observationId: String

        Scenario("creating the observation") {
            When("using 'post'") {
                val observationJson =
                    File(ClassLoader.getSystemResource("dummy-observation.json").file).readText()
                val body = observationJson.toRequestBody("application/json".toMediaType())

                val request = Request.Builder().url(url).post(body).build()
                call = client.newCall(request)
            }

            Then("it should return an Created status code") {
                val response = call.execute()

                response.use { result ->
                    assertThat(result)
                        .prop(Response::code)
                        .isEqualTo(201)

                    assertThat(response)
                        .prop(Response::body).transform { it?.string() }
                        .isNotNull()
                }
            }
        }

        Scenario("send wrong input to the server") {
            When("using 'post' with wrong input") {
                val body = "nix".toRequestBody("application/json".toMediaType())
                val request = Request.Builder().url(url).post(body).build()
                call = client.newCall(request)
            }

            Then("it should return an NotFound status code") {
                val response = call.execute()

                response.use {
                    assertThat(response)
                        .prop(Response::code)
                        .isEqualTo(400)

                    assertThat(response)
                        .prop(Response::message)
                        .isEqualTo("Bad Request")
                }
            }
        }

        Scenario("retrieve observation") {
            var uuid: String = ""

            When("creating a observation 'post'") {
                val observationJson =
                    File(ClassLoader.getSystemResource("dummy-observation.json").file).readText()
                val body = observationJson.toRequestBody("application/json".toMediaType())

                val request = Request.Builder().url(url).post(body).build()
                call = client.newCall(request)

                val response = call.execute()
                val responseBody = response.use {
                    response.body?.string() ?: ""
                }

                val json = Json { }
                val responseObject = json.parseToJsonElement(responseBody)

                uuid = responseObject.jsonObject["id"]?.jsonPrimitive?.content ?: ""
            }

            Then("the same observation is retrieved") {
                val getUrl = "$url/$uuid"

                println("getUrl: '$getUrl'")

                val request = Request.Builder().url(getUrl).get().build()
                call = client.newCall(request)

                val response = call.execute()

                response.use { result ->
                    assertThat(result)
                        .prop(Response::code)
                        .isEqualTo(200)

                    val json = Json { ignoreUnknownKeys = true }
                    val expectedText =
                        File(ClassLoader.getSystemResource("dummy-observation.json").file).readText()
                    val expectedElement = json.parseToJsonElement(expectedText).jsonObject

                    val expected: RequestObservation = json.decodeFromJsonElement(expectedElement)

                    val responseBody = result.body?.string() ?: ""

                    val responseObject: RestObservation =
                        json.decodeFromString(responseBody)

                    assertThat(responseObject)
                        .isEqualToIgnoringGivenProperties(expected.value, RestObservation::id)
                }
            }
        }
    }
})

typealias RestObservation = org.javafreedom.kcd.adapters.rest.model.Observation