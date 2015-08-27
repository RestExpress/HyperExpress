/*
    Copyright 2015, Strategic Gains, Inc.

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

import com.strategicgains.hyperexpress.BuilderFactory;


/**
 * @author toddf
 * @since Jun 3, 2015
 */
public class DefaultBuilderFactory
implements BuilderFactory
{
	private boolean useAnnotationsResolver;

	/**
	 * Creates a new DefaultBuilderFactory. Same as DefaultBuilderFactory(true).
	 */
	public DefaultBuilderFactory()
	{
		this(true);
	}

	/**
	 * When useAnnotationsResolver is set to true, the newTokenResolver() method will create
	 * a token resolver that will also bind via the {@link BindToken} annotations in the POJO model.
	 * 
	 * @param useAnnotationsResolver
	 */
	public DefaultBuilderFactory(boolean useAnnotationsResolver)
	{
		super();
		this.useAnnotationsResolver = useAnnotationsResolver;
	}

	@Override
	public ConditionalLinkBuilder newLinkBuilder()
	{
		return new DefaultConditionalLinkBuilder();
	}
	@Override
	public ConditionalLinkBuilder newLinkBuilder(String urlPattern)
	{
		return new DefaultConditionalLinkBuilder(urlPattern);
	}

	@Override
	public UrlBuilder newUrlBuilder()
	{
		return new DefaultUrlBuilder();
	}

	@Override
    public TokenResolver newTokenResolver()
    {
	    return new DefaultTokenResolver(useAnnotationsResolver);
    }
}
