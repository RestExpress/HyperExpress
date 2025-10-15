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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Todd Fredrich
 * @since Feb 16, 2005
 */
public class MapStringFormat
{
	private static final String DEFAULT_END_DELIMITER = "}";
	private static final String DEFAULT_START_DELIMITER = "{";
	private static final String ESCAPE_REGEX = "([\\\\*+\\[\\](){}\\$.?\\^|])";
	private static final String ESCAPE_REPLACEMENT = "\\\\$1";


	// PROTOCOL: VARIABLES

	private String endDelimiter;
	private String startDelimiter;
	private String escapedStartDelimiter;
	private String escapedEndDelimiter;

	public MapStringFormat()
	{
		this(DEFAULT_START_DELIMITER, DEFAULT_END_DELIMITER);
	}

	public MapStringFormat(String startDelimiter, String endDelimiter)
	{
		startDelimiter(startDelimiter);
		endDelimiter(endDelimiter);
	}

	// PROTOCOL: ACCESSING

	public String endDelimiter()
	{
		return endDelimiter;
	}

	public void endDelimiter(String delimiter)
	{
		endDelimiter = delimiter;
		escapedEndDelimiter = delimiter != null ? escape(delimiter) : null;
	}

	public String startDelimiter()
	{
		return startDelimiter;
	}

	public void startDelimiter(String delimiter)
	{
		startDelimiter = delimiter;
		escapedStartDelimiter = delimiter != null ? escape(delimiter) : null;
	}

	public String format(String string, String... parameters)
	{
		if (parameters.length % 2 != 0) throw new IllegalArgumentException("Parameters must be in name/value pairs");

		return format(string, toMap(parameters));
	}

	/**
	 * 
	 * @param string The string containing named tokens to replace with parameters.
	 * @param parameters A map of strings or I18nKeys, keyed by parameter names.
	 * @return An internationalized string with parameter tokens replaced with values.
	 */
	public String format(String string, Map<String, String> parameters)
	{
		String result = string;
		StringBuilder sb = new StringBuilder();

		for (Entry<String, String> entry : parameters.entrySet())
		{
			constructParameterName(sb, entry.getKey());
			result = result.replaceAll(sb.toString(), entry.getValue());
		}
		
		
		return result;
	}

	// PROTOCOL: UTILITY

	/**
	 * @param key
	 * @return String with key contained within start delimiter and end delimiter
	 */
	private String constructParameterName(StringBuilder sb, String key)
	{
		sb.setLength(0);
		sb.append(escapedStartDelimiter);
		sb.append(key);
		sb.append(escapedEndDelimiter);
		return sb.toString();
	}

	private String escape(String delimeter)
	{
		return delimeter.replaceAll(ESCAPE_REGEX, ESCAPE_REPLACEMENT);
	}

	/**
	 * Converts a sequence of strings into name/value pairs in a map. Pairs must
	 * be matched or IllegalArgumentException is thrown. If nameValuePairs is
	 * null an empty Map is returned.
	 * 
	 * @param nameValuePairs a sequence of strings as matched name/value pairs.
	 * @return a Map of name/value pairs. Never null. Empty, if nameValuePairs is null.
	 * @throws IllegalArgumentException if name/value pairs not matched.
	 */
	public static Map<String, String> toMap(String... nameValuePairs)
	{
		Map<String, String> result = new HashMap<>();

		if (nameValuePairs == null) return result;

		if ((nameValuePairs.length % 2) != 0)
		{
			throw new IllegalArgumentException("Name/value pairs unbalanced: " + Arrays.toString(nameValuePairs));
		}

		for (int i = 0; i < nameValuePairs.length; i += 2)
		{
			result.put(nameValuePairs[i], nameValuePairs[i + 1]);
		}

		return result;
	}

}
