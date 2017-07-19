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
package com.ufoscout.properlty.reader.decorator;

import com.ufoscout.properlty.ProperltyBaseTest;
import com.ufoscout.properlty.exception.UnresolvablePlaceholdersException;
import com.ufoscout.properlty.reader.Properties;
import com.ufoscout.properlty.reader.PropertyValue;
import com.ufoscout.properlty.reader.ProprammaticPropertiesReader;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class ReplacerDecoratorReaderTest extends ProperltyBaseTest {

	@Test
	public void shouldResolveSimpleKeys() {
		final ProprammaticPropertiesReader properties = Properties.add("key.one", "${key.two}");
		properties.add("key.two", "value.two");

		final boolean ignoreNotResolvable = false;
		final Map<String, PropertyValue> output = new ReplacerDecoratorReader(properties, "${", "}", ignoreNotResolvable).read();
		assertNotNull(output);

		assertEquals(2, output.size());

		assertEquals("value.two", output.get("key.one").getValue());
		assertEquals("value.two", output.get("key.two").getValue());

	}

	@Test
	public void shouldNotResolveUnresolvableKeys() {
		final ProprammaticPropertiesReader properties = Properties.add("key.unresolvable", PropertyValue.of("${key.two}").resolvable(false));
		properties.add("key.one", PropertyValue.of("${key.two}"));
		properties.add("key.two", PropertyValue.of("value.two"));

		final boolean ignoreNotResolvable = false;
		final Map<String, PropertyValue> output = new ReplacerDecoratorReader(properties, "${", "}", ignoreNotResolvable).read();
		assertNotNull(output);

		assertEquals(3, output.size());

		assertEquals("${key.two}", output.get("key.unresolvable").getValue());
		assertEquals("value.two", output.get("key.one").getValue());
		assertEquals("value.two", output.get("key.two").getValue());

	}

	@Test
	public void shouldFailIfNotResolvablePlaceholders() {
		final ProprammaticPropertiesReader properties = Properties.add("key.1", "${key.4}");
		properties.add("key.2", "${key.1}");
		properties.add("key.3", "${key.2}");
		properties.add("key.4", "${key.3}");

		final boolean ignoreNotResolvable = false;
		try {
			new ReplacerDecoratorReader(properties, "${", "}", ignoreNotResolvable).read();
			fail();
		} catch (final UnresolvablePlaceholdersException e) {
			final String message = e.getMessage();
			assertTrue(message.contains("key: [key.1] value: [${key.4}]"));
			assertTrue(message.contains("key: [key.2] value: [${key.1}]"));
			assertTrue(message.contains("key: [key.3] value: [${key.2}]"));
			assertTrue(message.contains("key: [key.4] value: [${key.3}]"));
		}
	}

	@Test
	public void shouldNotLoopOnSelfReferencingKeys() {
		final ProprammaticPropertiesReader properties = Properties.add("key.one", "${key.one}");

		final boolean ignoreNotResolvable = true;
		final Map<String, PropertyValue> output = new ReplacerDecoratorReader(properties, "${", "}", ignoreNotResolvable).read();
		assertNotNull(output);

		assertEquals(1, output.size());

		assertEquals("${key.one}", output.get("key.one").getValue());

	}

	@Test
	public void shouldNotResolveCircularReferencingKeys() {
		final ProprammaticPropertiesReader properties = Properties.add("key.one", "${key.two}");
		properties.add("key.two", "${key.one}");

		final boolean ignoreNotResolvable = true;
		final Map<String, PropertyValue> output = new ReplacerDecoratorReader(properties, "${", "}", ignoreNotResolvable).read();
		assertNotNull(output);

		assertEquals(2, output.size());

		assertEquals("${key.two}", output.get("key.one").getValue());
		assertEquals("${key.one}", output.get("key.two").getValue());

	}

	@Test
	public void shouldRecursivelyResolveKeys() {
		final ProprammaticPropertiesReader properties = Properties.add("key.1", "${key.2}");
		properties.add("key.2", "${key.3} world!");
		properties.add("key.3", "Hello");
		properties.add("key.4", "${key.2}");

		final boolean ignoreNotResolvable = false;
		final Map<String, PropertyValue> output = new ReplacerDecoratorReader(properties, "${", "}", ignoreNotResolvable).read();
		assertNotNull(output);

		assertEquals(4, output.size());

		assertEquals("Hello world!", output.get("key.1").getValue());
		assertEquals("Hello world!", output.get("key.2").getValue());
		assertEquals("Hello", output.get("key.3").getValue());
		assertEquals("Hello world!", output.get("key.4").getValue());

	}

	@Test
	public void shouldRecursivelyResolveDynamicKeys() {
		final ProprammaticPropertiesReader properties = Properties.add("key.1", "${${key.2}}");
		properties.add("key.2", "${key.3}");
		properties.add("key.3", "key.4");
		properties.add("key.4", "Hello world!");

		final boolean ignoreNotResolvable = false;
		final Map<String, PropertyValue> output = new ReplacerDecoratorReader(properties, "${", "}", ignoreNotResolvable).read();
		assertNotNull(output);

		assertEquals(4, output.size());

		assertEquals("Hello world!", output.get("key.1").getValue());
		assertEquals("key.4", output.get("key.2").getValue());
		assertEquals("key.4", output.get("key.3").getValue());
		assertEquals("Hello world!", output.get("key.4").getValue());

	}

}
