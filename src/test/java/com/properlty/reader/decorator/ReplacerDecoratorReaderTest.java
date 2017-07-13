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
package com.properlty.reader.decorator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import com.properlty.ProperltyBaseTest;
import com.properlty.exception.UnresolvablePlaceholdersException;
import com.properlty.reader.DoNothingReader;
import com.properlty.reader.PropertyValue;

public class ReplacerDecoratorReaderTest extends ProperltyBaseTest {

	@Test
	public void shouldResolveSimpleKeys() {
		final Map<String, String> properties = new HashMap<>();

		properties.put("key.one", "${key.two}");
		properties.put("key.two", "value.two");

		final boolean ignoreNotResolvable = false;
		final Map<String, PropertyValue> output = new ReplacerDecoratorReader(DoNothingReader.of(properties), "${", "}", ignoreNotResolvable).read();
		assertNotNull(output);

		assertEquals(2, output.size());

		assertEquals("value.two", output.get("key.one").getValue());
		assertEquals("value.two", output.get("key.two").getValue());

	}

	@Test
	public void shouldNotResolveUnresolvableKeys() {
		final Map<String, PropertyValue> properties = new HashMap<>();

		properties.put("key.unresolvable", PropertyValue.of("${key.two}").resolvable(false));
		properties.put("key.one", PropertyValue.of("${key.two}"));
		properties.put("key.two", PropertyValue.of("value.two"));

		final boolean ignoreNotResolvable = false;
		final Map<String, PropertyValue> output = new ReplacerDecoratorReader(new DoNothingReader(properties), "${", "}", ignoreNotResolvable).read();
		assertNotNull(output);

		assertEquals(3, output.size());

		assertEquals("${key.two}", output.get("key.unresolvable").getValue());
		assertEquals("value.two", output.get("key.one").getValue());
		assertEquals("value.two", output.get("key.two").getValue());

	}

	@Test
	public void shouldFailIfNotResolvablePlaceholders() {
		final Map<String, String> properties = new HashMap<>();

		properties.put("key.1", "${key.4}");
		properties.put("key.2", "${key.1}");
		properties.put("key.3", "${key.2}");
		properties.put("key.4", "${key.3}");

		final boolean ignoreNotResolvable = false;
		try {
			new ReplacerDecoratorReader(DoNothingReader.of(properties), "${", "}", ignoreNotResolvable).read();
			fail();
		} catch (final UnresolvablePlaceholdersException e) {
			final String message = e.getMessage();
			getLogger().error(message, e);
			assertTrue(message.contains("key: [key.1] value: [${key.4}]"));
			assertTrue(message.contains("key: [key.2] value: [${key.1}]"));
			assertTrue(message.contains("key: [key.3] value: [${key.2}]"));
			assertTrue(message.contains("key: [key.4] value: [${key.3}]"));
		}
	}

	@Test
	public void shouldNotLoopOnSelfReferencingKeys() {
		final Map<String, String> properties = new HashMap<>();

		properties.put("key.one", "${key.one}");

		final boolean ignoreNotResolvable = true;
		final Map<String, PropertyValue> output = new ReplacerDecoratorReader(DoNothingReader.of(properties), "${", "}", ignoreNotResolvable).read();
		assertNotNull(output);

		assertEquals(1, output.size());

		assertEquals("${key.one}", output.get("key.one").getValue());

	}

	@Test
	public void shouldNotResolveCircularReferencingKeys() {
		final Map<String, String> properties = new HashMap<>();

		properties.put("key.one", "${key.two}");
		properties.put("key.two", "${key.one}");

		final boolean ignoreNotResolvable = true;
		final Map<String, PropertyValue> output = new ReplacerDecoratorReader(DoNothingReader.of(properties), "${", "}", ignoreNotResolvable).read();
		assertNotNull(output);

		assertEquals(2, output.size());

		assertEquals("${key.two}", output.get("key.one").getValue());
		assertEquals("${key.one}", output.get("key.two").getValue());

	}

	@Test
	public void shouldRecursivelyResolveKeys() {
		final Map<String, String> properties = new TreeMap<>();

		properties.put("key.1", "${key.2}");
		properties.put("key.2", "${key.3} world!");
		properties.put("key.3", "Hello");
		properties.put("key.4", "${key.2}");

		final boolean ignoreNotResolvable = false;
		final Map<String, PropertyValue> output = new ReplacerDecoratorReader(DoNothingReader.of(properties), "${", "}", ignoreNotResolvable).read();
		assertNotNull(output);

		assertEquals(4, output.size());

		assertEquals("Hello world!", output.get("key.1").getValue());
		assertEquals("Hello world!", output.get("key.2").getValue());
		assertEquals("Hello", output.get("key.3").getValue());
		assertEquals("Hello world!", output.get("key.4").getValue());

	}

	@Test
	public void shouldRecursivelyResolveDynamicKeys() {
		final Map<String, String> properties = new TreeMap<>();

		properties.put("key.1", "${${key.2}}");
		properties.put("key.2", "${key.3}");
		properties.put("key.3", "key.4");
		properties.put("key.4", "Hello world!");

		final boolean ignoreNotResolvable = false;
		final Map<String, PropertyValue> output = new ReplacerDecoratorReader(DoNothingReader.of(properties), "${", "}", ignoreNotResolvable).read();
		assertNotNull(output);

		assertEquals(4, output.size());

		assertEquals("Hello world!", output.get("key.1").getValue());
		assertEquals("key.4", output.get("key.2").getValue());
		assertEquals("key.4", output.get("key.3").getValue());
		assertEquals("Hello world!", output.get("key.4").getValue());

	}

}
