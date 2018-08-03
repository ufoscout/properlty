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

import com.ufoscout.properlty.ProperltyBaseTest;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import static org.junit.Assert.*;

public class EnvironmentVariablesReaderTest extends ProperltyBaseTest {

	@Test
	public void shouldReturnUnresolvableEnvironmentVariables() {
		final Map<String, PropertyValue> var = new EnvironmentVariablesReader().read();
		assertNotNull(var);
		assertFalse(var.isEmpty());

		var.forEach((key, value) -> {
			assertFalse(value.isResolvable());
		});
	}

	@Test
	public void shouldReplaceInEnvironmentVariableKeys() {
		Supplier<Map<String, String>> envSupplier = () -> {
			Map<String, String> envs = new ConcurrentHashMap<>();
			envs.put("ENV_ONE_KEY_ONE", "ENV_ONE_VALUE_ONE");
			return envs;
		};
		final Map<String, PropertyValue> var = new EnvironmentVariablesReader(envSupplier)
				.replace("_", ".")
				.read();

		assertNotNull(var);
		assertFalse(var.isEmpty());

		assertFalse(var.containsKey("ENV_ONE_KEY_ONE"));
		assertTrue(var.containsKey("ENV.ONE.KEY.ONE"));
		assertEquals("ENV_ONE_VALUE_ONE", var.get("ENV.ONE.KEY.ONE").getValue());

	}

}
