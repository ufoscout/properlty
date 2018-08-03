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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Return a {@link Map} with all values from OS environment variables.
 *
 * @author Francesco Cina
 *
 */
public class EnvironmentVariablesReader implements Reader {

	private final Map<String, String> replaceMap = new ConcurrentHashMap<>();
	private final Supplier<Map<String, String>> envSupplier;

	public EnvironmentVariablesReader() {
		this(() -> System.getenv());
	}

	EnvironmentVariablesReader(Supplier<Map<String, String>> envSupplier) {
		this.envSupplier = envSupplier;
	}

	@Override
	public Map<String, PropertyValue> read() {
		return envSupplier.get().entrySet().stream()
		        .collect(Collectors.toMap(
		                e -> getKey(e.getKey()),
		                e -> PropertyValue.of(e.getValue()).resolvable(false)
		            ));
	}

	/**
	 * Replace characters from the key.
	 *
	 * @param from
	 * @param to
	 * @return
	 */
	public EnvironmentVariablesReader replace(String from, String to) {
		replaceMap.put(from, to);
		return this;
	}

	private String getKey(String key) {
		for (Map.Entry<String, String> entry : replaceMap.entrySet()) {
			key = key.replace(entry.getKey(), entry.getValue());
		}
		return key;
	}


}
