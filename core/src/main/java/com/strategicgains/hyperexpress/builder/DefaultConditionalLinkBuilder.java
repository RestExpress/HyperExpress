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

import com.strategicgains.hyperexpress.domain.Link;

/**
 * Extends LinkBuilder, adding a 'conditional' flag, where the value can either be "true" or
 * a token (e.g. "{role}").  If the flag is set to true and the resulting Link that is built
 * contains a token (a URL token is not bound), then build() returns null.
 * <p/>
 * Otherwise, if the flag is set to a token and that token doesn't get bound from the object
 * during build(), then the resulting link is not returned (it is null).
 * 
 * @author toddf
 * @since Jul 9, 2014
 */
public class DefaultConditionalLinkBuilder
extends DefaultLinkBuilder
implements ConditionalLinkBuilder
{
	private boolean optional = false;
	private List<String> conditionals = new ArrayList<String>();

	public DefaultConditionalLinkBuilder()
    {
	    super();
    }

	public DefaultConditionalLinkBuilder(String urlPattern)
    {
	    super(urlPattern);
    }

	/**
	 * Copy-constructor from DefaultLinkBuilder.
	 * 
	 * @param builder a DefaultLinkBuilder instance. Never null;
	 */
	public DefaultConditionalLinkBuilder(DefaultLinkBuilder builder)
	{
		super(builder);
	}

	/**
	 * Copy-constructor from ConditionalLinkBuilder.
	 * 
	 * @param builder an ConditionalLinkBuilder instance. Never null.
	 */
	public DefaultConditionalLinkBuilder(DefaultConditionalLinkBuilder builder)
	{
		super(builder);
		this.conditionals = new ArrayList<String>(builder.conditionals);
	}

	public void optional()
	{
		optional = true;
	}

	public void ifBound(String token)
	{
		if (token == null)
		{
			return;
		}

		if (token.startsWith("{") && token.endsWith("}"))
		{
			conditionals.add(token);
		}
		else
		{
			conditionals.add("{" + token + "}");
		}
	}

	public void ifNotBound(String token)
	{
		if (token == null)
		{
			return;
		}

		if (token.startsWith("{") && token.endsWith("}"))
		{
			conditionals.add("!" + token);
		}
		else
		{
			conditionals.add("!{" + token + "}");
		}
	}

	public boolean isOptional()
	{
		return optional;
	}

	public boolean hasConditionals()
	{
		return (conditionals != null && !conditionals.isEmpty());
	}

	public List<String> getConditionals()
	{
		return conditionals;
	}

	@Override
	public Link build(Object object, TokenResolver tokenResolver)
    {
		Link link = super.build(object, tokenResolver);


		if (hasConditionals())
		{
			for (String conditional : conditionals)
			{
				String value = tokenResolver.resolve(conditional, object);

				// not bound
				if ((value.startsWith("{") && value.endsWith("}"))
					|| value.trim().equalsIgnoreCase(Boolean.FALSE.toString()))
				{
					return null;
				}
				// desire not bound
				else if (value.startsWith("!"))
				{
					if (!(value.startsWith("!{") && value.endsWith("}"))
						&& !value.substring(1).trim().equalsIgnoreCase(Boolean.FALSE.toString()))
					{
						return null;
					}
				}
			}
		}
		
		if (isOptional() && link.hasToken())
		{
			return null;
		}

		return link;
    }

	@Override
	public Link build()
	{
		Link link = super.build();

		if (isOptional() && link != null && link.hasToken())
		{
			return null;
		}

		return link;
	}

	@Override
	public Link build(TokenResolver tokenResolver)
	{
		Link link = super.build(tokenResolver);

		if (isOptional() && link.hasToken())
		{
			return null;
		}

		return link;
	}
}
