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

import com.ufoscout.properlty.reader.PropertiesResourceReader
import com.ufoscout.properlty.reader.Reader
import com.ufoscout.properlty.reader.decorator.PriorityQueueDecoratorReader
import com.ufoscout.properlty.reader.decorator.ReplacerDecoratorReader
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

class ProperltyBuilder internal constructor() {

    private val reader = PriorityQueueDecoratorReader()
    /**
     * Return the start delimiter of the placeholders.
     * Default value is [Default.START_DELIMITER]

     * @return the start delimiter
     */
    var startDelimiter = Default.START_DELIMITER
        private set
    /**
     * Return the end delimiter of the placeholders.
     * Default value is [Default.END_DELIMITER]

     * @return the end delimiter
     */
    var endDelimiter = Default.END_DELIMITER
        private set

    /**
     * Set the separator for the default value.
     * Default value is [Default.DEFAULT_DEFAULT_VALUE_SEPARATOR]

     * @return the default value separator
     */
    var defaultValueSeparator = Default.DEFAULT_DEFAULT_VALUE_SEPARATOR
        private set

    private var ignoreUnresolvablePlaceholders = false

    private var caseSensitive = true

    /*
    init {
        reader.add(EnvironmentVariablesReader(), environmentVariablesPriority)
        reader.add(ToLowerCaseAndDotKeyReader(EnvironmentVariablesReader()), environmentVariablesPriority)
        reader.add(SystemPropertiesReader(), systemPropertiesPriority)
    }
    */

    /**
     * Add a new property [Reader].
     * If two or more [Reader]s have the same priority, the last added has the highest priority among them.

     * @param reader
     * *
     * @return
     */
    fun add(reader: Reader, priority: Int = Default.DEFAULT_PRIORITY): ProperltyBuilder {
        this.reader.add(reader, priority)
        return this
    }

    /**
     * Add a new [PropertiesResourceReader] to fetch the properties from the a specific resource on the file system
     * or on the classpath.
     * The resourcePath can be in the form:

     * - ./path/file : path of a file in the file system
     * - file:./path/file : same as previous case, a path of a file in the file system
     * - classpath:/path/file : path of a resource in the classpath

     * If two or more [Reader]s have the same priority, the last added has the highest priority among them.

     * @param resourcePath the resource path
     * @param ignoreNotFound whether to ignore if the resource is not found. Default is false.
     * @param charset the resource character encoding. Default is [StandardCharsets.UTF_8]
     * @param priority
     * *
     * @return
     */
    fun add(resourcePath: String, ignoreNotFound: Boolean = false, charset: Charset = StandardCharsets.UTF_8, priority: Int = Default.DEFAULT_PRIORITY): ProperltyBuilder {
        return add(PropertiesResourceReader.build(resourcePath).ignoreNotFound(ignoreNotFound).charset(charset), priority)
    }

    /**
     * build a [Properlty] instance that contains the evaluated properties.
     * @return
     */
    fun build(): Properlty {
        return Properlty(caseSensitive, ReplacerDecoratorReader(reader, startDelimiter, endDelimiter, defaultValueSeparator, ignoreUnresolvablePlaceholders, caseSensitive).read())
    }

    /**

     * Set the start and end placeholder delimiters.
     * Default are [Default.START_DELIMITER] and [Default.END_DELIMITER]

     * @param startDelimiter the startDelimiter to set
     */
    fun delimiters(startDelimiter: String = Default.START_DELIMITER, endDelimiter: String = Default.END_DELIMITER): ProperltyBuilder {
        this.startDelimiter = startDelimiter
        this.endDelimiter = endDelimiter
        return this
    }

    /**
     * Whether to ignore not resolvable placeholders.
     * Default is false.

     * @param ignoreUnresolvablePlaceholders
     * *
     * @return
     */
    fun ignoreUnresolvablePlaceholders(ignoreUnresolvablePlaceholders: Boolean): ProperltyBuilder {
        this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders
        return this
    }

    /**
     * Whether the key are case sensitive.
     * Default is true.
     *
     * @param caseSensitive
     * @return
     */
    fun caseSensitive(caseSensitive: Boolean): ProperltyBuilder {
        this.caseSensitive = caseSensitive
        return this
    }

}
