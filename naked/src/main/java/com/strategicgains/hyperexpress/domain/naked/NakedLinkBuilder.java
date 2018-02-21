/*
    Copyright 2017, Strategic Gains, Inc.

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
package com.strategicgains.hyperexpress.domain.naked;

import com.strategicgains.hyperexpress.builder.DefaultConditionalLinkBuilder;

/**
 * A convenience class for building HalLink implmenentations.
 * 
 * @author toddf
 * @since Jan 10, 2014
 */
public class NakedLinkBuilder
extends DefaultConditionalLinkBuilder
{
	private static final String TEMPLATED = "templated";
	private static final String DEPRECATION = "deprecation";
	private static final String NAME = "name";
	private static final String PROFILE = "profile";
	private static final String HREFLANG = "hreflang";

	public NakedLinkBuilder(String urlPattern)
    {
	    super(urlPattern);
    }

	public NakedLinkBuilder templated(boolean value)
	{
		if (value == false)
		{
			set(TEMPLATED, null);
		}
		else
		{
			set(TEMPLATED, Boolean.TRUE.toString());
		}

		return this;
	}

	@Override
	public NakedLinkBuilder type(String type)
	{
		return (NakedLinkBuilder) super.type(type);
	}

	public NakedLinkBuilder deprecation(String deprecation)
	{
		return (NakedLinkBuilder) set(DEPRECATION, deprecation);
	}

	public NakedLinkBuilder name(String name)
	{
		return (NakedLinkBuilder) set(NAME, name);
	}

	public NakedLinkBuilder profile(String profile)
	{
		return (NakedLinkBuilder) set(PROFILE, profile);
	}

	@Override
	public NakedLinkBuilder title(String title)
	{
		return (NakedLinkBuilder) super.title(title);
	}

	public NakedLinkBuilder hreflang(String hreflang)
	{
		return (NakedLinkBuilder) set(HREFLANG, hreflang);
	}

	@Override
    public NakedLinkBuilder baseUrl(String url)
    {
	    return (NakedLinkBuilder) super.baseUrl(url);
    }

	@Override
    public NakedLinkBuilder rel(String rel)
    {
	    return (NakedLinkBuilder) super.rel(rel);
    }

	@Override
    public NakedLinkBuilder set(String name, String value)
    {
	    return (NakedLinkBuilder) super.set(name, value);
    }
}
