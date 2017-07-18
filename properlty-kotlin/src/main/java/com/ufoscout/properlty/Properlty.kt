/*******************************************************************************
 * Copyright 2017 Francesco Cina'

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ufoscout.properlty

import java.util.Arrays
import java.util.Optional
import java.util.stream.Collectors

import com.ufoscout.properlty.reader.PropertyValue

class Properlty internal constructor(private val properties: Map<String, PropertyValue>) {

    /**
     * Return the property value associated with the given key.

     * @param key
     * *
     * @return
     */
    operator fun get(key: String): Optional<String> {
        val value = properties[key]
        if (value != null) {
            return Optional.ofNullable(value.value)
        }
        return Optional.empty<String>()
    }

    /**
     * Return the property value associated with the given key or the defaultValue if the key cannot be resolved.

     * @param key
     * *
     * @param defaultValue
     * *
     * @return
     */
    operator fun get(key: String, defaultValue: String): String {
        return get(key).orElse(defaultValue)
    }

    /**
     * Return the property value associated with the given key and apply the map function to it.

     * @param key
     * *
     * @param map
     * *
     * @return
     */
    operator fun <T> get(key: String, map: (String) -> T): Optional<T> {
        return get(key).map(map)
    }

    /**
     * Return the property value associated with the given key and apply the map function to it.
     * The defaultValue if the key cannot be resolved.

     * @param key
     * *
     * @param defaultValue
     * *
     * @param map
     * *
     * @return
     */
    operator fun <T> get(key: String, defaultValue: T, map: (String) -> T): T {
        return get(key).map(map).orElse(defaultValue)
    }

    /**
     * Return the property value associated with the given key.

     * @param key
     * *
     * @return
     */
    fun getInt(key: String): Optional<Int> {
        return get(key).map { Integer.parseInt(it) }
    }

    /**
     * Return the property value associated with the given key or the defaultValue if the key cannot be resolved.
     * @param key
     * *
     * @param defaultValue
     * *
     * @return
     */
    fun getInt(key: String, defaultValue: Int): Int {
        val optional = getInt(key)
        if (optional.isPresent) {
            return optional.get()
        }
        return defaultValue
    }

    /**
     * Return the property value associated with the given key.

     * @param key
     * *
     * @return
     */
    fun getDouble(key: String): Optional<Double> {
        return get(key).map { java.lang.Double.parseDouble(it) }
    }

    /**
     * Return the property value associated with the given key or the defaultValue if the key cannot be resolved.
     * @param key
     * *
     * @param defaultValue
     * *
     * @return
     */
    fun getDouble(key: String, defaultValue: Double): Double {
        val optional = getDouble(key)
        if (optional.isPresent) {
            return optional.get()
        }
        return defaultValue
    }

    /**
     * Return the property value associated with the given key.

     * @param key
     * *
     * @return
     */
    fun getFloat(key: String): Optional<Float> {
        return get(key).map { java.lang.Float.parseFloat(it) }
    }

    /**
     * Return the property value associated with the given key or the defaultValue if the key cannot be resolved.
     * @param key
     * *
     * @param defaultValue
     * *
     * @return
     */
    fun getFloat(key: String, defaultValue: Float): Float {
        val optional = getFloat(key)
        if (optional.isPresent) {
            return optional.get()
        }
        return defaultValue
    }

    /**
     * Return the property value associated with the given key.

     * @param key
     * *
     * @return
     */
    fun getLong(key: String): Optional<Long> {
        return get(key).map { java.lang.Long.parseLong(it) }
    }

    /**
     * Return the property value associated with the given key or the defaultValue if the key cannot be resolved.
     * @param key
     * *
     * @param defaultValue
     * *
     * @return
     */
    fun getLong(key: String, defaultValue: Long): Long {
        val optional = getLong(key)
        if (optional.isPresent) {
            return optional.get()
        }
        return defaultValue
    }

    /**
     * Return the property value associated with the given key.

     * @param key
     * *
     * @return
     */
    fun <T : Enum<T>> getEnum(key: String, type: Class<T>): Optional<T> {
        return get(key).map<T> { value -> java.lang.Enum.valueOf<T>(type, value) }
    }

    /**
     * Return the property value associated with the given key or the defaultValue if the key cannot be resolved.
     * @param key
     * *
     * @param defaultValue
     * *
     * @return
     */
    fun <T : Enum<T>> getEnum(key: String, defaultValue: T): T {
        val optional = getEnum(key, defaultValue.javaClass)
        if (optional.isPresent) {
            return optional.get()
        }
        return defaultValue
    }

    /**
     * Return the property value associated with the given key split with the specific separator.

     * @param key
     * *
     * @param separator
     * *
     * @return
     */
    @JvmOverloads fun getArray(key: String, separator: String = DEFAULT_LIST_SEPARATOR): Array<String> {
        return get(key).map { value -> value.split(separator.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray() }.orElse(arrayOf<String>())
    }

    /**
     * Return the property value associated with the given key split with the default separator.
     * The default separator is {@value #DEFAULT_LIST_SEPARATOR}}

     * @param key
     * *
     * @return
     */
    fun getList(key: String): List<String> {
        return Arrays.asList(*getArray(key))
    }

    /**
     * Return the property value associated with the given key split with the specific separator.

     * @param key
     * *
     * @param separator
     * *
     * @return
     */
    fun getList(key: String, separator: String): List<String> {
        return Arrays.asList(*getArray(key, separator))
    }

    /**
     * Return the property value associated with the given key split with the default separator
     * and apply the map function to elements in the resulting list.
     * The default separator is {@value #DEFAULT_LIST_SEPARATOR}}

     * @param key
     * *
     * @return
     */
    fun <T> getList(key: String, map: (String) -> T): List<T> {
        return getList(key, DEFAULT_LIST_SEPARATOR, map)
    }

    /**
     * Return the property value associated with the given key split with the specific separator
     * and apply the map function to elements in the resulting list.

     * @param key
     * *
     * @param separator
     * *
     * @return
     */
    fun <T> getList(key: String, separator: String, map: (String) -> T): List<T> {
        return getList(key, separator).stream().map(map).collect(Collectors.toList())
    }

    companion object {

        val HIGHEST_PRIORITY = 0
        val DEFAULT_SYSTEM_PROPERTIES_PRIORITY = 100
        val DEFAULT_ENVIRONMENT_VARIABLES_PRIORITY = 1000
        val DEFAULT_PRIORITY = 10000
        val LOWEST_PRIORITY = Integer.MAX_VALUE

        val DEFAULT_START_DELIMITER = "\${"
        val DEFAULT_END_DELIMITER = "}"
        val DEFAULT_LIST_SEPARATOR = ","

        fun builder(): ProperltyBuilder {
            return ProperltyBuilder()
        }
    }
}
/**
 * Return the property value associated with the given key split with the default separator.
 * The default separator is {@value #DEFAULT_LIST_SEPARATOR}}

 * @param key
 * *
 * @return
 */
