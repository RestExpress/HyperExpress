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
package com.strategicgains.hyperexpress.domain;

/**
 * @author toddf
 * @since Apr 8, 2014
 */
public class Namespace
extends LinkImpl
{
	private static final String TEMPLATED = "templated";

	public Namespace(String name, String href)
    {
		super(name, href);
    }

	public String name()
	{
		return getRel();
	}

	public String href()
	{
		return getHref();
	}

	public Namespace setTemplated(boolean value)
	{
		set(TEMPLATED, Boolean.toString(value));
		return this;
	}
}
