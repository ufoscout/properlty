/*******************************************************************************
 * Copyright 2017 Francesco Cina'
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.ufoscout.properlty;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.ufoscout.properlty.reader.EnvironmentVariablesReader;
import com.ufoscout.properlty.reader.SystemPropertiesReader;
import com.ufoscout.properlty.reader.decorator.ToLowerCaseAndDotKeyReader;
import org.junit.Test;

import com.ufoscout.properlty.exception.UnresolvablePlaceholdersException;
import com.ufoscout.properlty.reader.Properties;
import com.ufoscout.properlty.reader.PropertiesResourceReader;

public class ProperltyBuilderTest extends ProperltyBaseTest {

	@Test
	public void environmentVariablesKeysShouldBeIncludedAndNormalized() {

		final Map<String, String> envVar = System.getenv();
		assertFalse(envVar.isEmpty());

		final Properlty prop = Properlty.builder()
				.add(new EnvironmentVariablesReader())
				.add(new ToLowerCaseAndDotKeyReader(new EnvironmentVariablesReader()))
				.build();

		for (final Entry<String, String> envEntry : envVar.entrySet()) {

			final String key = envEntry.getKey();

			final String value = envEntry.getValue();
			assertEquals(value, prop.get(key).get());

			final String normalizedKey = key.toLowerCase().replace("_", ".");
			assertTrue(prop.get(normalizedKey).isPresent());
		}

	}

	@Test
	public void systemPropertiesShouldHaveHigherPriorityThanEnvVariables() {
		final Map<String, String> envVar = System.getenv();

		final String[] envVarKeys = getKeysWithUppercase(envVar, 2);

		final String envVarKey1 = envVarKeys[0];
		final String envVarKey1Normalized = envVarKey1.toLowerCase().replace("_", ".");
		final String envVarValue1 = envVar.get(envVarKey1);

		final String envVarKey2 = envVarKeys[1];
		final String envVarKey2Normalized = envVarKey2.toLowerCase().replace("_", ".");
		final String envVarValue2 = envVar.get(envVarKey2);

		try {
			// Override an environment variable with a system property
			final String overriddenValue = UUID.randomUUID().toString();
			System.setProperty(envVarKey1Normalized, overriddenValue);

			final Properlty prop = Properlty.builder()
					.add(new EnvironmentVariablesReader())
					.add(new ToLowerCaseAndDotKeyReader(new EnvironmentVariablesReader()))
					.add(new SystemPropertiesReader())
					.build();

			assertEquals(overriddenValue, prop.get(envVarKey1Normalized).get());
			assertEquals(envVarValue1, prop.get(envVarKey1).get());

			assertEquals(envVarValue2, prop.get(envVarKey2Normalized).get());
			assertEquals(envVarValue2, prop.get(envVarKey2).get());

		} finally {
			System.clearProperty(envVarKey1Normalized);
		}
	}

	@Test
	public void envVariablesShouldHaveHigherPriorityThanCustomProperties() {

		final Map<String, String> envVar = System.getenv();
		assertTrue(envVar.size() >= 1);

		final String[] envVarKeys = getKeysWithUppercase(envVar, 1);

		final String envVarKey1 = envVarKeys[0];
		final String envVarKey1Normalized = envVarKey1.toLowerCase().replace("_", ".");
		final String envVarValue1 = envVar.get(envVarKey1);

		final String customValue = UUID.randomUUID().toString();
		final String customKey2 = UUID.randomUUID().toString();

		final Properlty prop = Properlty.builder()
				.add(Properties.add(envVarKey1Normalized, customValue).add(customKey2, customValue))
				.add(new ToLowerCaseAndDotKeyReader(new EnvironmentVariablesReader()))
				.build();

		assertEquals(envVarValue1, prop.get(envVarKey1Normalized).get());
		assertEquals(customValue, prop.get(customKey2).get());
	}

	@Test
	public void shouldIgnoreFileNotFound() {

		final String key = UUID.randomUUID().toString();
		final Properlty prop = Properlty.builder()
				.add(Properties.add(key, "value"))
				.add(PropertiesResourceReader.build("NOT VALID PATH").ignoreNotFound(true).charset(StandardCharsets.UTF_8))
				.build();
		assertNotNull(prop);

		assertTrue(prop.get(key).isPresent());

	}

	@Test
	public void shouldFailIfFileNotFound() {
		try {
			Properlty.builder()
					.add("NOT VALID PATH")
					.build();
			fail("Should fail before");
		} catch (final RuntimeException e) {
			assertTrue(e.getCause() instanceof FileNotFoundException);
		}
	}

	@Test
	public void shouldConsiderFileAddPriority() {
		final Properlty prop = Properlty.builder()
					.add("file:./src/test/files/test1.properties")
					.add("classpath:resource1.properties")
					.add("classpath:inner/resource2.properties")
					.build();

		// from file:./src/test/files/test1.properties
		assertEquals( "firstvalue", prop.get("keyOne").get() );

		// from classpath:resource1.properties AND classpath:resource2.properties
		assertEquals( "resource2", prop.get("name").get() );

	}

	@Test
	public void shouldBePossibleTosetCustomPriority() {

		final String key = UUID.randomUUID().toString();
		try {
			System.setProperty(key, "SystemProperty");

			final Properlty prop = Properlty.builder()
					.add(Properties.add(key, "customReader"), Default.HIGHEST_PRIORITY )
					.build();
			assertNotNull(prop);

			assertEquals("customReader", prop.get(key).get());
		} finally {
			System.clearProperty(key);
		}

	}

	@Test
	public void shouldReplacePlaceHolders() {

		final String key1 = "key1";
		final String value1 = UUID.randomUUID().toString();
		try {
			System.setProperty(key1, value1);

			final Properlty prop = Properlty.builder()
					.add(new SystemPropertiesReader())
					.add(Properties.add("key2", "${${key3}}__${key1}"), Default.HIGHEST_PRIORITY )
					.add(Properties.add("key3", "key1"), Default.HIGHEST_PRIORITY )
					.build();
			assertNotNull(prop);

			assertEquals(value1 + "__" + value1, prop.get("key2").get());
		} finally {
			System.clearProperty(key1);
		}

	}

	@Test
	public void shouldReplaceUsingCustomDelimiters() {

		final String startDelimiter = "((";
		final String endDelimiter = "))";

			final Properlty prop = Properlty.builder()
					.delimiters(startDelimiter, endDelimiter)
					.add(Properties.add("key1", "value1").add("key2", "((((key3))))__((key1))"), Default.HIGHEST_PRIORITY )
					.add(Properties.add("key3", "key1"), Default.HIGHEST_PRIORITY )
					.build();
			assertNotNull(prop);

			assertEquals("value1__value1", prop.get("key2").get());

	}

	@Test
	public void shouldIgnoreNotResolvedPlaceHolders() {

			final Properlty prop = Properlty.builder()
					.ignoreUnresolvablePlaceholders(true)
					.add(Properties.add("key2", "${${key3}}__${key1}"), Default.HIGHEST_PRIORITY )
					.add(Properties.add("key3", "key1"), Default.HIGHEST_PRIORITY )
					.build();
			assertNotNull(prop);

			assertEquals("${key1}__${key1}", prop.get("key2").get());

	}

	@Test(expected=UnresolvablePlaceholdersException.class)
	public void shouldFailIfNotResolvedPlaceHolders() {
			Properlty.builder()
					.add(Properties.add("key2", "${${key3}}__${key1}"), Default.HIGHEST_PRIORITY )
					.add(Properties.add("key3", "key1"), Default.HIGHEST_PRIORITY )
					.build();
	}


	private String[] getKeysWithUppercase(Map<String, ?> map, int howMany) {
		final String[] keys = new String[howMany];

		int current = 0;

		for (final String entry : map.keySet()) {
			if (entry.matches(".*[A-Z].*")) {
				keys[current] = entry;
				current++;
				if (current >= howMany) {
					break;
				}
			};
		}

		if (current < howMany) {
			throw new RuntimeException("Not enough environment variables with at least an uppercase character! Needed [" + howMany + "] found [" + (current-1) + "]");
		}
		return keys;
	}

}
