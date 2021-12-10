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
package com.strategicgains.hyperexpress.domain.hal;

import com.strategicgains.hyperexpress.builder.DefaultConditionalLinkBuilder;

/**
 * A convenience class for building HalLink implmenentations.
 * 
 * @author toddf
 * @since Jan 10, 2014
 */
public class HalLinkBuilder
extends DefaultConditionalLinkBuilder
{
	private static final String TEMPLATED = "templated";
	private static final String DEPRECATION = "deprecation";
	private static final String NAME = "name";
	private static final String PROFILE = "profile";
	private static final String HREFLANG = "hreflang";

	public HalLinkBuilder(String urlPattern)
    {
	    super(urlPattern);
    }

	public HalLinkBuilder templated(boolean value)
	{
		if (value)
		{
			set(TEMPLATED, Boolean.TRUE.toString());
		}
		else
		{
			set(TEMPLATED, null);
		}

		return this;
	}

	@Override
	public HalLinkBuilder type(String type)
	{
		return (HalLinkBuilder) super.type(type);
	}

	public HalLinkBuilder deprecation(String deprecation)
	{
		return set(DEPRECATION, deprecation);
	}

	public HalLinkBuilder name(String name)
	{
		return set(NAME, name);
	}

	public HalLinkBuilder profile(String profile)
	{
		return set(PROFILE, profile);
	}

	@Override
	public HalLinkBuilder title(String title)
	{
		return (HalLinkBuilder) super.title(title);
	}

	public HalLinkBuilder hreflang(String hreflang)
	{
		return set(HREFLANG, hreflang);
	}

	@Override
    public HalLinkBuilder baseUrl(String url)
    {
	    return (HalLinkBuilder) super.baseUrl(url);
    }

	@Override
    public HalLinkBuilder rel(String rel)
    {
	    return (HalLinkBuilder) super.rel(rel);
    }

	@Override
    public HalLinkBuilder set(String name, String value)
    {
	    return (HalLinkBuilder) super.set(name, value);
    }
}
