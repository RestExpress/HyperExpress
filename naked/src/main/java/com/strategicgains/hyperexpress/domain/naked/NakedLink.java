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

import java.util.regex.Pattern;

import com.strategicgains.hyperexpress.domain.Link;
import com.strategicgains.syntaxe.annotation.Required;

/**
 * A HAL Link Object represents a hyperlink from the containing resource to a
 * URI.
 * 
 * @author toddf
 * @since May 21, 2013
 */
public class NakedLink
{
	public static final String IDENTIFIER = "id";
	public static final String TYPE = "type";
	public static final String TITLE = "title";
	public static final String TEMPLATED = "templated";
	public static final String NAME = "name";
	public static final String HREF = "href";
	public static final String DEPRECATION = "deprecation";

	// Regular expression for the hasTemplate() method.
	private static final String TEMPLATE_REGEX = "\\{(\\w*?)\\}";
	private static final Pattern TEMPLATE_PATTERN = Pattern.compile(TEMPLATE_REGEX);

	@Required
	private String id;

	/**
	 * The "href" property is REQUIRED.
	 * 
	 * Its value is either a URI [RFC3986] or a URI Template [RFC6570].
	 * 
	 * If the value is a URI Template then the Link Object SHOULD have a
	 * "templated" attribute whose value is true.
	 */
	@Required
	private String href;

	/**
	 * The "name" property is OPTIONAL.
	 * 
	 * Its value MAY be used as a secondary key for selecting Link Objects which
	 * share the same relation type.
	 * 
	 * For distinguishing between Resource and Link elements that share the same
	 * rel value. The name attribute SHOULD NOT be used exclusively for
	 * identifying elements within a HAL representation, it is intended only as
	 * a 'secondary key' to a given rel value.
	 */
	private String name;

/**
	 * The "title" property is OPTIONAL.
	 * 
	 * Its value is a string and is intended for labelling the link with a
	 * human-readable identifier (as defined by [RFC5988]).
	 * 
	 * The "title" attribute conveys human-readable information about the
	 * link.  The content of the "title" attribute is Language-Sensitive.
	 * Entities such as "&amp;" and "&lt;" represent their corresponding
	 * characters ("&" and "<", respectively), not markup.  Link elements
	 * MAY have a title attribute.
	 */
	private String title;

	/**
	 * The "templated" property is OPTIONAL.
	 * 
	 * Its value is boolean and SHOULD be true when the Link Object's "href"
	 * property is a URI Template.
	 * 
	 * Its value SHOULD be considered false if it is undefined or any other
	 * value than true.
	 */
	private Boolean templated;

	/**
	 * The "type" property is OPTIONAL.
	 * 
	 * Its value is a string used as a hint to indicate the media type expected
	 * when dereferencing the target resource.
	 * 
	 * On the link element, the "type" attribute's value is an advisory media
	 * type: it is a hint about the type of the representation that is expected
	 * to be returned when the value of the href attribute is dereferenced. Note
	 * that the type attribute does not override the actual media type returned
	 * with the representation. Link elements MAY have a type attribute, whose
	 * value MUST conform to the syntax of a MIME media type [MIMEREG].
	 */
	private String type;

	/**
	 * The "deprecation" property is OPTIONAL.
	 * 
	 * Its presence indicates that the link is to be deprecated (i.e. removed)
	 * at a future date. Its value is a URL that SHOULD provide further
	 * information about the deprecation.
	 * 
	 * A client SHOULD provide some notification (for example, by logging a
	 * warning message) whenever it traverses over a link that has this
	 * property. The notification SHOULD include the deprecation property's
	 * value so that a client maintainer can easily find information about the
	 * deprecation.
	 */
	private String deprecation;

	public NakedLink()
	{
		super();
	}

	public NakedLink(Link that)
	{
		this();

		if (NakedLink.class.isAssignableFrom(that.getClass()))
		{
			this.setId(((NakedLink)that).getId());
		}

		this.setHref(that.getHref());
		this.setDeprecation(that.get(DEPRECATION));
		this.setName(that.get(NAME));
		this.setTemplated(that.has(TEMPLATED) ? Boolean.valueOf(that.get(TEMPLATED)) : null);
		this.setTitle(that.get(TITLE));
		this.setType(that.get(TYPE));
	}

	public String getId()
	{
		return id;
	}

	public NakedLink setId(String id)
	{
		this.id = id;
		return this;
	}

	public String getHref()
	{
		return href;
	}

	public NakedLink setHref(String href)
	{
		this.href = href;
		return this;
	}

	public String getName()
	{
		return name;
	}

	public NakedLink setName(String name)
	{
		this.name = name;
		return this;
	}

	public String getTitle()
	{
		return title;
	}

	public NakedLink setTitle(String title)
	{
		this.title = title;
		return this;
	}

	public Boolean getTemplated()
	{
		return templated;
	}

	public boolean hasTemplate()
	{
		return TEMPLATE_PATTERN.matcher(getHref()).find();
	}

	public NakedLink setTemplated(Boolean templated)
	{
		this.templated = templated;
		return this;
	}

	public String getType()
	{
		return type;
	}

	public NakedLink setType(String type)
	{
		this.type = type;
		return this;
	}

	public String getDeprecation()
	{
		return deprecation;
	}

	public NakedLink setDeprecation(String deprecation)
	{
		this.deprecation = deprecation;
		return this;
	}
}
