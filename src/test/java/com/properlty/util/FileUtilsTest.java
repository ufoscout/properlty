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
package com.properlty.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;

import com.google.common.io.CharStreams;
import com.properlty.ProperltyBaseTest;

public class FileUtilsTest extends ProperltyBaseTest {

	@Test
	public void shouldReadFileFromRelativePath() throws FileNotFoundException, IOException {
		try (InputStream is = FileUtils.getStream("./src/test/files/test1.properties")) {
			final String content = toString(is);
			assertNotNull(content);
			assertTrue(content.contains("keyOne"));
		}
	}

	@Test
	public void shouldReadFileFromRelativePathWithPrefix() throws FileNotFoundException, IOException {
		try (InputStream is = FileUtils.getStream("file:src/test/files/test1.properties")) {
			final String content = toString(is);
			assertNotNull(content);
			assertTrue(content.contains("keyOne"));
		}
	}

	@Test
	public void shouldReadFileFromAbsolutePath() throws FileNotFoundException, IOException {

		final String absolutePath = new File("src/test/files/test1.properties").getAbsolutePath();
		getLogger().info("File absolute path is: [{}]", absolutePath);

		try (InputStream is = FileUtils.getStream(absolutePath)) {
			final String content = toString(is);
			assertNotNull(content);
			assertTrue(content.contains("keyOne"));
		}
	}

	@Test
	public void shouldReadFileFromAbsolutePathWithPrefix() throws FileNotFoundException, IOException {

		final String absolutePath = new File("src/test/files/test1.properties").getAbsolutePath();
		getLogger().info("File absolute path is: [{}]", absolutePath);

		try (InputStream is = FileUtils.getStream("file:" + absolutePath)) {
			final String content = toString(is);
			assertNotNull(content);
			assertTrue(content.contains("keyOne"));
		}
	}

	@Test
	public void shouldReadFileFromClasspath() throws FileNotFoundException, IOException {
		try (InputStream is = FileUtils.getStream("classpath:resource1.properties")) {
			final String content = toString(is);
			assertNotNull(content);
			assertTrue(content.contains("name=resource1"));
		}
	}

	@Test
	public void shouldReadFileFromClasspathFolder() throws FileNotFoundException, IOException {
		try (InputStream is = FileUtils.getStream("classpath:./inner/resource2.properties")) {
			final String content = toString(is);
			assertNotNull(content);
			assertTrue(content.contains("name=resource2"));
		}
	}

	@Test
	public void shouldThrowFileNotFoundExceptionForMissingFileFromClasspath() throws FileNotFoundException, IOException {
		try {
			FileUtils.getStream("classpath:NOT_EXISTING_FILE");
			fail("Should have thrown a FileNotFoundException");
		} catch (final FileNotFoundException e) {
			assertTrue(e.getMessage().contains("NOT_EXISTING_FILE"));
		}
	}

	@Test
	public void shouldThrowFileNotFoundExceptionForMissingFile() throws FileNotFoundException, IOException {
		try {
			FileUtils.getStream("file:NOT_EXISTING_FILE");
			fail("Should have thrown a FileNotFoundException");
		} catch (final FileNotFoundException e) {
			assertTrue(e.getMessage().contains("NOT_EXISTING_FILE"));
		}
	}

	private String toString(java.io.InputStream is) throws IOException {
		return CharStreams.toString(new InputStreamReader(is));
	}

}
