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
import java.util.function.Predicate;

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
	private List<Conditional> conditionals = new ArrayList<>();

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
		this.conditionals = new ArrayList<>(builder.conditionals);
	}

	public void optional()
	{
		optional = true;
	}

	public void ifBound(String token)
	{
		ifBound(token, null);
	}

	public void ifBound(String token, Predicate<String> predicate)
	{
		if (token == null)
		{
			return;
		}

		if (token.startsWith("{") && token.endsWith("}"))
		{
			conditionals.add(new Conditional(token, predicate));
		}
		else
		{
			conditionals.add(new Conditional("{" + token + "}", predicate));
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
			conditionals.add(new Conditional("!" + token));
		}
		else
		{
			conditionals.add(new Conditional("!{" + token + "}"));
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

	public List<Conditional> getConditionals()
	{
		return conditionals;
	}

	@Override
	public Link build(Object object, TokenResolver tokenResolver)
    {
		Link link = super.build(object, tokenResolver);


		if (hasConditionals())
		{
			for (Conditional conditional : conditionals)
			{
				String value = tokenResolver.resolve(conditional.token, object);

				if (!conditional.test(value)) return null;
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

	public class Conditional
	{
		private String token;
		private Predicate<String> predicate;

		public Conditional(String token)
		{
			this(token, null);
		}

		public Conditional(String token, Predicate<String> predicate)
		{
			super();
			this.predicate = predicate;
			this.token = token;
		}

		public boolean test(String value)
		{
			if (!isBound(value) || isFalse(value))
			{
				return false;
			}
			else if (shouldBeUnbound()) 
			{
				String val = value.substring(1);

				if (isBound(val) && !isFalse(val))
				{
					return false;
				}
			}
			else if (predicate != null)
			{
				return predicate.test(value);
			}

			return true;
		}

		private boolean shouldBeUnbound()
		{
			return token.startsWith("!");
		}

		private boolean isBound(String value)
		{
			return !(value.startsWith("{") && value.endsWith("}"));
		}

		private boolean isFalse(String value)
		{
			return Boolean.FALSE.toString().equalsIgnoreCase(value.trim());
		}
	}
}
