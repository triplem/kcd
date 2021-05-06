plugins {
    id("org.javafreedom.kotlin-library-conventions")
}

val kloggingVersion: String by project

val assertkVersion: String by project
val mockkVersion: String by project
val kotlinCoroutinesVersion: String by project

dependencies {
    implementation("io.github.microutils:kotlin-logging:$kloggingVersion")

    testImplementation("com.willowtreeapps.assertk:assertk-jvm:$assertkVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinCoroutinesVersion")
}


