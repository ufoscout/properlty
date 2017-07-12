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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;

import com.properlty.reader.DoNothingReader;
import com.properlty.reader.Reader;

public class ToLowerCaseAndDotKeyDecoratorReaderTest {

	@Test
	public void shouldLowerCaseInputMapWithoutSideEffect() {
		final Map<String, String> properties = new HashMap<>();

		properties.put("UPPER_CASE_KEY_ONE", UUID.randomUUID().toString());
		properties.put("lower_case_key_TWO", UUID.randomUUID().toString());
		properties.put("lower.case.key.three", UUID.randomUUID().toString());

		final Reader input = new DoNothingReader(properties);

		final Map<String, String> lowerCased = new ToLowerCaseAndDotKeyDecoratorReader(input).read();

		assertNotNull(lowerCased);
		assertFalse(lowerCased.isEmpty());

		assertTrue(lowerCased.containsKey("upper.case.key.one"));
		assertTrue(lowerCased.containsKey("lower.case.key.two"));
		assertTrue(lowerCased.containsKey("lower.case.key.three"));

		assertFalse(lowerCased.containsKey("UPPER_CASE_KEY_ONE"));
		assertFalse(lowerCased.containsKey("lower.case.key.TWO"));

		assertEquals( properties.get("UPPER_CASE_KEY_ONE") , lowerCased.get("upper.case.key.one") );
		assertEquals( properties.get("lower_case_key_TWO") , lowerCased.get("lower.case.key.two") );
		assertEquals( properties.get("lower.case.key.three") , lowerCased.get("lower.case.key.three") );

	}

}
