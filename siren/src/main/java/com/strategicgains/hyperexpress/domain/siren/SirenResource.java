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
package com.strategicgains.hyperexpress.domain.siren;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.strategicgains.hyperexpress.domain.AbstractResource;

/**
 * A Siren Resource instance, containing class, properties, entities, actions, links.
 * 
 * @author toddf
 * @since Sep 12, 2014
 */
public class SirenResource
extends AbstractResource
{
	private String title;
	private Set<String> classes = new HashSet<String>(1);
	private List<SirenAction> actions = new ArrayList<SirenAction>();

	public String getTitle()
	{
		return title;
	}

	public SirenResource setTitle(String title)
	{
		this.title = title;
		return this;
	}

	public SirenResource addClass(String className)
	{
		classes.add(className);
		return this;
	}

	public Collection<String> getClasses()
	{
		return Collections.unmodifiableSet(classes);
	}

	public SirenResource addAction(SirenAction action)
	{
		actions.add(action);
		return this;
	}

	public Collection<SirenAction> getActions()
	{
		return Collections.unmodifiableCollection(actions);
	}
}
