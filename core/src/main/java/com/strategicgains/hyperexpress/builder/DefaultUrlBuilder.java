/*
    Copyright 2014, Strategic Gains, Inc.

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
package com.strategicgains.hyperexpress.builder;

import java.util.ArrayList;
import java.util.List;

import com.strategicgains.hyperexpress.util.Strings;

/**
 * Build URL strings from a URL pattern, binding URL tokens to actual values.
 * <p/>
 * There is also building of dynamic query strings. By calling addQuery() with a
 * tokenized query-string segment (e.g. "limit={limit}"), the builder will
 * include query-string arguments that get fully populated. If there is no
 * parameter bound to a particular query-string segment, it is not included in
 * the constructed URL string.
 * 
 * @author toddf
 * @since Jan 10, 2014
 */
public class DefaultUrlBuilder
implements UrlBuilder
{
	private String baseUrl;
	private String urlPattern;
	private List<String> queries;

	/**
	 * Create an empty UrlBuilder, with no URL pattern. Using this constructor
	 * mandates that you MUST use the build(String) form of build instead of the
	 * parameterless, build(), as the latter will throw IllegalStateException in
	 * this state.
	 */
	public DefaultUrlBuilder()
	{
		super();
	}

	/**
	 * Create a new UrlBuilder, passing in the URL pattern in which to
	 * substitute tokens. This URL pattern may represent the entire URL or just
	 * the path portion (relative path).
	 * <p/>
	 * The URL pattern is templated, in that it contains tokens to be later
	 * substituted for actual values. The tokens are delimited with beginning
	 * and trailing curly-braces (e.g. '{token}').
	 * <p/>
	 * If used in conjunction with baseUrl(), this URL pattern must be just the
	 * path portion of the URL and should be prefixed with a leading slash
	 * ('/').
	 * <p/>
	 * For example: '/users/{userId}' or
	 * 'http://www.example.com/api/users/{userId}'
	 * 
	 * @param urlPattern a URL path with optional tokens of the form '{tokenName}'
	 */
	public DefaultUrlBuilder(String urlPattern)
	{
		this();

		if (urlPattern == null)
		{
			throw new NullPointerException("URL Pattern cannot be null using this constructor. Use the no-argument constructor, UrlBuilder().");
		}

		this.urlPattern = urlPattern;
	}

	/**
	 * Set the URL pattern for the URL builder.
	 * 
	 * @param urlPattern a URL path with optional tokens of the form '{tokenName}'
	 * @return this UrlBuilder instance to facilitate method chaining.
	 */
	public DefaultUrlBuilder urlPattern(String urlPattern)
	{
		this.urlPattern = urlPattern;
		return this;
	}

	/**
	 * Retrieve the URL pattern set on this URL builder.
	 * 
	 * @return the URL pattern or null.
	 */
	public String urlPattern()
	{
		return urlPattern;
	}

	/**
	 * Set the prefix portion of the URL which is to be pre-pended to the URL
	 * pattern.
	 * <p/>
	 * Optional, as the URL pattern may contain everything. However, this is
	 * provided as a convenience so consumers don't have to perform their own
	 * concatenation to pass in the entire URL pattern string.
	 * <p/>
	 * For example: 'http://www.example.com:8080'
	 * 
	 * @param baseUrl the string that will prefix the URL pattern
	 * @return this UrlBuilder instance to facilitate method chaining.
	 */
	public DefaultUrlBuilder baseUrl(String baseUrl)
	{
		this.baseUrl = baseUrl;
		return this;
	}

	/**
	 * Retrieve the base URL set on this URL builder.
	 * 
	 * @return the base URL or null.
	 */
	public String baseUrl()
	{
		return baseUrl;
	}

	/**
	 * Add an optional query-string segment to this UrlBuilder. If all of the
	 * tokens in the query-string are bound, the segment is included in the
	 * generated URL string during build().  However, if there are unbound
	 * tokens in the resulting query-string segment, it is not included in the
	 * generated URL string.
	 * <p/>
	 * Do not include any question mark ("?") or ampersand ("&") in the query-string
	 * segment.
	 * 
	 * @param query a query-string segment to optionally include.
	 * @return this UrlBuilder instance to facilitate method chaining.
	 */
	public DefaultUrlBuilder withQuery(String query)
	{
		queries().add(query);
		return this;
	}

	/**
	 * Remove the query-string segments from this UrlBuilder.
	 */
	public void clearQueries()
	{
		if (queries != null)
		{
			queries.clear();
		}
	}

	@Override
	public DefaultUrlBuilder clone()
	{
		DefaultUrlBuilder b = new DefaultUrlBuilder(this.urlPattern);
		b.baseUrl = this.baseUrl;
		b.queries = (this.queries == null ? null : new ArrayList<String>(this.queries));
		return b;
	}

	@Override
	public String toString()
	{
		StringBuilder s = new StringBuilder();
		s.append("baseUrl: ");
		s.append(baseUrl == null ? "null" : baseUrl);
		s.append(", urlPattern: ");
		s.append(urlPattern == null ? "null" : urlPattern);
		printQueryStrings(s);
		return s.toString();
	}

	/**
	 * Builds a URL but does not perform any token replacement.
	 * Equivalent to (and short-hand for) build(null).
	 * 
	 * @return
	 */
	public String build()
	{
		return build(null);
	}

	/**
	 * Build a URL, resolving any tokens using the given TokenResolver.
	 * 
	 * @param tokenResolver a TokenResolver to perform token substitution.
	 * @return a URL string.
	 */
	public String build(TokenResolver tokenResolver)
	{
		return build(buildFullUrlPattern(), null, tokenResolver);
	}

	/**
	 * Build a URL, resolving any tokens using the given TokenResolver. Additionally,
	 * will call any TokenBinders for the given Object instance to bind values from
	 * it into the URL.
	 * 
	 * @param object an object of which to bind properties into the URL parameters.
	 * @param tokenResolver a TokenResolver to perform token substitution.
	 * @return a URL string.
	 */
	public String build(Object object, TokenResolver tokenResolver)
	{
		return build(buildFullUrlPattern(), object, tokenResolver);
	}

	/**
	 * Build a new URL from the given URL pattern. Optional query-string parameters
	 * may be appended. Tokens are resolved using the passed-in TokenResolver.
	 * 
	 * @param urlPattern a URL pattern to bind, instead of using the one associated 
	 * with this builder. May not be null.
	 * @param tokenResolver a TokenResolver instance set with bound token values.
	 * @return a URL string.
	 */
	public String build(String urlPattern, TokenResolver tokenResolver)
	{
		return build(urlPattern, null, tokenResolver);
	}

	/**
	 * Build a URL, resolving any tokens using the given TokenResolver. Additionally,
	 * will call any TokenBinders for the given Object instance to bind values from
	 * it into the URL.
	 * 
	 * @param urlPattern a URL pattern, with optional tokens.
	 * @param object an object of which to bind properties into the URL parameters.
	 * @param tokenResolver a TokenResolver to perform token substitution.
	 * @return a URL string.
	 */
	public String build(String urlPattern, Object object, TokenResolver tokenResolver)
	{
		if (tokenResolver == null)
		{
			return appendQueryString(urlPattern, null);
		}

		String url = tokenResolver.resolve(urlPattern, object);
		return appendQueryString(url, tokenResolver);
	}

	private String buildFullUrlPattern()
	{
		if (urlPattern == null)
		{
			throw new IllegalStateException("Null URL pattern");
		}

		return (baseUrl == null ? urlPattern : baseUrl + urlPattern);
	}

	private String appendQueryString(String url, TokenResolver tokenResolver)
	{
		if (queries == null || queries.isEmpty()) return url;

		StringBuilder sb = new StringBuilder(url);
		boolean hasQuery = url.contains("?");

		for (String query : queries)
		{
			String boundQuery = null;

			if (Strings.hasToken(query))
			{
				boundQuery = resolveQueryString(query, tokenResolver);
			}
			else
			{
				boundQuery = query;
			}

			if (boundQuery != null)
			{
				sb.append(queryDelimiter(hasQuery));
				sb.append(boundQuery);
				hasQuery = true;
			}
		}

		return (hasQuery ? sb.toString() : url);
	}

	private String resolveQueryString(String query, TokenResolver tokenResolver)
    {
		if (tokenResolver != null)
		{
			String[] resolved = tokenResolver.resolveMulti(query);
			String all = Strings.join(queryDelimiter(true), resolved);
			return (Strings.hasToken(all) ? null : all);
		}

		return null;
    }

	private String queryDelimiter(boolean hasQuery)
    {
		return (hasQuery ? "&" : "?");
    }

	private void printQueryStrings(StringBuilder s)
    {
	    boolean isFirst = true;
		s.append("}, query-strings: {");

		if (queries != null)
		{
			for (String query : queries)
			{
				if (!isFirst)
				{
					s.append(", ");
				}
				else
				{
					isFirst = false;
				}

				s.append(query);
			}
		}

		s.append("}");
    }

	private List<String> queries()
	{
		if (queries == null)
		{
			queries = new ArrayList<String>();
		}

		return queries;
	}
}
