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
package com.strategicgains.hyperexpress.domain.naked;

import java.util.UUID;

/**
 * @author toddf
 * @since Apr 8, 2014
 */
public class Blog
extends AbstractEntity
{
	@Include
	private static final String IGNORED = "ignore this on resource creation";

	@Exclude
	private String name;

	@Include
	private String description;
	private Reference<User> owner;

	@Include
	private transient int somethingTransient = 5;

	@Include
	private volatile boolean somethingVolatile = true;

	@Include
	private static double somethingStatic = 3.14597;

	public UUID getOwnerId()
	{
		return (owner != null ? owner.getId() : null);
	}

	public void setOwner(User owner)
	{
		this.owner = (owner != null ? new Reference<>(owner) : null);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}
}
