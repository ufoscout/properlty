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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ufoscout.properlty.exception.UnresolvablePlaceholdersException;
import com.ufoscout.properlty.reader.PropertyValue;
import com.ufoscout.properlty.reader.Reader;
import com.ufoscout.properlty.util.StringUtils;

/**
 *
 * A decorator that scrolls all values in a map and recursively replaces
 * placeholders with the appropriate value.
 *
 * @author Francesco Cina
 *
 */
public class ReplacerDecoratorReader extends DecoratorReader {

	private final String startDelimiter;
	private final String endDelimiter;
	private final String defaultValueSeparator;
	private final boolean ignoreUnresolvablePlaceholders;
	private final boolean caseSensitiveKeys;

	public ReplacerDecoratorReader(Reader reader,
								   String startDelimiter,
								   String endDelimiter,
								   String defaultValueSeparator,
								   boolean ignoreUnresolvablePlaceholders,
								   boolean caseSensitive) {
		super(reader);
		this.startDelimiter = startDelimiter;
		this.endDelimiter = endDelimiter;
		this.defaultValueSeparator = defaultValueSeparator;
		this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
		this.caseSensitiveKeys = caseSensitive;
	}

	@Override
	protected Map<String, PropertyValue> apply(Map<String, PropertyValue> input) {
		final Map<String, PropertyValue> output = new LinkedHashMap<>();
		input.forEach((key, value) -> {
			output.put(getKey(key), value);
		});

		final Map<String, PropertyValue> valuesToBeReplacedMap = new LinkedHashMap<>();
		boolean valuesToBeReplaced = true;
		boolean valuesReplacedOnLastLoop = true;

		while (valuesReplacedOnLastLoop && valuesToBeReplaced) {
			valuesToBeReplaced = false;
			valuesToBeReplacedMap.clear();
			valuesReplacedOnLastLoop = false;

			for (final Entry<String, PropertyValue> entry : output.entrySet()) {
				final String key = entry.getKey();
				final PropertyValue value = entry.getValue();

				if (value.isResolvable()) {

					final List<String> tokens = StringUtils.allTokens(value.getValue(), startDelimiter, endDelimiter, true);

					if (!tokens.isEmpty()) {
						valuesToBeReplaced = true;
						valuesToBeReplacedMap.put(key, value);
					}

					for (final String token : tokens) {
						final PropertyValue tokenValue = output.get(getBaseValue(token, defaultValueSeparator));
						if (tokenValue!=null) {
							if (!StringUtils.hasTokens(tokenValue.getValue(), startDelimiter, endDelimiter)) {
								value.value( value.getValue().replace(startDelimiter + token + endDelimiter, tokenValue.getValue()) );
								valuesReplacedOnLastLoop = true;
							}
						} else if (hasDefaultValue(token, defaultValueSeparator)) {
							value.value( getDefaultValue(token, defaultValueSeparator) );
							output.put(key, value);
							valuesReplacedOnLastLoop = true;
						}
					};

				}
			};
		}

		if (!valuesToBeReplacedMap.isEmpty() && !ignoreUnresolvablePlaceholders) {

			final StringBuilder message = new StringBuilder("Unresolvable placeholders: \n");
			valuesToBeReplacedMap.forEach((key, value) -> {
				message.append("key: [");
				message.append(key);
				message.append("] value: [");
				message.append(value.getValue());
				message.append("]\n");
			});

			throw new UnresolvablePlaceholdersException(message.toString());
		}

		return output;
	}

	private boolean hasDefaultValue(String token, String defaultValueSeparator) {
		return token.indexOf(defaultValueSeparator) >= 0;
	}

	private String getBaseValue(String token, String defaultValueSeparator) {
		int index = token.indexOf(defaultValueSeparator);
		if (index >= 0) {
			token = token.substring(0, index);
		}
		return getKey(token);
	}

	private String getDefaultValue(String token, String defaultValueSeparator) {
		int index = token.indexOf(defaultValueSeparator);
		if (index >= 0) {
			return token.substring(index+1);
		}
		return "";
	}

	private String getKey(String key)  {
		if (caseSensitiveKeys) {
			return key;
		} else {
			return key.toLowerCase();
		}
	}

}
