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
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.strategicgains.hyperexpress.domain.naked.NakedResource;

/**
 * @author toddf
 * @since May 21, 2014
 */
public class NakedResourceDeserializer
extends JsonDeserializer<NakedResource>
{
	public NakedResourceDeserializer()
    {
		super();
    }

	@Override
	public NakedResource deserialize(JsonParser jp, DeserializationContext context)
	throws IOException, JsonProcessingException
	{
		ObjectCodec oc = jp.getCodec();
		return deserializeResource((JsonNode) jp.readValueAsTree(), oc);
	}

	private NakedResource deserializeResource(JsonNode root, ObjectCodec oc)
	throws JsonProcessingException, IOException
    {
		NakedResource resource = new NakedResource();
		processProperties(root, resource, oc);
		return resource;
    }

	private void processProperties(JsonNode root, NakedResource resource, ObjectCodec oc)
	throws JsonProcessingException, IOException
    {
		if (root == null) return;

		Iterator<Map.Entry<String, JsonNode>> fields = root.fields();

		while (fields.hasNext())
		{
			Map.Entry<String, JsonNode> fieldEntry = fields.next();

			if (fieldEntry.getValue().isArray())
			{
				Iterator<JsonNode> values = fieldEntry.getValue().elements();

				while (values.hasNext())
				{
					JsonNode value = values.next();
					resource.addResource(fieldEntry.getKey(), deserializeResource(value, oc));
				}
			}
			else
			{
				resource.addResource(fieldEntry.getKey(), deserializeResource(fieldEntry.getValue(), oc));
			}
		}
	}
}
