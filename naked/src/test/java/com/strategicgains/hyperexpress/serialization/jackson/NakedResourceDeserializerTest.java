/*
<<<<<<< HEAD
    Copyright 2014, Strategic Gains, Inc.
=======
    Copyright 2015, Strategic Gains, Inc.
>>>>>>> upstream/master

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
package com.strategicgains.hyperexpress.serialization.jackson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.strategicgains.hyperexpress.builder.DefaultLinkBuilder;
import com.strategicgains.hyperexpress.builder.LinkBuilder;
import com.strategicgains.hyperexpress.domain.Link;
import com.strategicgains.hyperexpress.domain.Namespace;
import com.strategicgains.hyperexpress.domain.Resource;
import com.strategicgains.hyperexpress.domain.naked.NakedResource;

/**
 * @author toddf,azaytsev
 * @since Feb 6, 2015
 */
public class NakedResourceDeserializerTest
{
	private static ObjectMapper mapper = new ObjectMapper();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		SimpleModule module = new SimpleModule();
		module.addDeserializer(NakedResource.class, new NakedResourceDeserializer());
		mapper.registerModule(module);
		mapper
			.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
			.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
			.setSerializationInclusion(JsonInclude.Include.NON_NULL)
			.setVisibility(PropertyAccessor.FIELD, Visibility.ANY)
			.setVisibility(PropertyAccessor.GETTER, Visibility.NONE)
			.setVisibility(PropertyAccessor.SETTER, Visibility.NONE)
			.setVisibility(PropertyAccessor.IS_GETTER, Visibility.NONE)
			.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"));
	}

	@Test
	public void shouldDeserializeLinkArray()
	throws IOException
	{
		String json = "{\"SessionId\":\"9d1b29fc-4240-4993-82c5-a42d00900324\",\"_links\":{\"self\":{\"href\":\"/api/orders/9d1b29fc-4240-4993-82c5-a42d00900324/scandata\"},"
		+ "\"orderstate\":[  {\"href\":\"/api/orders/9d1b29fc-4240-4993-82c5-a42d00900324/orderstate?orderState=Accepted\",\"title\":\"Accept\"},"
		+ "{\"href\":\"/api/orders/9d1b29fc-4240-4993-82c5-a42d00900324/orderstate?orderState=Declined\",\"title\":\"Decline\"} ]}}";
		NakedResource hal = mapper.readValue(json, NakedResource.class);
		assertTrue(hal.hasLinks());
		assertTrue(hal.hasProperties());
		assertEquals("9d1b29fc-4240-4993-82c5-a42d00900324", hal.getProperty("SessionId"));
		assertTrue(hal.getLinksByRel().containsKey("self"));
		assertTrue(hal.getLinksByRel().containsKey("orderstate"));
		List<Link> links = hal.getLinksByRel().get("orderstate");
		assertNotNull(links);
		assertEquals(2, links.size());
		assertEquals("/api/orders/9d1b29fc-4240-4993-82c5-a42d00900324/orderstate?orderState=Accepted", links.get(0).getHref());
		assertEquals("/api/orders/9d1b29fc-4240-4993-82c5-a42d00900324/orderstate?orderState=Declined", links.get(1).getHref());
	}


	private NakedResource whenReadingFromFile(String filename) throws IOException {
		try(InputStream inputStream = this.getClass().getResourceAsStream(filename)) {
			return mapper.readValue(inputStream,NakedResource.class);
		}
	}

	@Test
	public void shouldDeserializeResourceWithArrays() throws IOException {

		NakedResource halResource = whenReadingFromFile("resource-with-arrays.json");

		assertNotNull(halResource);
		assertEquals(halResource.getProperty("name"),"root");

		List<Namespace> namespaces = halResource.getNamespaces();
		assertNotNull(namespaces);
		assertEquals(namespaces.size(), 2);
		assertEquals(namespaces.get(0),new Namespace("ns:1", "/namespaces/1"));
		assertEquals(namespaces.get(1),new Namespace("ns:2", "/namespaces/2"));

		List<Link> links = halResource.getLinks();
		assertNotNull(links);
		assertEquals(links.size(),2);
		LinkBuilder l = new DefaultLinkBuilder();

		assertEquals(links.get(0),l.rel("self").urlPattern("/something").build());
		assertEquals(links.get(1), l.rel("self").urlPattern("/something/{templated}").set("templated", "true").build());

		List<Resource> childrenList = halResource.getResources("children");
		assertNotNull(childrenList);
		assertEquals(childrenList.size(),2);
		assertEquals(childrenList.get(0).getProperty("name"),"child 1");
		assertEquals(childrenList.get(1).getProperty("name"),"child 2");
	}


}
