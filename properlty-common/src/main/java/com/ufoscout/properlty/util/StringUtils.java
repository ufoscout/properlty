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
package com.ufoscout.properlty.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

public final class StringUtils {

	private StringUtils() {}

	/**
	 * Return true if the input {@link String} contains at least one token delimited by startDelimiter and endDelimiter
	 * @param input
	 * @param startDelimiter
	 * @param endDelimiter
	 * @return
	 */
	public static boolean hasTokens(String input, String startDelimiter, String endDelimiter) {
		final int start = input.indexOf(startDelimiter);
		final int end = input.lastIndexOf(endDelimiter);
		return start>=0 && end>= start;
	}

	/**
	 * Return the first token delimited by the startDelimiter and endDelimiter.
	 * Example:
	 * startDelimiter = "${"
	 * endDelimiter = "}"
	 *
	 * - input = "abdc" -> empty {@link Optional}
	 * - input = "${abcd}" -> "abcd"
	 * - input = "${${${abcd}}}" -> "abcd"
	 * - input = "aaa${abcd}aaa${efgh}" -> "abcd"
	 *
	 * @param input
	 * @param startDelimiter
	 * @param endDelimiter
	 * @return
	 */
	public static Optional<String> firstToken(String input, String startDelimiter, String endDelimiter) {
		String result = null;
		if ( hasTokens(input, startDelimiter, endDelimiter)) {
			while (input.contains(startDelimiter)) {
				final int start = input.indexOf(startDelimiter);
				input = input.substring(start + startDelimiter.length());
			    while (input.contains(endDelimiter)) {
				    final int end = input.indexOf(endDelimiter);
				    input = input.substring(0, end);
			    }
			}
			result = input;
		}
		return Optional.ofNullable(result);
	}

	/**
	 * Return all tokens delimited by the startDelimiter and endDelimiter.
	 * Example:
	 * startDelimiter = "${"
	 * endDelimiter = "}"
	 *
	 * - input = "abcd" -> {}
	 * - input = "${abcd}" -> {"abcd"}
	 * - input = "${${${abcd}}}" -> {"abcd"}
	 * - input = "aaa${abcd}aaa${efgh}" -> {"abcd","efgh"}
	 *
	 * @param input
	 * @param startDelimiter
	 * @param endDelimiter
	 * @param distinct whether to strip duplicated tokens
	 * @return
	 */
	public static List<String> allTokens(String input, String startDelimiter, String endDelimiter) {
		final List<String> tokens = new ArrayList<>();

		Optional<String> token = firstToken(input, startDelimiter, endDelimiter);
		while(token.isPresent()) {
			final String tokenValue = token.get();
			tokens.add(tokenValue);
			input = input.substring(input.indexOf(tokenValue) + tokenValue.length() + endDelimiter.length());
			token = firstToken(input, startDelimiter, endDelimiter);
		}

		return tokens;
	}

	/**
	 * Return all tokens delimited by the startDelimiter and endDelimiter.
	 * If distinct is true, it removes duplicated tokens.
	 * Example:
	 * startDelimiter = "${"
	 * endDelimiter = "}"
	 * distinc = true
	 *
	 * - input = "abcd" -> {}
	 * - input = "${abcd}" -> {"abcd"}
	 * - input = "${${${abcd}${abcd}}}" -> {"abcd"}
	 * - input = "__${abcd}__${efgh}__${abcd}" -> {"abcd","efgh"}
	 *
	 * @param input
	 * @param startDelimiter
	 * @param endDelimiter
	 * @param distinct whether to strip duplicated tokens
	 * @return
	 */
	public static List<String> allTokens(String input, String startDelimiter, String endDelimiter, boolean distinct) {
		final List<String> tokens = allTokens(input, startDelimiter, endDelimiter);
		if (distinct) {
			return new ArrayList<>( new LinkedHashSet<>(tokens) );
		}
		return tokens;
	}

}
