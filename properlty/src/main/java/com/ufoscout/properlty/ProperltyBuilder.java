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

import com.ufoscout.properlty.reader.PropertiesResourceReader;
import com.ufoscout.properlty.reader.Reader;
import com.ufoscout.properlty.reader.decorator.PriorityQueueDecoratorReader;
import com.ufoscout.properlty.reader.decorator.ReplacerDecoratorReader;

public class ProperltyBuilder {

	private final PriorityQueueDecoratorReader reader = new PriorityQueueDecoratorReader();
	private String startDelimiter = Default.START_DELIMITER;
	private String endDelimiter = Default.END_DELIMITER;
	private String defaultValueSeparator = Default.DEFAULT_DEFAULT_VALUE_SEPARATOR;
	private boolean ignoreUnresolvablePlaceholders = false;
	private boolean caseSensitive = true;

	ProperltyBuilder() {
		/*
		reader.add(new EnvironmentVariablesReader(), environmentVariablesPriority);
		reader.add(new ToLowerCaseAndDotKeyReader(new EnvironmentVariablesReader()), environmentVariablesPriority);
		reader.add(new SystemPropertiesReader(), systemPropertiesPriority);
		*/
	}

	/**
	 * Add a new property {@link Reader} with the default priority.
	 * If two or more {@link Reader}s have the same priority, the last added has the highest priority among them.
	 *
	 * @param reader
	 * @return
	 */
	public ProperltyBuilder add(Reader reader) {
		return add(reader, Default.DEFAULT_PRIORITY);
	}

	/**
	 * Add a new property {@link Reader}.
	 * If two or more {@link Reader}s have the same priority, the last added has the highest priority among them.
	 *
	 * @param reader
	 * @return
	 */
	public ProperltyBuilder add(Reader reader, int priority) {
		this.reader.add(reader, priority);
		return this;
	}

	/**
	 * Add a new {@link PropertiesResourceReader} to fetch the properties from the a specific resource on the file system
	 * or on the classpath.
	 * The resourcePath can be in the form:
	 *
	 * - ./path/file : path of a file in the file system
	 * - file:./path/file : same as previous case, a path of a file in the file system
	 * - classpath:/path/file : path of a resource in the classpath
	 *
	 * If two or more {@link Reader}s have the same priority, the last added has the highest priority among them.
	 *
	 * @param resourcePath
	 * @return
	 */
	public ProperltyBuilder add(String resourcePath) {
		return add(resourcePath, Default.DEFAULT_PRIORITY);
	}

	/**
	 * Add a new {@link PropertiesResourceReader} to fetch the properties from the a specific resource on the file system
	 * or on the classpath.
	 * The resourcePath can be in the form:
	 *
	 * - ./path/file : path of a file in the file system
	 * - file:./path/file : same as previous case, a path of a file in the file system
	 * - classpath:/path/file : path of a resource in the classpath
	 *
	 * If two or more {@link Reader}s have the same priority, the last added has the highest priority among them.
	 *
	 * @param resourcePath
	 * @param priority
	 * @return
	 */
	public ProperltyBuilder add(String resourcePath, int priority) {
		return add(PropertiesResourceReader.build(resourcePath), priority);
	}

	/**
	 * build a {@link Properlty} instance that contains the evaluated properties.
	 * @return
	 */
	public Properlty build() {
		return new Properlty( caseSensitive, new ReplacerDecoratorReader(reader, startDelimiter, endDelimiter, defaultValueSeparator, ignoreUnresolvablePlaceholders, caseSensitive).read() );
	}

	/**
	 * Return the end delimiter of the placeholders.
	 * Default value is {@value Default#END_DELIMITER}
	 *
	 * @return the end delimiter
	 */
	public String getEndDelimiter() {
		return endDelimiter;
	}

	/**
	 * Return the start delimiter of the placeholders.
	 * Default value is {@value Default#START_DELIMITER}
	 *
	 * @return the start delimiter
	 */
	public String getStartDelimiter() {
		return startDelimiter;
	}

	/**
	 *
	 * Set the start and end placeholder delimiters.
	 * Default are {@value Default#START_DELIMITER} and {@value Default#END_DELIMITER}
	 *
	 * @param startDelimiter the startDelimiter to set
	 */
	public ProperltyBuilder delimiters(String startDelimiter, String endDelimiter) {
		this.startDelimiter = startDelimiter;
		this.endDelimiter = endDelimiter;
		return this;
	}

	/**
	 * Set the default value separator.
	 * Default is {@value Default#DEFAULT_DEFAULT_VALUE_SEPARATOR}
	 *
	 * @param defaultValueSeparator the default value separator
	 */
	public ProperltyBuilder defaultValueSeparator(String defaultValueSeparator) {
		this.defaultValueSeparator = defaultValueSeparator;
		return this;
	}

	/**
	 * Whether to ignore not resolvable placeholders.
	 * Default is false.
	 *
	 * @param ignoreUnresolvablePlaceholders
	 * @return
	 */
	public ProperltyBuilder ignoreUnresolvablePlaceholders(boolean ignoreUnresolvablePlaceholders) {
		this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
		return this;
	}

	/**
	 * Whether the key are case sensitive.
	 * Default is true.
	 *
	 * @param caseSensitive
	 * @return
	 */
	public ProperltyBuilder caseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
		return this;
	}

}
