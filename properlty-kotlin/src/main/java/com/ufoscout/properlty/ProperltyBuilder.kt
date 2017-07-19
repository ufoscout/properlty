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

import com.ufoscout.properlty.reader.EnvironmentVariablesReader
import com.ufoscout.properlty.reader.PropertiesResourceReader
import com.ufoscout.properlty.reader.Reader
import com.ufoscout.properlty.reader.SystemPropertiesReader
import com.ufoscout.properlty.reader.decorator.PriorityQueueDecoratorReader
import com.ufoscout.properlty.reader.decorator.ReplacerDecoratorReader
import com.ufoscout.properlty.reader.decorator.ToLowerCaseAndDotKeyDecoratorReader

class ProperltyBuilder internal constructor() {

    private val systemPropertiesPriority = Properlty.DEFAULT_SYSTEM_PROPERTIES_PRIORITY
    private val environmentVariablesPriority = Properlty.DEFAULT_ENVIRONMENT_VARIABLES_PRIORITY

    /**
     * Return the default priority of readers added without explicitly
     * declaring the priority.
     * Default value is [Properlty.DEFAULT_PRIORITY]
     * @return the default priority
     */
    var defaultPriority = Properlty.DEFAULT_PRIORITY
        private set

    private val reader = PriorityQueueDecoratorReader()
    /**
     * Return the start delimiter of the placeholders.
     * Default value is [Properlty.DEFAULT_START_DELIMITER]

     * @return the start delimiter
     */
    var startDelimiter = Properlty.DEFAULT_START_DELIMITER
        private set
    /**
     * Return the end delimiter of the placeholders.
     * Default value is [Properlty.DEFAULT_END_DELIMITER]

     * @return the end delimiter
     */
    var endDelimiter = Properlty.DEFAULT_END_DELIMITER
        private set
    private var ignoreUnresolvablePlaceholders = false

    init {
        reader.add(EnvironmentVariablesReader(), environmentVariablesPriority)
        reader.add(ToLowerCaseAndDotKeyDecoratorReader(EnvironmentVariablesReader()), environmentVariablesPriority)
        reader.add(SystemPropertiesReader(), systemPropertiesPriority)
    }

    /**
     * Set the defaultPriority of Readers added without explicitly
     * priority declaration.

     * @param defaultPriority positive integer value; the higher the value the lower the priority. 0 is the highest priority.
     * *
     * @return
     */
    fun defaultPriority(defaultPriority: Int): ProperltyBuilder {
        this.defaultPriority = defaultPriority
        return this
    }

    /**
     * Add a new property [Reader].
     * If two or more [Reader]s have the same priority, the last added has the highest priority among them.

     * @param reader
     * *
     * @return
     */
    @JvmOverloads fun add(reader: Reader, priority: Int = defaultPriority): ProperltyBuilder {
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

     * @param resourcePath
     * *
     * @param priority
     * *
     * @return
     */
    @JvmOverloads fun add(resourcePath: String, priority: Int = defaultPriority): ProperltyBuilder {
        return add(PropertiesResourceReader.build(resourcePath), priority)
    }

    /**
     * build a [Properlty] instance that contains the evaluated properties.
     * @return
     */
    fun build(): Properlty {
        return Properlty(ReplacerDecoratorReader(reader, startDelimiter, endDelimiter, ignoreUnresolvablePlaceholders).read())
    }

    /**

     * Set the start and end placeholder delimiters.
     * Default are [Properlty.DEFAULT_START_DELIMITER] and [Properlty.DEFAULT_END_DELIMITER]

     * @param startDelimiter the startDelimiter to set
     */
    fun delimiters(startDelimiter: String, endDelimiter: String): ProperltyBuilder {
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

}
