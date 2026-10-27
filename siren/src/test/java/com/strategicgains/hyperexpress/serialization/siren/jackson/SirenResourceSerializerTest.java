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
package com.strategicgains.hyperexpress.serialization.siren.jackson;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.SimpleDateFormat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.strategicgains.hyperexpress.builder.DefaultLinkBuilder;
import com.strategicgains.hyperexpress.builder.LinkBuilder;
import com.strategicgains.hyperexpress.domain.Resource;
import com.strategicgains.hyperexpress.domain.siren.SirenAction;
import com.strategicgains.hyperexpress.domain.siren.SirenField;
import com.strategicgains.hyperexpress.domain.siren.SirenResource;

/**
 * @author toddf
 * @since Sep 25, 2014
 */
public class SirenResourceSerializerTest
{
	private static ObjectMapper mapper = new ObjectMapper();

	@BeforeAll
	public static void setUpBeforeClass() throws Exception
	{
		SimpleModule module = new SimpleModule();
		module.addSerializer(SirenResource.class, new SirenResourceSerializer());
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
	public void shouldNotSerializeSingleNamespace()
	throws JsonProcessingException
	{
		Resource r = new SirenResource();
		r.addNamespace("ns:1", "/namespaces/1");
		String json = mapper.writeValueAsString(r);
		assertEquals("{}", json);
	}

	@Test
	public void shouldNotSerializeNamespaceArray()
	throws JsonProcessingException
	{
		Resource r = new SirenResource();
		r.addNamespace("ns:1", "/namespaces/1");
		r.addNamespace("ns:2", "/namespaces/2");
		String json = mapper.writeValueAsString(r);
		assertEquals("{}", json);
	}

	@Test
	public void shouldSerializeSingleLink()
	throws JsonProcessingException
	{
		Resource r = new SirenResource();
		LinkBuilder l = new DefaultLinkBuilder();
		r.addLink(l.rel("self").urlPattern("/something").build());
		String json = mapper.writeValueAsString(r);
		assertEquals("{\"links\":[{\"rel\":[\"self\"],\"href\":\"/something\"}]}", json);
	}

	@Test
	public void shouldSerializeTemplated()
	throws JsonProcessingException
	{
		Resource r = new SirenResource();
		LinkBuilder l = new DefaultLinkBuilder();
		r.addLink(l.rel("self").urlPattern("/something/{templated}").build());
		String json = mapper.writeValueAsString(r);
		assertEquals("{\"links\":[{\"rel\":[\"self\"],\"href\":\"/something/{templated}\"}]}", json);
	}

	@Test
	public void shouldSerializeTemplatedArray()
	throws JsonProcessingException
	{
		Resource r = new SirenResource();
		LinkBuilder l = new DefaultLinkBuilder();
		r.addLink(l.rel("self").urlPattern("/something/not_templated").build());
		r.addLink(l.rel("self").urlPattern("/something/{templated}").build());
		String json = mapper.writeValueAsString(r);
		assertEquals("{\"links\":[{\"rel\":[\"self\"],\"href\":\"/something/not_templated\"},{\"rel\":[\"self\"],\"href\":\"/something/{templated}\"}]}", json);
	}

	@Test
	public void shouldSerializeLinkArray()
	throws JsonProcessingException
	{
		Resource r = new SirenResource();
		LinkBuilder l = new DefaultLinkBuilder();
		r.addLink(l.rel("self").urlPattern("/something").build());
		r.addLink(l.rel("self").urlPattern("/something/else").build());
		String json = mapper.writeValueAsString(r);
		assertEquals("{\"links\":[{\"rel\":[\"self\"],\"href\":\"/something\"},{\"rel\":[\"self\"],\"href\":\"/something/else\"}]}", json);
	}

	@Test
	public void shouldCollapseMatchingUrls()
	throws JsonProcessingException
	{
		Resource r = new SirenResource();
		LinkBuilder l = new DefaultLinkBuilder();
		r.addLink(l.rel("self").urlPattern("/something/self").build());
		r.addLink(l.rel("alternate").urlPattern("/something/self").build());
		String json = mapper.writeValueAsString(r);
		assertEquals("{\"links\":[{\"rel\":[\"self\",\"alternate\"],\"href\":\"/something/self\"}]}", json);
	}

	@Test
	public void shouldSerializeProperties()
	throws JsonProcessingException
	{
		Resource r = new SirenResource();
		r.addProperty("name", "A Siren resource");
		r.addProperty("value", new Integer(42));
		String json = mapper.writeValueAsString(r);
		assertEquals("{\"properties\":{\"name\":\"A Siren resource\",\"value\":42}}", json);
	}

	@Test
	public void shouldSerializeSingleEntity()
	throws JsonProcessingException
	{
		Resource r = new SirenResource();
		r.addResource("children", new SirenResource().addProperty("name", "child"));
		String json = mapper.writeValueAsString(r);
		assertEquals("{\"entities\":[{\"rel\":[\"children\"],\"properties\":{\"name\":\"child\"}}]}", json);
	}

	@Test
	public void shouldSerializeSingleEntityAgain()
	throws JsonProcessingException
	{
		Resource r = new SirenResource().addProperty("name", "root");
		r.addResource("children", new SirenResource().addProperty("name", "child"), true);
		String json = mapper.writeValueAsString(r);
		assertEquals("{\"entities\":[{\"rel\":[\"children\"],\"properties\":{\"name\":\"child\"}}],\"properties\":{\"name\":\"root\"}}", json);
	}

	@Test
	public void shouldSerializeEmbeddedArray()
	throws JsonProcessingException
	{
		Resource r = new SirenResource().addProperty("name", "root");
		r.addResource("children", new SirenResource().addProperty("name", "child 1"));
		r.addResource("children", new SirenResource().addProperty("name", "child 2"));
		String json = mapper.writeValueAsString(r);
		assertEquals("{\"entities\":[{\"rel\":[\"children\"],\"properties\":{\"name\":\"child 1\"}},{\"rel\":[\"children\"],\"properties\":{\"name\":\"child 2\"}}],\"properties\":{\"name\":\"root\"}}", json);
	}

	@Test
	public void shouldSerializeResource()
	throws JsonProcessingException
	{
		Resource r = new SirenResource().addProperty("name", "root");
		r.addNamespace("ns:1", "/namespaces/1");
		LinkBuilder l = new DefaultLinkBuilder();
		r.addLink(l.rel("self").urlPattern("/something").build());
		r.addResource("children", new SirenResource().addProperty("name", "child"));
		String json = mapper.writeValueAsString(r);
		assertEquals("{\"links\":[{\"rel\":[\"self\"],\"href\":\"/something\"}],\"entities\":[{\"rel\":[\"children\"],\"properties\":{\"name\":\"child\"}}],\"properties\":{\"name\":\"root\"}}", json);
	}

	@Test
	public void shouldSerializeResourceWithArrays()
	throws JsonProcessingException
	{
		Resource r = new SirenResource().addProperty("name", "root");
		r.addNamespace("ns:1", "/namespaces/1");
		r.addNamespace("ns:2", "/namespaces/2");
		LinkBuilder l = new DefaultLinkBuilder();
		r.addLink(l.rel("self").urlPattern("/something").build());
		r.addLink(l.rel("self").urlPattern("/something/{templated}").build());
		r.addResource("children", new SirenResource().addProperty("name", "child 1"));
		r.addResource("children", new SirenResource().addProperty("name", "child 2"));
		String json = mapper.writeValueAsString(r);
		assertEquals("{\"links\":[{\"rel\":[\"self\"],\"href\":\"/something\"},{\"rel\":[\"self\"],\"href\":\"/something/{templated}\"}],\"entities\":[{\"rel\":[\"children\"],\"properties\":{\"name\":\"child 1\"}},{\"rel\":[\"children\"],\"properties\":{\"name\":\"child 2\"}}],\"properties\":{\"name\":\"root\"}}", json);
	}

	@Test
	public void shouldSerializeActions()
	throws JsonProcessingException
	{
		SirenResource r = new SirenResource();
		SirenAction action = new SirenAction()
			.setName("add-item")
			.setTitle("Add an Item")
			.setMethod("POST")
			.setType("application/x-www-form-urlencoded")
			.setHref("http://api.x.io/orders/42/items");
		action.addField(new SirenField()
			.setName("orderNumber")
			.setType("hidden")
			.setValue("42"));
		action.addField(new SirenField()
			.setName("productCode")
			.setType("text"));
		action.addField(new SirenField()
			.setName("quantity")
			.setType("number"));
		r.addAction(action);
		String json = mapper.writeValueAsString(r);
		assertEquals("{\"actions\":[{"
			+ "\"name\":\"add-item\","
			+ "\"title\":\"Add an Item\","
			+ "\"method\":\"POST\","
			+ "\"href\":\"http://api.x.io/orders/42/items\","
			+ "\"type\":\"application/x-www-form-urlencoded\","
			+ "\"fields\":["
			+ "{\"name\":\"orderNumber\",\"type\":\"hidden\",\"value\":\"42\"},"
			+ "{\"name\":\"productCode\",\"type\":\"text\"},"
			+ "{\"name\":\"quantity\",\"type\":\"number\"}]}]}", json);
	}
}
