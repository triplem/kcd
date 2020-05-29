package org.javafreedom.kcd.service

import kotlinx.serialization.Serializable
import java.util.*

class User(val name: String, val password: String)

/**
 * Demo User Datasource
 *
 * In a real-world application this would be "solved" by a more sophisticated solution
 */
val users = Collections.synchronizedMap(
    listOf(User("test", "test"))
        .associateBy { it.name }
        .toMutableMap()
)

@Serializable
data class LoginRegister(val user: String, val password: String)
