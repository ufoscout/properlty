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
package com.ufoscout.properlty.reader.decorator;

import java.util.HashMap;
import java.util.Map;

import com.ufoscout.properlty.reader.PropertyValue;
import com.ufoscout.properlty.reader.Reader;

/**
 * A {@link DecoratorReader} that turns each property key in lower case and replaces underscores with a dot.
 * This is mostly used to transform environment variables in a properties like format.
 *
 * @author Francesco Cina
 *
 */
@Deprecated
public class ToLowerCaseAndDotKeyReader extends DecoratorReader {

	public ToLowerCaseAndDotKeyReader(Reader reader) {
		super(reader);
	}

	@Override
	protected Map<String, PropertyValue> apply(Map<String, PropertyValue> input) {
		final Map<String, PropertyValue> output = new HashMap<>();
		input.forEach((key, value) -> {
			output.put(key.toLowerCase().replace("_", "."), value);
		});
		return output;
	}

}
