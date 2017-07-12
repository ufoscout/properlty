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
package com.properlty.reader.decorator;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.properlty.exception.UnresolvablePlaceholdersException;
import com.properlty.reader.Reader;
import com.properlty.util.StringUtils;

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
	private final boolean ignoreUnresolvablePlaceholders;

	public ReplacerDecoratorReader(Reader reader, String startDelimiter, String endDelimiter, boolean ignoreUnresolvablePlaceholders) {
		super(reader);
		this.startDelimiter = startDelimiter;
		this.endDelimiter = endDelimiter;
		this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
	}

	@Override
	protected Map<String, String> apply(Map<String, String> input) {
		final Map<String, String> output = new LinkedHashMap<>(input);

		final Map<String, String> valuesToBeReplacedMap = new LinkedHashMap<>(input);
		boolean valuesToBeReplaced = true;
		boolean valuesReplacedOnLastLoop = true;

		while (valuesReplacedOnLastLoop && valuesToBeReplaced) {
			valuesToBeReplaced = false;
			valuesToBeReplacedMap.clear();
			valuesReplacedOnLastLoop = false;

			for (final Entry<String, String> entry : output.entrySet()) {
				final String key = entry.getKey();
				final String value = entry.getValue();

				final List<String> tokens = StringUtils.allTokens(value, startDelimiter, endDelimiter, true);

				valuesToBeReplaced = valuesToBeReplaced || !tokens.isEmpty();
				valuesToBeReplacedMap.put(key, value);

				for (final String token : tokens) {
					if (output.containsKey(token)) {
						final String tokenValue = output.get(token);
						if (!StringUtils.hasTokens(tokenValue, startDelimiter, endDelimiter)) {
							final String replacedValue = value.replace(startDelimiter + token + endDelimiter, tokenValue);
							valuesReplacedOnLastLoop = true;
							output.put(key, replacedValue);
						}
					}
				};
			};
		}

		if (valuesToBeReplaced && !ignoreUnresolvablePlaceholders) {

			final StringBuilder message = new StringBuilder("Unresolvable placeholders: \n");
			valuesToBeReplacedMap.forEach((key, value) -> {
				message.append("key: [");
				message.append(key);
				message.append("] value: [");
				message.append(value);
				message.append("]\n");
			});

			throw new UnresolvablePlaceholdersException(message.toString());
		}

		return output;
	}

}
