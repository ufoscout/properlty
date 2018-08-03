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

import com.ufoscout.properlty.reader.PropertyValue
import java.util.*
import java.util.stream.Collectors

class Properlty internal constructor(private val caseSensitive: Boolean, private val properties: Map<String, PropertyValue>) {

    /**
     * Return the property value associated with the given key.

     * @param key
     * *
     * @return
     */
    operator fun get(key: String): String? {
        var key_ = if (caseSensitive) {
            key
        } else {
            key.toLowerCase()
        }
        return properties[key_]?.value
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
        return get(key) ?: defaultValue
    }

    /**
     * Return the property value associated with the given key and apply the map function to it.

     * @param key
     * *
     * @param map
     * *
     * @return
     */
    operator fun <T> get(key: String, map: (String) -> T): T? {
        val value = get(key)
        if (value!=null) {
            return map(value)
        }
        return null
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
        val value = get(key, map)
        return value ?: defaultValue
    }

    /**
     * Return the property value associated with the given key.

     * @param key
     * *
     * @return
     */
    fun getBoolean(key: String): Boolean? {
        val value = get(key)
        return if (value == null)
            value
        else if ("true".equals(value, ignoreCase = true))
            true
        else if ("false".equals(value, ignoreCase = true))
            false
        else
            throw RuntimeException("Cannot parse boolean value: [$value]")
    }

    /**
     * Return the property value associated with the given key or the defaultValue if the key cannot be resolved.
     * @param key
     * *
     * @param defaultValue
     * *
     * @return
     */
    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return getBoolean(key) ?: defaultValue
    }

    /**
     * Return the property value associated with the given key.

     * @param key
     * *
     * @return
     */
    fun getInt(key: String): Int? {
        return get(key, {java.lang.Integer.parseInt(it)})
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
        return getInt(key) ?: defaultValue
    }

    /**
     * Return the property value associated with the given key.

     * @param key
     * *
     * @return
     */
    fun getDouble(key: String): Double? {
        return get(key, {java.lang.Double.parseDouble(it)})
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
        return getDouble(key) ?: defaultValue
    }

    /**
     * Return the property value associated with the given key.

     * @param key
     * *
     * @return
     */
    fun getFloat(key: String): Float? {
        return get(key, {java.lang.Float.parseFloat(it)})
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
        return getFloat(key) ?: defaultValue
    }

    /**
     * Return the property value associated with the given key.

     * @param key
     * *
     * @return
     */
    fun getLong(key: String): Long? {
        return get(key, {java.lang.Long.parseLong(it)})
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
        return getLong(key) ?: defaultValue
    }

    /**
     * Return the property value associated with the given key.

     * @param key
     * *
     * @return
     */
    inline fun <reified T : Enum<T>> getEnum(key: String): T? {
        return get(key, {java.lang.Enum.valueOf<T>(T::class.java, it)})
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
        return get(key, {java.lang.Enum.valueOf<T>(defaultValue.javaClass, it)}) ?: defaultValue
    }

    /**
     * Return the property value associated with the given key split with the specific separator.

     * @param key
     * *
     * @param separator
     * *
     * @return
     */
    fun getArray(key: String, separator: String = Default.LIST_SEPARATOR): Array<String> {
        val value = get(key)
        if (value!=null) {
            return value.split(separator.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        }
        return arrayOf()
    }

    /**
     * Return the property value associated with the given key split with the specific separator.
     * The default separator is [Default.LIST_SEPARATOR]
     *
     * @param key
     * @param separator
     * @return
     */
    fun getList(key: String, separator: String = Default.LIST_SEPARATOR): List<String> {
        return Arrays.asList(*getArray(key, separator))
    }

    /**
     * Return the property value associated with the given key split with the default separator
     * and apply the map function to elements in the resulting list.
     * The default separator is [Default.LIST_SEPARATOR]

     * @param key
     * @return
     */
    fun <T> getList(key: String, map: (String) -> T): List<T> {
        return getList(key, Default.LIST_SEPARATOR).stream().map(map).collect(Collectors.toList())
    }

    /**
     * Return the property value associated with the given key split with the specific separator
     * and apply the map function to elements in the resulting list.

     * @param key
     * @param separator
     * @return
     */
    fun <T> getList(key: String, separator: String = Default.LIST_SEPARATOR, map: (String) -> T): List<T> {
        return getList(key, separator).stream().map(map).collect(Collectors.toList())
    }

    companion object {

        fun builder(): ProperltyBuilder {
            return ProperltyBuilder()
        }
    }
}
