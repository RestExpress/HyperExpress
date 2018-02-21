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
package com.strategicgains.hyperexpress.serialization.jackson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.strategicgains.hyperexpress.domain.Link;
import com.strategicgains.hyperexpress.domain.Namespace;
import com.strategicgains.hyperexpress.domain.Resource;
import com.strategicgains.hyperexpress.domain.naked.NakedLink;
import com.strategicgains.hyperexpress.domain.naked.NakedResource;

/**
 * @author toddf
 * @since May 2, 2014
 */
public class NakedResourceSerializer
extends JsonSerializer<NakedResource>
{
	public NakedResourceSerializer()
	{
		super();
	}

	@Override
	public void serialize(NakedResource resource, JsonGenerator jgen, SerializerProvider provider)
	throws IOException, JsonProcessingException
	{
		jgen.writeStartObject();
		renderJson(resource, jgen, false);
		jgen.writeEndObject();
	}

	private void renderJson(NakedResource resource, JsonGenerator jgen, boolean isEmbedded)
	throws JsonGenerationException, IOException
	{
		Set<Object> linkRels = getLinkRels(resource);
		Set<Object> propertyNames = getPropertyNames(resource);
		writeLinks(resource, propertyNames, isEmbedded, jgen);
		writeProperties(resource, jgen);
		writeEmbedded(resource, linkRels, propertyNames, jgen);
	}

	private Set<Object> getPropertyNames(NakedResource resource)
	{
		return resource.getProperties().keySet().stream().collect(Collectors.toSet());
	}

	private Set<Object> getLinkRels(NakedResource resource)
	{
		return resource.getLinks().stream().map(new Function<Link, String>()
		{
			@Override
			public String apply(Link t)
			{
				return t.getRel();
			}
		}).collect(Collectors.toSet());
	}

	private void writeLinks(NakedResource resource, Set<Object> propertyNames, boolean isEmbedded, JsonGenerator jgen)
	throws JsonGenerationException, IOException
	{
		List<Link> links = resource.getLinks();
		List<Namespace> namespaces = resource.getNamespaces();
		if (links.isEmpty() && (isEmbedded || namespaces.isEmpty())) return;

		Map<String, List<NakedLink>> linksByRel = indexLinksByRel(resource.getLinks());

		for (Entry<String, List<NakedLink>> entry : linksByRel.entrySet())
		{
			if (propertyNames.contains(entry.getKey())) continue;

			if (entry.getValue().size() == 1 && !resource.isMultipleLinks(entry.getKey())) // Write single link
			{
				NakedLink link = entry.getValue().iterator().next();

				if (null == link.getTemplated())
				{
					link.setTemplated(link.hasTemplate() ? true : null);
				}

				jgen.writeObjectField(entry.getKey(), link);
			}
			else // Write link array
			{
				jgen.writeArrayFieldStart(entry.getKey());

				for (NakedLink link : entry.getValue())
				{
					if (null == link.getTemplated())
					{
						link.setTemplated(link.hasTemplate() ? true : null);
					}

					jgen.writeObject(link);
				}

				jgen.writeEndArray();
			}
		}
	}

	private Map<String, List<NakedLink>> indexLinksByRel(List<Link> links)
	{
		Map<String, List<NakedLink>> linksByRel = new HashMap<String, List<NakedLink>>();

		for (Link link : links)
		{
			List<NakedLink> linksForRel = linksByRel.get(link.getRel());

			if (linksForRel == null)
			{
				linksForRel = new ArrayList<NakedLink>();
				linksByRel.put(link.getRel(), linksForRel);
			}

			linksForRel.add(new NakedLink(link));
		}

		return linksByRel;
	}

	private void writeEmbedded(Resource resource, Set<Object> linkRels, Set<Object> propertyNames, JsonGenerator jgen)
	throws JsonGenerationException, IOException
	{
		Map<String, List<Resource>> embedded = resource.getResources();

		if (embedded.isEmpty()) return;

		for (Entry<String, List<Resource>> entry : embedded.entrySet())
		{
			if (linkRels.contains(entry.getKey()) || propertyNames.contains(entry.getKey())) continue;

			if (entry.getValue().size() == 1 && !resource.isMultipleResources(entry.getKey()))
			{
				jgen.writeObjectFieldStart(entry.getKey());
                renderJson((NakedResource) entry.getValue().iterator().next(), jgen, true);
                jgen.writeEndObject();
			}
			else
			{
				jgen.writeArrayFieldStart(entry.getKey());

				for (Resource r : entry.getValue())
				{
					jgen.writeStartObject();
					renderJson((NakedResource) r, jgen, true);
					jgen.writeEndObject();
				}

                jgen.writeEndArray();
			}
		}
	}

	private void writeProperties(Resource resource, JsonGenerator jgen)
	throws JsonProcessingException, IOException
	{
		Map<String, Object> properties = resource.getProperties();

		if (properties.isEmpty()) return;

		for (Entry<String, Object> entry : properties.entrySet())
		{
			jgen.writeObjectField(entry.getKey(), entry.getValue());
		}
	}
}
