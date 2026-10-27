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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.strategicgains.hyperexpress.builder.DefaultTokenResolver;
import com.strategicgains.hyperexpress.domain.Namespace;
import com.strategicgains.hyperexpress.exception.ResourceException;

/**
 * @author toddf
 * @since Mar 18, 2014
 */
public class SirenResourceImplTest
{
	@Test
	public void shouldThrowOnMissingRel()
	{
		assertThrows(ResourceException.class, () -> {
		SirenResource r = new SirenResource();
		r.addLink(new SirenLinkBuilder("/something").build(new DefaultTokenResolver()));
		});
	}

	@Test
	public void shouldThrowOnNullLink()
	{
		assertThrows(ResourceException.class, () -> {
		SirenResource r = new SirenResource();
		r.addLink(null);
		});
	}

	@Test
	public void shouldThrowOnNullEmbed()
	{
		assertThrows(ResourceException.class, () -> {
		SirenResource r = new SirenResource();
		r.addResource("something", null);
		});
	}

	@Test
	public void shouldThrowOnNullEmbedRel()
	{
		assertThrows(ResourceException.class, () -> {
		SirenResource r = new SirenResource();
		r.addResource(null, new SirenResource());
		});
	}

	@Test
	public void shouldThrowOnNullCurie()
	{
		assertThrows(ResourceException.class, () -> {
		SirenResource r = new SirenResource();
		r.addNamespace(null);
		});
	}

	@Test
	public void shouldThrowOnNamelessCurie()
	{
		assertThrows(ResourceException.class, () -> {
		SirenResource r = new SirenResource();
		r.addNamespace(new Namespace(null, "/sample/{rel}"));
		});
	}

	@Test
	public void shouldAddCurieRel()
	{
		SirenResource r = new SirenResource();
		r.addNamespace(new Namespace("some-name", "/sample/{rel}"));
		r.addNamespace(new Namespace("another-name", "/sample/{rel}"));

		List<Namespace> curies = r.getNamespaces();
		assertNotNull(curies);
		assertEquals(2, curies.size());
	}

	@Test
	public void shouldAddNamespace()
	{
		SirenResource r = new SirenResource();
		r.addNamespace("ea:blah", "/sample/{rel}");

		List<Namespace> curies = r.getNamespaces();
		assertNotNull(curies);
		assertEquals(1, curies.size());
		assertEquals("/sample/{rel}", curies.get(0).href());
		assertEquals("ea:blah", curies.get(0).name());
	}
}
