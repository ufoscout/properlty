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
package com.properlty.reader;

/**
 * The value and attributes of a property
 *
 * @author Francesco Cina
 *
 */
public class PropertyValue {

	private String value = "";
	private boolean resolvable = true;

	public static PropertyValue of(String value) {
		return new PropertyValue().value(value);
	}

	/**
	 * The property value
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * Set the property value
	 * @param value the value to set
	 */
	public PropertyValue value(String value) {
		this.value = value;
		return this;
	}
	/**
	 * Whether this property contains placeholders that should be resolved. Default is true.
	 *
	 * @return the replaceable
	 */
	public boolean isResolvable() {
		return resolvable;
	}
	/**
	 * Whether this property contains placeholders that should be resolved. Default is true.
	 *
	 * @param resolvable
	 */
	public PropertyValue resolvable(boolean resolvable) {
		this.resolvable = resolvable;
		return this;
	}



}
