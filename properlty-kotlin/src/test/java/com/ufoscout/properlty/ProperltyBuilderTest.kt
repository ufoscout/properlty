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

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail

import java.io.FileNotFoundException
import java.nio.charset.StandardCharsets
import kotlin.collections.Map.Entry
import java.util.UUID

import org.junit.Test

import com.ufoscout.properlty.Properlty
import com.ufoscout.properlty.exception.UnresolvablePlaceholdersException
import com.ufoscout.properlty.reader.Properties
import com.ufoscout.properlty.reader.PropertiesResourceReader

class ProperltyBuilderTest : ProperltyBaseTest() {

    @Test
    fun environmentVariablesKeysShouldBeIncludedAndNormalized() {

        val envVar = System.getenv()
        assertFalse(envVar.isEmpty())

        val prop = Properlty.builder().build()

        for ((key, value) in envVar) {

            logger.info("Checking key [{}]", key)

            assertEquals(value, prop[key].get())

            val normalizedKey = key.toLowerCase().replace("_", ".")
            assertTrue(prop[normalizedKey].isPresent)
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

            val prop = Properlty.builder().build()

            assertEquals(overriddenValue, prop[envVarKey1Normalized].get())
            assertEquals(envVarValue1, prop[envVarKey1].get())

            assertEquals(envVarValue2, prop[envVarKey2Normalized].get())
            assertEquals(envVarValue2, prop[envVarKey2].get())

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
                .build()

        assertEquals(envVarValue1, prop[envVarKey1Normalized].get())
        assertEquals(customValue, prop[customKey2].get())
    }

    @Test
    fun shouldIgnoreFileNotFound() {

        val key = UUID.randomUUID().toString()
        try {
            System.setProperty(key, key)

            val prop = Properlty.builder()
                    .add(PropertiesResourceReader.build("NOT VALID PATH").ignoreNotFound(true).charset(StandardCharsets.UTF_8))
                    .build()
            assertNotNull(prop)

            assertTrue(prop[key].isPresent)
        } finally {
            System.clearProperty(key)
        }

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
        assertEquals("firstvalue", prop["keyOne"].get())

        // from classpath:resource1.properties AND classpath:resource2.properties
        assertEquals("resource2", prop["name"].get())

    }

    @Test
    fun shouldBePossibleTosetCustomPriority() {

        val key = UUID.randomUUID().toString()
        try {
            System.setProperty(key, "SystemProperty")

            val prop = Properlty.builder()
                    .add(Properties.add(key, "customReader"), Properlty.HIGHEST_PRIORITY)
                    .build()
            assertNotNull(prop)

            assertEquals("customReader", prop[key].get())
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
                    .add(Properties.add("key2", "\${\${key3}}__\${key1}"), Properlty.HIGHEST_PRIORITY)
                    .add(Properties.add("key3", "key1"), Properlty.HIGHEST_PRIORITY)
                    .build()
            assertNotNull(prop)

            assertEquals(value1 + "__" + value1, prop["key2"].get())
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
                .add(Properties.add("key1", "value1").add("key2", "((((key3))))__((key1))"), Properlty.HIGHEST_PRIORITY)
                .add(Properties.add("key3", "key1"), Properlty.HIGHEST_PRIORITY)
                .build()
        assertNotNull(prop)

        assertEquals("value1__value1", prop["key2"].get())

    }

    @Test
    fun shouldIgnoreNotResolvedPlaceHolders() {

        val prop = Properlty.builder()
                .ignoreUnresolvablePlaceholders(true)
                .add(Properties.add("key2", "\${\${key3}}__\${key1}"), Properlty.HIGHEST_PRIORITY)
                .add(Properties.add("key3", "key1"), Properlty.HIGHEST_PRIORITY)
                .build()
        assertNotNull(prop)

        assertEquals("\${key1}__\${key1}", prop["key2"].get())

    }

    @Test(expected = UnresolvablePlaceholdersException::class)
    fun shouldFailIfNotResolvedPlaceHolders() {
        Properlty.builder()
                .add(Properties.add("key2", "\${\${key3}}__\${key1}"), Properlty.HIGHEST_PRIORITY)
                .add(Properties.add("key3", "key1"), Properlty.HIGHEST_PRIORITY)
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
