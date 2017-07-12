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

import com.properlty.reader.EnvironmentVariablesReader;
import com.properlty.reader.PropertiesResourceFileReader;
import com.properlty.reader.Reader;
import com.properlty.reader.SystemPropertiesReader;
import com.properlty.reader.decorator.PriorityQueueDecoratorReader;
import com.properlty.reader.decorator.ReplacerDecoratorReader;
import com.properlty.reader.decorator.ToLowerCaseAndDotKeyDecoratorReader;

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

	public int getDefaultPriority() {
		return defaultPriority;
	}

	public ProperltyBuilder defaultPriority(int defaultPriority) {
		this.defaultPriority = defaultPriority;
		return this;
	}

	public ProperltyBuilder add(Reader reader) {
		return add(reader, defaultPriority);
	}

	public ProperltyBuilder add(Reader reader, int priority) {
		this.reader.add(reader, priority);
		return this;
	}

	public ProperltyBuilder add(String resourcePath) {
		return add(resourcePath, defaultPriority);
	}

	public ProperltyBuilder add(String resourcePath, int priority) {
		return add(PropertiesResourceFileReader.build(resourcePath), priority);
	}

	public Properlty build() {
		return new Properlty( new ReplacerDecoratorReader(reader, startDelimiter, endDelimiter, ignoreUnresolvablePlaceholders).read() );
	}

	/**
	 * @return the endDelimiter
	 */
	public String getEndDelimiter() {
		return endDelimiter;
	}

	/**
	 * @return the startDelimiter
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
