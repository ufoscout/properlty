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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.ufoscout.properlty.ProperltyBaseTest;
import com.ufoscout.properlty.exception.UnresolvablePlaceholdersException;
import com.ufoscout.properlty.reader.ProgrammaticPropertiesReader;
import com.ufoscout.properlty.reader.Properties;
import com.ufoscout.properlty.reader.PropertyValue;
import java.util.Map;
import org.junit.Test;

public class ReplacerDecoratorReaderTest extends ProperltyBaseTest {

	@Test
	public void shouldResolveSimpleKeys() {
		final ProgrammaticPropertiesReader properties = Properties.add("key.one", "${key.two}");
		properties.add("key.two", "value.two");

		final boolean ignoreNotResolvable = false;
		final boolean caseSensitive = true;
		final Map<String, PropertyValue> output = new ReplacerDecoratorReader(properties, "${", "}", ":", ignoreNotResolvable, caseSensitive).read();
		assertNotNull(output);

		assertEquals(2, output.size());

		assertEquals("value.two", output.get("key.one").getValue());
		assertEquals("value.two", output.get("key.two").getValue());

	}

	@Test
	public void shouldNotResolveUnresolvableKeys() {
		final ProgrammaticPropertiesReader properties = Properties.add("key.unresolvable", PropertyValue.of("${key.two}").resolvable(false));
		properties.add("key.one", PropertyValue.of("${key.two}"));
		properties.add("key.two", PropertyValue.of("value.two"));

		final boolean ignoreNotResolvable = false;
		final boolean caseSensitive = true;
		final Map<String, PropertyValue> output = new ReplacerDecoratorReader(properties, "${", "}", ":", ignoreNotResolvable, caseSensitive).read();
		assertNotNull(output);

		assertEquals(3, output.size());

		assertEquals("${key.two}", output.get("key.unresolvable").getValue());
		assertEquals("value.two", output.get("key.one").getValue());
		assertEquals("value.two", output.get("key.two").getValue());

	}

