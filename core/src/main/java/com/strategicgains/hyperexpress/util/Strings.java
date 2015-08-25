/*
 * Copyright 2014, eCollege, Inc.  All rights reserved.
 */
package com.strategicgains.hyperexpress.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

/**
 * @author toddf
 * @since May 7, 2014
 */
public class Strings
{
	// Regular expression for the hasTokene(String) method.
	private static final String TEMPLATE_REGEX = "\\{(\\w*?)\\}";
	private static final Pattern TEMPLATE_PATTERN = Pattern.compile(TEMPLATE_REGEX);

	/**
	 * Regular expressions used to create plural forms of words.
	 */
	private static final Map<String, String> PLURALIZATION_RULES = new LinkedHashMap<String, String>();
	static
	{
		PLURALIZATION_RULES.put("(ox)$", "$1en");
		PLURALIZATION_RULES.put("(\\w+)(x|ch|ss|sh)$", "$1$2es");
		PLURALIZATION_RULES.put("(\\w+)([^aeiou])y$", "$1$2ies");
		PLURALIZATION_RULES.put("(\\w*)(f)$", "$1ves");
		PLURALIZATION_RULES.put("(\\w*)(fe)$", "$1ves");
		PLURALIZATION_RULES.put("(\\w+)(sis)$", "$1ses");
		PLURALIZATION_RULES.put("(\\w*)person$", "$1people");
		PLURALIZATION_RULES.put("(\\w*)child$", "$1children");
		PLURALIZATION_RULES.put("(\\w*)series$", "$1series");
		PLURALIZATION_RULES.put("(\\w*)foot$", "$1feet");
		PLURALIZATION_RULES.put("(\\w*)tooth$", "$1teeth");
		PLURALIZATION_RULES.put("(\\w*)bus$", "$1buses");
		PLURALIZATION_RULES.put("(\\w*)man$", "$1men");
		PLURALIZATION_RULES.put("(\\w*)mouse$", "$1mice");
		PLURALIZATION_RULES.put("(\\w*)goose$", "$1geese");
		PLURALIZATION_RULES.put("(\\w*)moose$", "$1moose");
	}

	/**
	 * Converts the string (presumably a single word) from singular into plural
	 * form.
	 * 
	 * @param word a single word in singular form.
	 * @return the plural form of the word.
	 */
	public static String pluralize(String word)
	{
		if (word == null || word.isEmpty()) return word;

		for (Entry<String, String> rule : PLURALIZATION_RULES.entrySet())
		{
			final String pattern = rule.getKey().toString();
			final String replacement = rule.getValue().toString();

			if (word.matches(pattern))
			{
				return word.replaceFirst(pattern, replacement);
			}
		}

		return word.replaceFirst("([\\w]+)([^s])$", "$1$2s");
	}


	/**
	 * Answer whether the string contains any tokens (e.g. of the form '{tokenName}').
	 * If 'true' is returned, this indicates that the tokens are not fully populated
	 * (by MapStringFormat, UrlBuilder, etc).
	 * 
	 * @return true if the string contains template tokens. Otherwise, false.
	 */
	public static boolean hasToken(String string)
	{
		return TEMPLATE_PATTERN.matcher(string).find();
	}

	private Strings()
	{
		// Prevents instantiation.
	}


	public static String join(String delimiter, String[] array)
	{
		StringBuilder sb = new StringBuilder();
		boolean isFirst = true;

		for (String string : array)
		{
			if (isFirst)
			{
				isFirst = false;
			}
			else
			{
				sb.append(delimiter);
			}

			sb.append(string);
		}

		return sb.toString();
	}
}
