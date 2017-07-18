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

import com.ufoscout.properlty.reader.EnvironmentVariablesReader;
import com.ufoscout.properlty.reader.PropertiesResourceReader;
import com.ufoscout.properlty.reader.Reader;
import com.ufoscout.properlty.reader.SystemPropertiesReader;
import com.ufoscout.properlty.reader.decorator.PriorityQueueDecoratorReader;
import com.ufoscout.properlty.reader.decorator.ReplacerDecoratorReader;
import com.ufoscout.properlty.reader.decorator.ToLowerCaseAndDotKeyDecoratorReader;

public class ProperltyBuilder {

	private final int systemPropertiesPriority = Properlty.DEFAULT_SYSTEM_PROPERTIES_PRIORITY;
	private final int environmentVariablesPriority = Properlty.DEFAULT_ENVIRONMENT_VARIABLES_PRIORITY;
	private int defaultPriority = Properlty.DEFAULT_PRIORITY;

	private final PriorityQueueDecoratorReader reader = new PriorityQueueDecoratorReader();
	private String startDelimiter = Properlty.DEFAULT_START_DELIMITER;
	private String endDelimiter = Properlty.DEFAULT_END_DELIMITER;
	private boolean ignoreUnresolvablePlaceholders = false;

	ProperltyBuilder() {
		reader.add(new EnvironmentVariablesReader(), environmentVariablesPriority);
		reader.add(new ToLowerCaseAndDotKeyDecoratorReader(new EnvironmentVariablesReader()), environmentVariablesPriority);
		reader.add(new SystemPropertiesReader(), systemPropertiesPriority);
	}

	/**
	 * Return the default priority of readers added without explicitly
	 * declaring the priority.
	 * Default value is {@value Properlty#DEFAULT_PRIORITY}
	 * @return the default priority
	 */
	public int getDefaultPriority() {
		return defaultPriority;
	}

	/**
	 * Set the defaultPriority of Readers added without explicitly
	 * priority declaration.
	 *
	 * @param defaultPriority positive integer value; the higher the value the lower the priority. 0 is the highest priority.
	 * @return
	 */
	public ProperltyBuilder defaultPriority(int defaultPriority) {
		this.defaultPriority = defaultPriority;
		return this;
	}

	/**
	 * Add a new property {@link Reader} with the default priority.
	 * If two or more {@link Reader}s have the same priority, the last added has the highest priority among them.
	 *
	 * @param reader
	 * @return
	 */
	public ProperltyBuilder add(Reader reader) {
		return add(reader, defaultPriority);
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
	 * @param reader
	 * @return
	 */
	public ProperltyBuilder add(String resourcePath) {
		return add(resourcePath, defaultPriority);
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
		return new Properlty( new ReplacerDecoratorReader(reader, startDelimiter, endDelimiter, ignoreUnresolvablePlaceholders).read() );
	}

	/**
	 * Return the end delimiter of the placeholders.
	 * Default value is {@value Properlty#DEFAULT_END_DELIMITER}
	 *
	 * @return the end delimiter
	 */
	public String getEndDelimiter() {
		return endDelimiter;
	}

	/**
	 * Return the start delimiter of the placeholders.
	 * Default value is {@value Properlty#DEFAULT_START_DELIMITER}
	 *
	 * @return the start delimiter
	 */
	public String getStartDelimiter() {
		return startDelimiter;
	}

	/**
	 *
	 * Set the start and end placeholder delimiters.
	 * Default are {@value Properlty#DEFAULT_START_DELIMITER} and {@value Properlty#DEFAULT_END_DELIMITER}
	 *
	 * @param startDelimiter the startDelimiter to set
	 */
	public ProperltyBuilder delimiters(String startDelimiter, String endDelimiter) {
		this.startDelimiter = startDelimiter;
		this.endDelimiter = endDelimiter;
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

}
