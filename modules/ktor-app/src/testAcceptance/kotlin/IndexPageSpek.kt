import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.prop
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

class IndexPageSpek : Spek({

    val spekApplication = SpekApplication()
    val client = OkHttpClient()
    val url = "http://localhost:8080/"

    beforeGroup {
        spekApplication.start()
    }

    afterGroup {
        spekApplication.stop()
    }

    Feature("calling index page of application") {
        lateinit var response: Response

        Scenario("calling the index page with 'get'") {
            When("using 'get'") {
                val request = Request.Builder().url(url).get().build()
                response = client.newCall(request).execute()
            }

            Then("it should return an OK status code") {
                assertThat(response)
                    .prop(Response::code)
                    .isEqualTo(200)
            }

            Then("it should return 'HELLO WORLD!'") {
                assertThat(response)
                    .prop(Response::body).transform { it?.string() }
                    .isEqualTo("HELLO WORLD!")
            }
        }

        Scenario("calling the index page with 'post'") {
            When("using 'post'") {
                val body = "nix".toRequestBody("application/json".toMediaType())
                val request = Request.Builder().url(url).post(body).build()
                response = client.newCall(request).execute()
            }

            Then("it should return an NotFound status code") {
                assertThat(response)
                    .prop(Response::code)
                    .isEqualTo(404)
            }

            Then("it should return 'HELLO WORLD!'") {
                assertThat(response)
                    .prop(Response::message)
                    .isEqualTo("Not Found")
            }
        }
    }
})