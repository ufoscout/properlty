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

import java.util.Map;
import java.util.Optional;

public class Properlty {

	public static final int HIGHEST_PRIORITY = 0;
	public static final int DEFAULT_SYSTEM_PROPERTIES_PRIORITY = 100;
	public static final int DEFAULT_ENVIRONMENT_VARIABLES_PRIORITY = 1000;
	public static final int DEFAULT_PRIORITY = 10000;
	public static final int LOWEST_PRIORITY = Integer.MAX_VALUE;

	public static final String DEFAULT_START_DELIMITER = "${";
	public static final String DEFAULT_END_DELIMITER = "}";

	private final Map<String, String> properties;

	public static ProperltyBuilder builder() {
		return new ProperltyBuilder();
	}

	Properlty(Map<String, String> properties) {
		this.properties = properties;
	}

	public Optional<String> get(String key) {
		return Optional.ofNullable(properties.get(key));
	}

	public String get(String key, String defaultValue) {
		return get(key).orElse(defaultValue);
	}

	public Optional<Integer> getInt(String key) {
		return Optional.ofNullable(properties.get(key)).map(Integer::parseInt);
	}

	public int getInt(String key, int defaultValue) {
		final Optional<Integer> optional = getInt(key);
		if (optional.isPresent()) {
			return optional.get();
		}
		return defaultValue;
	}

	public Optional<Double> getDouble(String key) {
		return Optional.ofNullable(properties.get(key)).map(Double::parseDouble);
	}

	public double getDouble(String key, double defaultValue) {
		final Optional<Double> optional = getDouble(key);
		if (optional.isPresent()) {
			return optional.get();
		}
		return defaultValue;
	}

	public Optional<Float> getFloat(String key) {
		return Optional.ofNullable(properties.get(key)).map(Float::parseFloat);
	}

	public float getFloat(String key, float defaultValue) {
		final Optional<Float> optional = getFloat(key);
		if (optional.isPresent()) {
			return optional.get();
		}
		return defaultValue;
	}

	public Optional<Long> getLong(String key) {
		return Optional.ofNullable(properties.get(key)).map(Long::parseLong);
	}

	public long getLong(String key, long defaultValue) {
		final Optional<Long> optional = getLong(key);
		if (optional.isPresent()) {
			return optional.get();
		}
		return defaultValue;
	}


}
