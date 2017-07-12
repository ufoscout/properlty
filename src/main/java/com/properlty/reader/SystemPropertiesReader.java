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
import java.util.Map.Entry;
import java.util.Properties;

/**
 * Return a {@link Map} with all existing system properties.
 * A system property can be set dynamically or statically.
 *
 * To set a system property statically, use the -D option of the java command:
 * java -DpropertyName=propertyValue MyApp
 *
 * To set a system property dynamically, call the java.lang.System.setProperty method in your code:
 * System.setProperty(propertyName,"propertyValue");
 *
 * @author Francesco Cina
 *
 */
public class SystemPropertiesReader implements Reader {

	@Override
	public Map<String, String> read() {
		final Map<String, String> properties = new HashMap<>();
		final Properties systemProperties = System.getProperties();
		for(final Entry<Object, Object> x : systemProperties.entrySet()) {
		    properties.put((String)x.getKey(), (String)x.getValue());
		}
		return properties;
	}

}
