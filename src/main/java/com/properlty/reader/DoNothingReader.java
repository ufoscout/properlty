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

import java.util.Map;

/**
 * A {@link Reader} that always return the same map
 *
 * @author Francesco Cina
 *
 */
public class DoNothingReader implements Reader {

	private final Map<String, String> properties;

	public DoNothingReader(Map<String, String> properties) {
		this.properties = properties;
	}

	@Override
	public Map<String, String> read() {
		return properties;
	}

}
