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
package com.ufoscout.properlty;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ufoscout.properlty.reader.PropertyValue;

public class Properlty {

	private final Map<String, PropertyValue> properties;

	public static ProperltyBuilder builder() {
		return new ProperltyBuilder();
	}

	Properlty(Map<String, PropertyValue> properties) {
		this.properties = properties;
	}

	/**
	 * Return the property value associated with the given key.
	 *
	 * @param key
	 * @return
	 */
	public Optional<String> get(String key) {
		final PropertyValue value = properties.get(key);
		if (value != null) {
			return Optional.ofNullable(value.getValue());
		}
		return Optional.empty();
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
	 * Return the property value associated with the given key and apply the map function to it.
	 *
	 * @param key
	 * @param map
	 * @return
	 */
	public <T> Optional<T> get(String key, Function<String, T> map) {
		return get(key).map(map);
	}

	/**
	 * Return the property value associated with the given key and apply the map function to it.
	 * The defaultValue if the key cannot be resolved.
	 *
	 * @param key
	 * @param defaultValue
	 * @param map
	 * @return
	 */
	public <T> T get(String key, T defaultValue, Function<String, T> map) {
		return get(key).map(map).orElse(defaultValue);
	}

	/**
	 * Return the property value associated with the given key.
	 *
	 * @param key
	 * @return
	 */
	public Optional<Integer> getInt(String key) {
		return get(key).map(Integer::parseInt);
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
	public Optional<Boolean> getBoolean(String key) {
		return get(key).map(value -> {
			if ( "true".equalsIgnoreCase(value) )
				return true;
			else if ( "false".equalsIgnoreCase(value) )
				return false;
			else
				throw new RuntimeException("Cannot parse boolean value: [" + value+ "]");
		});
	}

	/**
	 * Return the property value associated with the given key or the defaultValue if the key cannot be resolved.
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public boolean getBoolean(String key, boolean defaultValue) {
		final Optional<Boolean> optional = getBoolean(key);
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
		return get(key).map(Double::parseDouble);
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
		return get(key).map(Float::parseFloat);
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
		return get(key).map(Long::parseLong);
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
		return get(key).map(value -> Enum.valueOf(type, value));
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

	/**
	 * Return the property value associated with the given key split with the default separator.
	 * The default separator is {@value Default#LIST_SEPARATOR}}
	 *
	 * @param key
	 * @return
	 */
	public String[] getArray(String key) {
		return getArray(key, Default.LIST_SEPARATOR);
	}

	/**
	 * Return the property value associated with the given key split with the specific separator.
	 *
	 * @param key
	 * @param separator
	 * @return
	 */
	public String[] getArray(String key, String separator) {
		return get(key).map(value -> value.split(separator)).orElse(new String[0]);
	}

	/**
	 * Return the property value associated with the given key split with the default separator.
	 * The default separator is {@value Default#LIST_SEPARATOR}}
	 *
	 * @param key
	 * @return
	 */
	public List<String> getList(String key) {
		return Arrays.asList(getArray(key));
	}

	/**
	 * Return the property value associated with the given key split with the specific separator.
	 *
	 * @param key
	 * @param separator
	 * @return
	 */
	public List<String> getList(String key, String separator) {
		return Arrays.asList(getArray(key, separator));
	}

	/**
	 * Return the property value associated with the given key split with the default separator
	 * and apply the map function to elements in the resulting list.
	 * The default separator is {@value Default#LIST_SEPARATOR}}
	 *
	 * @param key
	 * @return
	 */
	public <T> List<T> getList(String key, Function<String, T> map) {
		return getList(key, Default.LIST_SEPARATOR, map);
	}

	/**
	 * Return the property value associated with the given key split with the specific separator
	 * and apply the map function to elements in the resulting list.
	 *
	 * @param key
	 * @param separator
	 * @return
	 */
	public <T> List<T> getList(String key, String separator, Function<String, T> map) {
		return getList(key, separator).stream().map(map).collect(Collectors.toList());
	}
}
