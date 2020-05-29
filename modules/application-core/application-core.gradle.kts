plugins {
    id("org.javafreedom.kotlin-library-conventions")
}

val klogging_version: String by project
val valiktor_version: String by project
val junit_version: String by project
val cucumber_version: String = "6.9.1"

dependencies {
    implementation("io.github.microutils:kotlin-logging:${klogging_version}")
    implementation("org.valiktor:valiktor-core:${valiktor_version}")


//    functionalTestImplementation("com.willowtreeapps.assertk:assertk-jvm:0.23")
//    functionalTestImplementation("io.mockk:mockk:1.10.5")
//    functionalTestImplementation("io.cucumber:cucumber-java:${cucumber_version}")
//    functionalTestImplementation("io.cucumber:cucumber-junit-platform-engine:${cucumber_version}")
//    functionalTestImplementation("io.cucumber:cucumber-picocontainer:${cucumber_version}")
//
//    functionalTestImplementation("org.junit.jupiter:junit-jupiter-api:$junit_version")
//    functionalTestRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit_version")
}


