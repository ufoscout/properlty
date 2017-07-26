/*******************************************************************************
 * Copyright 2017 Francesco Cina'

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ufoscout.properlty

import com.ufoscout.properlty.exception.UnresolvablePlaceholdersException
import com.ufoscout.properlty.reader.EnvironmentVariablesReader
import com.ufoscout.properlty.reader.Properties
import com.ufoscout.properlty.reader.SystemPropertiesReader
import com.ufoscout.properlty.reader.decorator.ToLowerCaseAndDotKeyReader
import org.junit.Assert.*
import org.junit.Test
import java.io.FileNotFoundException
import java.nio.charset.StandardCharsets
import java.util.*

class ProperltyBuilderTest : ProperltyBaseTest() {

    @Test
    fun environmentVariablesKeysShouldBeIncludedAndNormalized() {

        val envVar = System.getenv()
        assertFalse(envVar.isEmpty())

        val prop = Properlty.builder()
                .add(EnvironmentVariablesReader())
                .add(ToLowerCaseAndDotKeyReader(EnvironmentVariablesReader()))
                .build()

        for ((key, value) in envVar) {

            assertEquals(value, prop[key])

            val normalizedKey = key.toLowerCase().replace("_", ".")
            assertNotNull(prop[normalizedKey])
        }

    }

    @Test
    fun systemPropertiesShouldHaveHigherPriorityThanEnvVariables() {
        val envVar = System.getenv()

        val envVarKeys = getKeysWithUppercase(envVar, 2)

        val envVarKey1 = envVarKeys[0]
        val envVarKey1Normalized = envVarKey1.toLowerCase().replace("_", ".")
        val envVarValue1 = envVar[envVarKey1]

        val envVarKey2 = envVarKeys[1]
        val envVarKey2Normalized = envVarKey2.toLowerCase().replace("_", ".")
        val envVarValue2 = envVar[envVarKey2]

        try {
            // Override an environment variable with a system property
            val overriddenValue = UUID.randomUUID().toString()
            System.setProperty(envVarKey1Normalized, overriddenValue)

            val prop = Properlty.builder()
                    .add(EnvironmentVariablesReader())
                    .add(ToLowerCaseAndDotKeyReader(EnvironmentVariablesReader()))
                    .add(SystemPropertiesReader())
                    .build()

            assertEquals(overriddenValue, prop[envVarKey1Normalized])
            assertEquals(envVarValue1, prop[envVarKey1])

            assertEquals(envVarValue2, prop[envVarKey2Normalized])
            assertEquals(envVarValue2, prop[envVarKey2])

        } finally {
            System.clearProperty(envVarKey1Normalized)
        }
    }

    @Test
    fun envVariablesShouldHaveHigherPriorityThanCustomProperties() {

        val envVar = System.getenv()
        assertTrue(envVar.size >= 1)

        val envVarKeys = getKeysWithUppercase(envVar, 1)

        val envVarKey1 = envVarKeys[0]
        val envVarKey1Normalized = envVarKey1.toLowerCase().replace("_", ".")
        val envVarValue1 = envVar[envVarKey1]

        val customValue = UUID.randomUUID().toString()
        val customKey2 = UUID.randomUUID().toString()

        val prop = Properlty.builder()
                .add(Properties.add(envVarKey1Normalized, customValue).add(customKey2, customValue))
                .add(ToLowerCaseAndDotKeyReader(EnvironmentVariablesReader()))
                .build()

        assertEquals(envVarValue1, prop[envVarKey1Normalized])
        assertEquals(customValue, prop[customKey2])
    }

    @Test
    fun shouldIgnoreFileNotFound() {

        val key = UUID.randomUUID().toString()
        val prop = Properlty.builder()
                .add(Properties.add(key, "value"))
                .add("NOT VALID PATH", true, StandardCharsets.UTF_8)
                .build()
        assertNotNull(prop)

        assertNotNull(prop[key])

    }

    @Test
    fun shouldFailIfFileNotFound() {
        try {
            Properlty.builder()
                    .add("NOT VALID PATH")
                    .build()
            fail("Should fail before")
        } catch (e: RuntimeException) {
            assertTrue(e.cause is FileNotFoundException)
        }

    }

    @Test
    fun shouldConsiderFileAddPriority() {
        val prop = Properlty.builder()
                .add("file:./src/test/files/test1.properties")
                .add("classpath:resource1.properties")
                .add("classpath:inner/resource2.properties")
                .build()

        // from file:./src/test/files/test1.properties
        assertEquals("firstvalue", prop["keyOne"])

        // from classpath:resource1.properties AND classpath:resource2.properties
        assertEquals("resource2", prop["name"])

    }

    @Test
    fun shouldBePossibleTosetCustomPriority() {

        val key = UUID.randomUUID().toString()
        try {
            System.setProperty(key, "SystemProperty")

            val prop = Properlty.builder()
                    .add(Properties.add(key, "customReader"), Default.HIGHEST_PRIORITY)
                    .build()
            assertNotNull(prop)

            assertEquals("customReader", prop[key])
        } finally {
            System.clearProperty(key)
        }

    }

    @Test
    fun shouldReplacePlaceHolders() {

        val key1 = "key1"
        val value1 = UUID.randomUUID().toString()
        try {
            System.setProperty(key1, value1)

            val prop = Properlty.builder()
                    .add(SystemPropertiesReader())
                    .add(Properties.add("key2", "\${\${key3}}__\${key1}"), Default.HIGHEST_PRIORITY)
                    .add(Properties.add("key3", "key1"), Default.HIGHEST_PRIORITY)
                    .build()
            assertNotNull(prop)

            assertEquals(value1 + "__" + value1, prop["key2"])
        } finally {
            System.clearProperty(key1)
        }

    }

    @Test
    fun shouldReplaceUsingCustomDelimiters() {

        val startDelimiter = "(("
        val endDelimiter = "))"

        val prop = Properlty.builder()
                .delimiters(startDelimiter, endDelimiter)
                .add(Properties.add("key1", "value1").add("key2", "((((key3))))__((key1))"), Default.HIGHEST_PRIORITY)
                .add(Properties.add("key3", "key1"), Default.HIGHEST_PRIORITY)
                .build()
        assertNotNull(prop)

        assertEquals("value1__value1", prop["key2"])

    }

    @Test
    fun shouldIgnoreNotResolvedPlaceHolders() {

        val prop = Properlty.builder()
                .ignoreUnresolvablePlaceholders(true)
                .add(Properties.add("key2", "\${\${key3}}__\${key1}"), Default.HIGHEST_PRIORITY)
                .add(Properties.add("key3", "key1"), Default.HIGHEST_PRIORITY)
                .build()
        assertNotNull(prop)

        assertEquals("\${key1}__\${key1}", prop["key2"])

    }

    @Test(expected = UnresolvablePlaceholdersException::class)
    fun shouldFailIfNotResolvedPlaceHolders() {
        Properlty.builder()
                .add(Properties.add("key2", "\${\${key3}}__\${key1}"), Default.HIGHEST_PRIORITY)
                .add(Properties.add("key3", "key1"), Default.HIGHEST_PRIORITY)
                .build()
    }


    private fun getKeysWithUppercase(map: Map<String, *>, howMany: Int): Array<String> {
        val keys = Array (howMany, { i -> "" })

        var current = 0

        for (entry in map.keys) {
            if (entry.matches(".*[A-Z].*".toRegex())) {
                keys[current] = entry
                current++
                if (current >= howMany) {
                    break
                }
            }
        }

        if (current < howMany) {
            throw RuntimeException("Not enough environment variables with at least an uppercase character! Needed [" + howMany + "] found [" + (current - 1) + "]")
        }
        return keys
    }

}
