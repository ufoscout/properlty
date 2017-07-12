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

	/**
	 * Return the property value associated with the given key.
	 *
	 * @param key
	 * @return
	 */
	public Optional<String> get(String key) {
		return Optional.ofNullable(properties.get(key));
	}

	/**
	 * Return the property value associated with the given key or the defaultValue if the key cannot be resolved.
	 *
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String get(String key, String defaultValue) {
		return get(key).orElse(defaultValue);
	}

	/**
	 * Return the property value associated with the given key.
	 *
	 * @param key
	 * @return
	 */
	public Optional<Integer> getInt(String key) {
		return Optional.ofNullable(properties.get(key)).map(Integer::parseInt);
	}

	/**
	 * Return the property value associated with the given key or the defaultValue if the key cannot be resolved.
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public int getInt(String key, int defaultValue) {
		final Optional<Integer> optional = getInt(key);
		if (optional.isPresent()) {
			return optional.get();
		}
		return defaultValue;
	}

	/**
	 * Return the property value associated with the given key.
	 *
	 * @param key
	 * @return
	 */
	public Optional<Double> getDouble(String key) {
		return Optional.ofNullable(properties.get(key)).map(Double::parseDouble);
	}

	/**
	 * Return the property value associated with the given key or the defaultValue if the key cannot be resolved.
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public double getDouble(String key, double defaultValue) {
		final Optional<Double> optional = getDouble(key);
		if (optional.isPresent()) {
			return optional.get();
		}
		return defaultValue;
	}

	/**
	 * Return the property value associated with the given key.
	 *
	 * @param key
	 * @return
	 */
	public Optional<Float> getFloat(String key) {
		return Optional.ofNullable(properties.get(key)).map(Float::parseFloat);
	}

	/**
	 * Return the property value associated with the given key or the defaultValue if the key cannot be resolved.
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public float getFloat(String key, float defaultValue) {
		final Optional<Float> optional = getFloat(key);
		if (optional.isPresent()) {
			return optional.get();
		}
		return defaultValue;
	}

	/**
	 * Return the property value associated with the given key.
	 *
	 * @param key
	 * @return
	 */
	public Optional<Long> getLong(String key) {
		return Optional.ofNullable(properties.get(key)).map(Long::parseLong);
	}

	/**
	 * Return the property value associated with the given key or the defaultValue if the key cannot be resolved.
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public long getLong(String key, long defaultValue) {
		final Optional<Long> optional = getLong(key);
		if (optional.isPresent()) {
			return optional.get();
		}
		return defaultValue;
	}

	/**
	 * Return the property value associated with the given key.
	 *
	 * @param key
	 * @return
	 */
	public <T extends Enum<T>> Optional<T> getEnum(String key, Class<T> type) {
		return Optional.ofNullable(properties.get(key)).map(value -> Enum.valueOf(type, value));
	}

	/**
	 * Return the property value associated with the given key or the defaultValue if the key cannot be resolved.
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public <T extends Enum<T>> T getEnum(String key, T defaultValue) {
		final Optional<T> optional = getEnum(key, (Class<T>) defaultValue.getClass());
		if (optional.isPresent()) {
			return optional.get();
		}
		return defaultValue;
	}

}
