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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import com.strategicgains.hyperexpress.builder.DefaultTokenResolver;
import com.strategicgains.hyperexpress.domain.Namespace;
import com.strategicgains.hyperexpress.domain.naked.NakedLinkBuilder;
import com.strategicgains.hyperexpress.domain.naked.NakedResource;
import com.strategicgains.hyperexpress.exception.ResourceException;

/**
 * @author toddf
 * @since Mar 18, 2014
 */
public class NakedResourceImplTest
{
	@Test(expected=ResourceException.class)
	public void shouldThrowOnMissingRel()
	{
		NakedResource r = new NakedResource();
		r.addLink(new NakedLinkBuilder("/something").build(new DefaultTokenResolver()));
	}

	@Test(expected=ResourceException.class)
	public void shouldThrowOnNullLink()
	{
		NakedResource r = new NakedResource();
		r.addLink(null);
	}

	@Test(expected=ResourceException.class)
	public void shouldThrowOnNullEmbed()
	{
		NakedResource r = new NakedResource();
		r.addResource("something", null);
	}

	@Test(expected=ResourceException.class)
	public void shouldThrowOnNullEmbedRel()
	{
		NakedResource r = new NakedResource();
		r.addResource(null, new NakedResource());
	}

	@Test(expected=ResourceException.class)
	public void shouldThrowOnNullCurie()
	{
		NakedResource r = new NakedResource();
		r.addNamespace(null);
	}

	@Test(expected=ResourceException.class)
	public void shouldThrowOnNamelessCurie()
	{
		NakedResource r = new NakedResource();
		r.addNamespace(new Namespace(null, "/sample/{rel}"));
	}

	@Test
	public void shouldAddCurieRel()
	{
		NakedResource r = new NakedResource();
		r.addNamespace(new Namespace("some-name", "/sample/{rel}"));
		r.addNamespace(new Namespace("another-name", "/sample/{rel}"));

		List<Namespace> curies = r.getNamespaces();
		assertNotNull(curies);
		assertEquals(2, curies.size());
	}

	@Test
	public void shouldAddNamespace()
	{
		NakedResource r = new NakedResource();
		r.addNamespace("ea:blah", "/sample/{rel}");

		List<Namespace> curies = r.getNamespaces();
		assertNotNull(curies);
		assertEquals(1, curies.size());
		assertEquals("/sample/{rel}", curies.get(0).href());
		assertEquals("ea:blah", curies.get(0).name());
	}
}
