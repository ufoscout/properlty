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
package com.ufoscout.properlty.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;

import com.ufoscout.properlty.ProperltyBaseTest;
import com.ufoscout.properlty.exception.ResourceNotFoundException;
import com.ufoscout.properlty.reader.PropertiesResourceReader;
import com.ufoscout.properlty.reader.PropertyValue;
import com.ufoscout.properlty.util.FileUtils;

public class PropertiesResourceReaderTest extends ProperltyBaseTest {

	@Test
	public void shouldThrowExceptionWhenFileNotFound() {
		try {
			final boolean ignoreNotFound = false;
			final String path = UUID.randomUUID().toString();
			PropertiesResourceReader.build(path).ignoreNotFound(ignoreNotFound).read();
			fail("It should have thrown an exception for file not found");
		} catch (final ResourceNotFoundException e) {
			assertTrue(e.getCause() instanceof FileNotFoundException);
		}
	}

	@Test
	public void shouldThrowExceptionWhenFileNotFoundInClasspath() {
		try {
			final boolean ignoreNotFound = false;
			final String path = FileUtils.CLASSPATH_PREFIX + UUID.randomUUID().toString();
			PropertiesResourceReader.build(path).ignoreNotFound(ignoreNotFound).read();
			fail("It should have thrown an exception for file not found");
		} catch (final ResourceNotFoundException e) {
			assertTrue(e.getCause() instanceof FileNotFoundException);
		}
	}

	@Test
	public void shouldIgnoreFileNotFoundIfNeeded() {
			final boolean ignoreNotFound = true;
			final String path = UUID.randomUUID().toString();
			final Map<String, PropertyValue> properties = PropertiesResourceReader.build(path).ignoreNotFound(ignoreNotFound).read();
			assertNotNull(properties);
			assertTrue(properties.isEmpty());
	}

	@Test
	public void shouldReadProperiesFromFile() {
			final boolean ignoreNotFound = false;
			final String path = "./src/test/files/test1.properties";
			final Map<String, PropertyValue> properties = PropertiesResourceReader.build(path).ignoreNotFound(ignoreNotFound).read();
			assertNotNull(properties);
			assertEquals(2, properties.size());

			assertTrue(properties.containsKey("keyOne"));
			assertEquals("firstvalue", properties.get("keyOne").getValue());

			assertTrue(properties.containsKey("keyTwo"));
			assertEquals("second VALUE", properties.get("keyTwo").getValue());
	}

	@Test
	public void shouldReadProperiesFromClasspath() {
			final boolean ignoreNotFound = false;
			final String path = FileUtils.CLASSPATH_PREFIX + "resource1.properties";
			final Map<String, PropertyValue> properties = PropertiesResourceReader.build(path).ignoreNotFound(ignoreNotFound).read();
			assertNotNull(properties);
			assertFalse(properties.isEmpty());

			assertTrue(properties.containsKey("name"));
			assertEquals("resource1", properties.get("name").getValue());
	}

	@Test
	public void shouldReadEmptyProperiesFile() {
			final boolean ignoreNotFound = false;
			final String path = "./src/test/files/empty.properties";
			final Map<String, PropertyValue> properties = PropertiesResourceReader.build(path).ignoreNotFound(ignoreNotFound).read();
			assertNotNull(properties);
			assertTrue(properties.isEmpty());
	}

}
