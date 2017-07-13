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

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Reader} to programmatically manage properties.
 *
 * @author Francesco Cina
 *
 */
public class ProprammaticPropertiesReader implements Reader {

	private final Map<String, PropertyValue> properties = new HashMap<>();

	ProprammaticPropertiesReader() {}

	@Override
	public Map<String, PropertyValue> read() {
		return properties;
	}

	/**
	 * Add a new property
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	public ProprammaticPropertiesReader add(String key, String value) {
		properties.put(key, PropertyValue.of(value));
		return this;
	}

	/**
	 * Add a new property
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	public ProprammaticPropertiesReader add(String key, PropertyValue value) {
		properties.put(key, value);
		return this;
	}

}