	@Test
	public void shouldFailIfNotResolvablePlaceholders() {
		final ProgrammaticPropertiesReader properties = Properties.add("key.1", "${key.4}");
		properties.add("key.2", "${key.1}");
		properties.add("key.3", "${key.2}");
		properties.add("key.4", "${key.3}");

		final boolean ignoreNotResolvable = false;
		final boolean caseSensitive = true;
		try {
			new ReplacerDecoratorReader(properties, "${", "}", ":", ignoreNotResolvable, caseSensitive).read();
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
		final ProgrammaticPropertiesReader properties = Properties.add("key.one", "${key.one}");

		final boolean ignoreNotResolvable = true;
		final boolean caseSensitive = true;
		final Map<String, PropertyValue> output = new ReplacerDecoratorReader(properties, "${", "}", ":", ignoreNotResolvable, caseSensitive).read();
		assertNotNull(output);

		assertEquals(1, output.size());

		assertEquals("${key.one}", output.get("key.one").getValue());

	}

	@Test
	public void shouldNotResolveCircularReferencingKeys() {
		final ProgrammaticPropertiesReader properties = Properties.add("key.one", "${key.two}");
		properties.add("key.two", "${key.one}");

		final boolean ignoreNotResolvable = true;
		final boolean caseSensitive = true;
		final Map<String, PropertyValue> output = new ReplacerDecoratorReader(properties, "${", "}", ":", ignoreNotResolvable, caseSensitive).read();
		assertNotNull(output);

		assertEquals(2, output.size());

		assertEquals("${key.two}", output.get("key.one").getValue());
		assertEquals("${key.one}", output.get("key.two").getValue());

	}

	@Test
	public void shouldRecursivelyResolveKeys() {
		final ProgrammaticPropertiesReader properties = Properties.add("key.1", "${key.2}");
		properties.add("key.2", "${key.3} world!");
		properties.add("key.3", "Hello");
		properties.add("key.4", "${key.2}");

		final boolean ignoreNotResolvable = false;
		final boolean caseSensitive = true;
		final Map<String, PropertyValue> output = new ReplacerDecoratorReader(properties, "${", "}", ":", ignoreNotResolvable, caseSensitive).read();
		assertNotNull(output);

		assertEquals(4, output.size());

		assertEquals("Hello world!", output.get("key.1").getValue());
		assertEquals("Hello world!", output.get("key.2").getValue());
		assertEquals("Hello", output.get("key.3").getValue());
		assertEquals("Hello world!", output.get("key.4").getValue());

	}

	@Test
	public void shouldRecursivelyResolveDynamicKeys() {
		final ProgrammaticPropertiesReader properties = Properties.add("key.1", "${${key.2}}");
		properties.add("key.2", "${key.3}");
		properties.add("key.3", "key.4");
		properties.add("key.4", "Hello world!");

		final boolean ignoreNotResolvable = false;
		final boolean caseSensitive = true;
		final Map<String, PropertyValue> output = new ReplacerDecoratorReader(properties, "${", "}", ":", ignoreNotResolvable, caseSensitive).read();
		assertNotNull(output);

		assertEquals(4, output.size());

		assertEquals("Hello world!", output.get("key.1").getValue());
		assertEquals("key.4", output.get("key.2").getValue());
		assertEquals("key.4", output.get("key.3").getValue());
		assertEquals("Hello world!", output.get("key.4").getValue());

	}

	@Test
	public void shouldDetectDefaultValue() {
		final ProgrammaticPropertiesReader properties = Properties
				.add("key.one", "${value1:defaultValue1}")
				.add("key.two", "${value2:defaultValue2}");

		boolean ignoreNotResolvable = true;
		final boolean caseSensitive = true;
		final Map<String, PropertyValue> output =
				new ReplacerDecoratorReader(properties, "${", "}", ":", ignoreNotResolvable, caseSensitive).read();

		assertNotNull(output);

		assertEquals(2, output.size());

		assertEquals("defaultValue1", output.get("key.one").getValue());
		assertEquals("defaultValue2", output.get("key.two").getValue());

	}

	@Test
	public void shouldDetectDefaultValueAndReplaceRecursively() {
		final ProgrammaticPropertiesReader properties = Properties
		.add("key.one", "${value1:defaultValue1}")
		.add("key.two", "${key.one}");

		boolean ignoreNotResolvable = true;
		final boolean caseSensitive = true;
		final Map<String, PropertyValue> output =
				new ReplacerDecoratorReader(properties, "${", "}", ":", ignoreNotResolvable, caseSensitive).read();

		assertNotNull(output);

		assertEquals(2, output.size());

		assertEquals("defaultValue1", output.get("key.one").getValue());
		assertEquals("defaultValue1", output.get("key.two").getValue());

	}

	@Test
	public void shouldDetectDefaultValueAndReplaceRecursively2() {
		final ProgrammaticPropertiesReader properties = Properties
		.add("key.one", "${value1:defaultValue1}")
		.add("key.two", "${key.one:defaultValue2}");

		boolean ignoreNotResolvable = true;
		final boolean caseSensitive = true;
		final Map<String, PropertyValue> output =
				new ReplacerDecoratorReader(properties, "${", "}", ":", ignoreNotResolvable, caseSensitive).read();

		assertNotNull(output);

		assertEquals(2, output.size());

		assertEquals("defaultValue1", output.get("key.one").getValue());
		assertEquals("defaultValue1", output.get("key.two").getValue());

	}

	@Test
	public void shouldDetectNestedDefaultValues() {
		final ProgrammaticPropertiesReader properties = Properties
		.add("key.one", "${${value1:${key.two}}")
		.add("key.two", "${key.three}")
		.add("key.three", "value");

		boolean ignoreNotResolvable = true;
		final boolean caseSensitive = true;
		final Map<String, PropertyValue> output =
				new ReplacerDecoratorReader(properties, "${", "}", ":", ignoreNotResolvable, caseSensitive).read();

		assertNotNull(output);

		assertEquals(3, output.size());

		assertEquals("value", output.get("key.one").getValue());
		assertEquals("value", output.get("key.two").getValue());
		assertEquals("value", output.get("key.three").getValue());

	}


	@Test
	public void shouldMatchPlaceholdersNotSensitiveCase() {
		final ProgrammaticPropertiesReader properties = Properties
				.add("key.ONE", "${value1:defaultValue1}")
				.add("keY.TWO", "${KEY.one:defaultValue2}");

		boolean ignoreNotResolvable = true;
		final boolean caseSensitive = false;
		final Map<String, PropertyValue> output =
				new ReplacerDecoratorReader(properties, "${", "}", ":", ignoreNotResolvable, caseSensitive).read();

		assertNotNull(output);

		assertEquals(2, output.size());

		assertEquals("defaultValue1", output.get("key.one").getValue());
		assertEquals("defaultValue1", output.get("key.two").getValue());

	}

}
