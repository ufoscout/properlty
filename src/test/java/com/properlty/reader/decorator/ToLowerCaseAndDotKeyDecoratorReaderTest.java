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

import java.util.Map;

import org.junit.Test;

import com.properlty.reader.Properties;
import com.properlty.reader.PropertyValue;
import com.properlty.reader.Reader;

public class ToLowerCaseAndDotKeyDecoratorReaderTest {

	@Test
	public void shouldLowerCaseInputMapWithoutSideEffect() {

		final Reader input = Properties.add("UPPER_CASE_KEY_ONE", "value1")
				.add("lower_case_key_TWO", "value2")
				.add("lower.case.key.three", "value3");

		final Map<String, PropertyValue> lowerCased = new ToLowerCaseAndDotKeyDecoratorReader(input).read();

		assertNotNull(lowerCased);
		assertFalse(lowerCased.isEmpty());

		assertTrue(lowerCased.containsKey("upper.case.key.one"));
		assertTrue(lowerCased.containsKey("lower.case.key.two"));
		assertTrue(lowerCased.containsKey("lower.case.key.three"));

		assertFalse(lowerCased.containsKey("UPPER_CASE_KEY_ONE"));
		assertFalse(lowerCased.containsKey("lower.case.key.TWO"));

		assertEquals( "value1" , lowerCased.get("upper.case.key.one").getValue() );
		assertEquals( "value2" , lowerCased.get("lower.case.key.two").getValue() );
		assertEquals( "value3" , lowerCased.get("lower.case.key.three").getValue() );

	}

}
