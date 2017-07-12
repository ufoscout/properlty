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
package com.properlty;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class ProperltyTest extends ProperltyBaseTest {

	@Test
	public void shouldReturnEmptyOptionalString() {
		final Map<String, String> properties = new HashMap<>();

		properties.put("key.one", "value.one");
		properties.put("key.two", "value.two");

		final Properlty prop = new Properlty(properties);

		assertFalse(prop.get("key.three").isPresent());
	}

	@Test
	public void shouldReturnOptionalString() {
		final Map<String, String> properties = new HashMap<>();

		properties.put("key.one", "value.one");
		properties.put("key.two", "value.two");

		final Properlty prop = new Properlty(properties);

		assertEquals("value.one", prop.get("key.one").get());
		assertEquals("value.two", prop.get("key.two").get());
	}

	@Test
	public void shouldReturnDefaultString() {
		final Map<String, String> properties = new HashMap<>();

		properties.put("key.one", "value.one");

		final Properlty prop = new Properlty(properties);

		assertEquals("value.one", prop.get("key.one", "default"));
		assertEquals("default", prop.get("key.two", "default"));
	}

	@Test
	public void shouldReturnEmptyOptionalInteger() {
		final Map<String, String> properties = new HashMap<>();

		properties.put("key.one", "value.one");
		properties.put("key.two", "value.two");

		final Properlty prop = new Properlty(properties);

		assertFalse(prop.getInt("key.three").isPresent());
	}


	@Test
	public void shouldReturnOptionalInteger() {
		final Map<String, String> properties = new HashMap<>();

		properties.put("key.one", "123");
		properties.put("key.two", "1000000");

		final Properlty prop = new Properlty(properties);

		assertEquals(123, prop.getInt("key.one").get().intValue());
		assertEquals(1000000, prop.getInt("key.two").get().intValue());
	}

	@Test
	public void shouldReturnDefaultInteger() {
		final Map<String, String> properties = new HashMap<>();

		properties.put("key.one", "1");

		final Properlty prop = new Properlty(properties);

		assertEquals(1, prop.getInt("key.one", 10));
		assertEquals(10, prop.getInt("key.two", 10));

	}

	@Test(expected=NumberFormatException.class)
	public void shouldThrowExceptionParsingWrongInteger() {
		final Map<String, String> properties = new HashMap<>();

		properties.put("key.one", "not a number");

		final Properlty prop = new Properlty(properties);

		prop.getInt("key.one");

	}

	@Test
	public void shouldReturnEmptyOptionalDouble() {
		final Map<String, String> properties = new HashMap<>();

		properties.put("key.one", "value.one");
		properties.put("key.two", "value.two");

		final Properlty prop = new Properlty(properties);

		assertFalse(prop.getDouble("key.three").isPresent());
	}


	@Test
	public void shouldReturnOptionalDouble() {
		final Map<String, String> properties = new HashMap<>();

		properties.put("key.one", "123");
		properties.put("key.two", "1000000");

		final Properlty prop = new Properlty(properties);

		assertEquals(123d, prop.getDouble("key.one").get().doubleValue(), 0.1d);
		assertEquals(1000000d, prop.getDouble("key.two").get().doubleValue(), 0.1d);
	}

	@Test
	public void shouldReturnDefaultDouble() {
		final Map<String, String> properties = new HashMap<>();

		properties.put("key.one", "1");

		final Properlty prop = new Properlty(properties);

		assertEquals(1d, prop.getDouble("key.one", 1.1111d), 0.1d);
		assertEquals(10d, prop.getDouble("key.two", 10d), 0.1d);

	}

	@Test(expected=NumberFormatException.class)
	public void shouldThrowExceptionParsingWrongDouble() {
		final Map<String, String> properties = new HashMap<>();

		properties.put("key.one", "not a number");

		final Properlty prop = new Properlty(properties);

		prop.getDouble("key.one");

	}

	@Test
	public void shouldReturnEmptyOptionalFloat() {
		final Map<String, String> properties = new HashMap<>();

		properties.put("key.one", "value.one");
		properties.put("key.two", "value.two");

		final Properlty prop = new Properlty(properties);

		assertFalse(prop.getFloat("key.three").isPresent());
	}


	@Test
	public void shouldReturnOptionalFloat() {
		final Map<String, String> properties = new HashMap<>();

		properties.put("key.one", "123");
		properties.put("key.two", "1000000");

		final Properlty prop = new Properlty(properties);

		assertEquals(123f, prop.getFloat("key.one").get().floatValue(), 0.1f);
		assertEquals(1000000f, prop.getFloat("key.two").get().floatValue(), 0.1f);
	}

	@Test
	public void shouldReturnDefaultfloat() {
		final Map<String, String> properties = new HashMap<>();

		properties.put("key.one", "1");

		final Properlty prop = new Properlty(properties);

		assertEquals(1f, prop.getFloat("key.one", 10), 0.1f);
		assertEquals(10f, prop.getFloat("key.two", 10), 0.1f);

	}

	@Test(expected=NumberFormatException.class)
	public void shouldThrowExceptionParsingWrongFloat() {
		final Map<String, String> properties = new HashMap<>();

		properties.put("key.one", "not a number");

		final Properlty prop = new Properlty(properties);

		prop.getFloat("key.one");

	}

	@Test
	public void shouldReturnEmptyOptionalLong() {
		final Map<String, String> properties = new HashMap<>();

		properties.put("key.one", "value.one");
		properties.put("key.two", "value.two");

		final Properlty prop = new Properlty(properties);

		assertFalse(prop.getLong("key.three").isPresent());
	}


	@Test
	public void shouldReturnOptionalLong() {
		final Map<String, String> properties = new HashMap<>();

		properties.put("key.one", "123");
		properties.put("key.two", "1000000");

		final Properlty prop = new Properlty(properties);

		assertEquals(123l, prop.getLong("key.one").get().longValue());
		assertEquals(1000000l, prop.getLong("key.two").get().longValue());
	}

	@Test
	public void shouldReturnDefaultLong() {
		final Map<String, String> properties = new HashMap<>();

		properties.put("key.one", "1");

		final Properlty prop = new Properlty(properties);

		assertEquals(1l, prop.getLong("key.one", 10l));
		assertEquals(10l, prop.getLong("key.two", 10l));

	}

	@Test(expected=NumberFormatException.class)
	public void shouldThrowExceptionParsingWrongLong() {
		final Map<String, String> properties = new HashMap<>();

		properties.put("key.one", "not a number");

		final Properlty prop = new Properlty(properties);

		prop.getLong("key.one");

	}
}
