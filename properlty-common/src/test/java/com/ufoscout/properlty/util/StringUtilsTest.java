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

import com.ufoscout.properlty.ProperltyBaseTest;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StringUtilsTest extends ProperltyBaseTest {

	private final String startDelimiter = "${";
	private final String endDelimiter = "}";

	@Test
	public void shouldReturnEmpty() {
		final String input = "";
		assertTrue(StringUtils.allTokens(input, startDelimiter, endDelimiter).isEmpty());
	}


	@Test
	public void shouldReturnEmpty2() {
		final String input = "asfasdfasfdasfdasf_${_asdasd";
		assertTrue(StringUtils.allTokens(input, startDelimiter, endDelimiter).isEmpty());
	}

	@Test
	public void shouldReturnEmpty3() {
		final String input = "asfasdfasfdasfdasf_}_asdasd";
		assertTrue(StringUtils.allTokens(input, startDelimiter, endDelimiter).isEmpty());
	}

	@Test
	public void shouldReturnSimpleToken() {
		final String input = "${TOKEN}";
		final List<String> tokens = StringUtils.allTokens(input, startDelimiter, endDelimiter);

		assertEquals(1, tokens.size());
		assertEquals("TOKEN", tokens.get(0));
	}

	@Test
	public void shouldReturnTokenAtTheEnd() {
		final String input = "asfasdfasfdasfdasf_${TOKEN}";
		final List<String> tokens = StringUtils.allTokens(input, startDelimiter, endDelimiter);

		assertEquals(1, tokens.size());
		assertEquals("TOKEN", tokens.get(0));
	}

	@Test
	public void shouldReturnTokenAtTheBeginning() {
		final String input = "${TOKEN}asfasdfasfd_${_asfdasf";
		final List<String> tokens = StringUtils.allTokens(input, startDelimiter, endDelimiter);

		assertEquals(1, tokens.size());
		assertEquals("TOKEN", tokens.get(0));
	}

	@Test
	public void shouldReturnTokenInTheMiddle() {
		final String input = "asfasdfasf${TOKEN}dasfdasf";
		final List<String> tokens = StringUtils.allTokens(input, startDelimiter, endDelimiter);

		assertEquals(1, tokens.size());
		assertEquals("TOKEN", tokens.get(0));
	}

	@Test
	public void shouldReturnTokenInTheMiddle2() {
		final String input = "asfas}dfasf${TOKEN}dasfdasf";
		final List<String> tokens = StringUtils.allTokens(input, startDelimiter, endDelimiter);

		assertEquals(1, tokens.size());
		assertEquals("TOKEN", tokens.get(0));
	}

	@Test
	public void shouldReturnTokenInTheMiddle3() {
		final String input = "asfas${dfasf${TOKEN}dasfdasf";
		final List<String> tokens = StringUtils.allTokens(input, startDelimiter, endDelimiter);

		assertEquals(1, tokens.size());
		assertEquals("TOKEN", tokens.get(0));
	}

	@Test
	public void shouldReturnTokenInTheMiddle4() {
		final String input = "asfas${dfasf${TOKEN}dasfd${asf";
		final List<String> tokens = StringUtils.allTokens(input, startDelimiter, endDelimiter);

		assertEquals(1, tokens.size());
		assertEquals("TOKEN", tokens.get(0));
	}

	@Test
	public void shouldReturnTokenInTheMiddle5() {
		final String input = "asfas${dfasf${TOKEN}dasfd}asf";
		final List<String> tokens = StringUtils.allTokens(input, startDelimiter, endDelimiter);

		assertEquals(1, tokens.size());
		assertEquals("TOKEN", tokens.get(0));
	}

	@Test
	public void shouldReturnInnerToken() {
		final String input = "asfas${dfasf${TOKEN}dasf}dasf";
		final List<String> tokens = StringUtils.allTokens(input, startDelimiter, endDelimiter);

		assertEquals(1, tokens.size());
		assertEquals("TOKEN", tokens.get(0));
	}

	@Test
	public void shouldReturnNestedToken() {
		final String input = "${${${TOKEN}}}";
		final List<String> tokens = StringUtils.allTokens(input, startDelimiter, endDelimiter);

		assertEquals(1, tokens.size());
		assertEquals("TOKEN", tokens.get(0));
	}

	@Test
	public void shouldReturnAllTokens1() {
		final String input = "___${____${TOKEN_1}__}___${TOKEN_2}__";
		final List<String> tokens = StringUtils.allTokens(input, startDelimiter, endDelimiter);

		assertEquals(2, tokens.size());
		assertEquals("TOKEN_1", tokens.get(0));
		assertEquals("TOKEN_2", tokens.get(1));
	}

	@Test
	public void shouldReturnAllTokens2() {
		final String input = "___${____${TOKEN_1__}___${TOKEN_2}__";
		final List<String> tokens = StringUtils.allTokens(input, startDelimiter, endDelimiter);

		assertEquals(2, tokens.size());
		assertEquals("TOKEN_1__", tokens.get(0));
		assertEquals("TOKEN_2", tokens.get(1));
	}

	@Test
	public void shouldReturnAllTokens3() {
		final String input = "___${____${TOKEN_2}_}___${TOKEN_2}__";
		final List<String> tokens = StringUtils.allTokens(input, startDelimiter, endDelimiter);

		assertEquals(2, tokens.size());
		assertEquals("TOKEN_2", tokens.get(0));
		assertEquals("TOKEN_2", tokens.get(1));
	}

	@Test
	public void shouldReturnAllTokens4() {
		final String input = "___${____${TOKEN_1}_${TOKEN_2}_}___${TOKEN_2}_${${TOKEN_3}}_";
		final List<String> tokens = StringUtils.allTokens(input, startDelimiter, endDelimiter, false);

		assertEquals(4, tokens.size());
		assertEquals("TOKEN_1", tokens.get(0));
		assertEquals("TOKEN_2", tokens.get(1));
		assertEquals("TOKEN_2", tokens.get(2));
		assertEquals("TOKEN_3", tokens.get(3));
	}

	@Test
	public void shouldReturnUniqueTokens1() {
		final String input = "___${____${TOKEN_2}_}___${TOKEN_2}__";
		final List<String> tokens = StringUtils.allTokens(input, startDelimiter, endDelimiter, true);

		assertEquals(1, tokens.size());
		assertEquals("TOKEN_2", tokens.get(0));
	}

	@Test
	public void shouldReturnUniqueTokens2() {
		final String input = "___${____${TOKEN_1}_${TOKEN_2}_}___${TOKEN_2}_${${TOKEN_3}}_";
		final List<String> tokens = StringUtils.allTokens(input, startDelimiter, endDelimiter, true);

		assertEquals(3, tokens.size());
		assertEquals("TOKEN_1", tokens.get(0));
		assertEquals("TOKEN_2", tokens.get(1));
		assertEquals("TOKEN_3", tokens.get(2));
	}

}
