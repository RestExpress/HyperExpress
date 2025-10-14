/*
	Copyright 2005 Strategic Gains, Inc.
	
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
		http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
package com.strategicgains.hyperexpress.util;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * @author Todd Fredrich
 * @since Feb 16, 2005
 */
public class MapStringFormatTest
{
	private static final String FREDRICH_TODD_A = "Fredrich, Todd A.";

	@Test
	public void shouldFormatWithDefaultDelimiters()
	{
		MapStringFormat formatter = new MapStringFormat();
		String template = "{last_name}, {first_name} {middle_initial}.";
		Map<String, String> parameters = createParameters();
		String result = formatter.format(template, parameters);
		assertEquals(FREDRICH_TODD_A, result);
	}

	@Test
	public void shouldFormatWithEscapedDelimiters()
	{
		MapStringFormat formatter = new MapStringFormat("{{", "}}");
		String template = "{{last_name}}, {{first_name}} {{middle_initial}}.";
		Map<String, String> parameters = createParameters();
		String result = formatter.format(template, parameters);
		assertEquals(FREDRICH_TODD_A, result);
	}

	@Test
	public void shouldFormatWithStringArray()
	{
		MapStringFormat formatter = new MapStringFormat();
		String template = "{last_name}, {first_name} {middle_initial}.";
		String result = formatter.format(template, createParameterArray());
		assertEquals(FREDRICH_TODD_A, result);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowExceptionWithUnbalancedParameters()
	{
		MapStringFormat formatter = new MapStringFormat();
		String template = "{last_name}, {first_name} {middle_initial}.";
		formatter.format(template, "first_name", "Todd", "middle_initial", "last_name", "Fredrich");
		fail("Shouldn't get here");
	}

	@Test
	public void shouldFormatWithSpecifiedDelimiters()
	{
		MapStringFormat formatter = new MapStringFormat("[", "]");
		String template = "[last_name], [first_name] [middle_initial].";
		Map<String, String> parameters = createParameters();
		String result = formatter.format(template, parameters);
		assertEquals(FREDRICH_TODD_A, result);
	}

	@Test
	public void shouldFormatWithStringDelimiters()
	{
		MapStringFormat formatter = new MapStringFormat("<start>", "<end>");
		String template = "<start>last_name<end>, <start>first_name<end> <start>middle_initial<end>.";
		Map<String, String> parameters = createParameters();
		String result = formatter.format(template, parameters);
		assertEquals(FREDRICH_TODD_A, result);
	}
	
	@Test
	public void shouldConvertNameValuePairsToMap()
	{
		Map<String, String> pairs = MapStringFormat.toMap("firstName", "Todd", "lastName", "Fredrich");
		assertNotNull(pairs);
		assertEquals(2, pairs.size());
		assertEquals("Todd", pairs.get("firstName"));
		assertEquals("Fredrich", pairs.get("lastName"));
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowOnUnmatchedPairs()
	{
		MapStringFormat.toMap("firstName", "Todd", "lastName");
		fail("Should throw IllegalArgumentException for unmatched pair");
	}
	
	@Test
	public void shouldHandleNull()
	{
		Map<String, String> pairs = MapStringFormat.toMap((String[]) null);
		assertNotNull(pairs);
		assertTrue(pairs.isEmpty());
	}

	private Map<String, String> createParameters() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("first_name", "Todd");
		parameters.put("middle_initial", "A");
		parameters.put("last_name", "Fredrich");
		return parameters;
	}

	private String[] createParameterArray() {
		return new String[] {
			"first_name", "Todd",
			"middle_initial", "A",
			"last_name", "Fredrich"
		};
	}
	
}
