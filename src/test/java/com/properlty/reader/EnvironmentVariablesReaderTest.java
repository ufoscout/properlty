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
package com.properlty.reader;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Test;

import com.properlty.ProperltyBaseTest;
import com.properlty.reader.EnvironmentVariablesReader;

public class EnvironmentVariablesReaderTest extends ProperltyBaseTest {

	@Test
	public void shouldReturnEnvironmentVariables() {
		final Map<String, String> var = new EnvironmentVariablesReader().read();
		assertNotNull(var);
		assertFalse(var.isEmpty());

		var.forEach((key, value) -> {
			getLogger().info("Found Environment variable {} = {}" ,key ,value);
		});
	}

}