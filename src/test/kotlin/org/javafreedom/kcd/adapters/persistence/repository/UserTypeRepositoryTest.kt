package org.javafreedom.kcd.adapters.persistence.repository

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest
import kotlin.test.assertTrue

class UserTypeRepositoryTest: RepositoryTest<UserTypeRepository>() {

    companion object {
        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            prepareDatabase("usertype.cql")
        }
    }

//    @BeforeTest
    fun createRepo() {
        createRepository<UserTypeRepository>()
    }

//    @Test
    fun `find types by user`() {
        runBlocking {
            getSut().insert("user1", "type1")
            getSut().insert("user1", "type2")
            getSut().insert("user1", "type3")
            getSut().insert("user1", "type1")
            getSut().insert("user2", "type4")
        }

        runBlocking {
            val result = getSut().findUserTypes("user1")

            assertTrue(result.contains("type1"))
            assertTrue(result.contains("type2"))
            assertTrue(result.contains("type3"))
            assertTrue(!result.contains("type4"))

            assertTrue(result.size == 3)
        }
    }
}
