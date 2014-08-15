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
package org.restexpress.plugin.hyperexpress;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;

import org.restexpress.Request;
import org.restexpress.Response;
import org.restexpress.pipeline.Postprocessor;
import org.restexpress.plugin.hyperexpress.expand.ExpansionParser;

import com.strategicgains.hyperexpress.HyperExpress;
import com.strategicgains.hyperexpress.domain.Resource;
import com.strategicgains.hyperexpress.exception.ResourceException;
import com.strategicgains.hyperexpress.expand.Expander;
import com.strategicgains.hyperexpress.expand.Expansion;
import com.strategicgains.hyperexpress.util.Strings;

/**
 * If the Response contains an instance of the given domainMarkerClass, or a Collection (or Array)
 * of the same, generates a HyperExpress Resource from it, injecting links and namespaces
 * as applicable.
 * <p/>
 * This postprocessor works in conjunction with HyperExpress relationship definitions, token bindings
 * and Jackson-based custom serializers. It will copy any extenders of the domainMarkerClass into
 * an instance of Resource, depending on the content type of the requested Accept header.
 * 
 * @author toddf
 * @since Apr 21, 2014
 * @see HyperExpress
 */
public class HyperExpressPostprocessor
implements Postprocessor
{
	private Class<?> resourceMarker;

	public HyperExpressPostprocessor(Class<?> resourceMarkerClass)
	{
		super();
		this.resourceMarker = resourceMarkerClass;
	}

    @Override
	public void process(Request request, Response response)
	{
		Object body = response.getBody();

		if (body == null || !response.isSerialized()) return;

		Resource resource = null;
		Class<?> bodyClass = body.getClass();
		Expansion expansion = ExpansionParser.parseFrom(request, response);

		try
		{
			if (isMarkerClass(bodyClass))
			{
				resource = HyperExpress.createResource(body, expansion.getMediaType());
				Expander.expand(expansion, bodyClass, resource);
			}
			else if (isCollection(bodyClass))
			{
				Type type = request.getResolvedRoute().getAction().getGenericReturnType();
	
				if (type instanceof ParameterizedType)
				{
					Type t = (((ParameterizedType) type).getActualTypeArguments())[0];
	
					if (resourceMarker.isAssignableFrom((Class<?>) t))
					{
						// TODO: do sensible defaults, but allow caller to set 'rel'.
						String componentRel = Strings.pluralize(((Class<?>) t).getSimpleName().toLowerCase());
						resource = HyperExpress.createCollectionResource((Collection<?>) body, (Class<?>) t, componentRel, expansion.getMediaType());
				    	Expander.expand(expansion, (Class<?>) t, resource.getResources(componentRel));
					}
				}
			}
			else if (bodyClass.isArray())
			{
				if (isMarkerClass(bodyClass.getComponentType()))
				{
					// TODO: do sensible defaults, but allow caller to set 'rel'.
					String componentRel = Strings.pluralize(bodyClass.getComponentType().getSimpleName().toLowerCase());
					resource = HyperExpress.createCollectionResource(Arrays.asList((Object[]) body),
						bodyClass.getComponentType(), componentRel, expansion.getMediaType());
			    	Expander.expand(expansion, bodyClass.getComponentType(), resource.getResources(componentRel));
				}
			}
		}
		catch (ResourceException e)
		{
			// log the exception and move on...
		}

		if (resource != null)
		{
			response.setBody(resource);
		}

		HyperExpress.clearTokenBindings();
	}

	private boolean isMarkerClass(Class<?> aClass)
	{
		return resourceMarker.isAssignableFrom(aClass);
	}

	private boolean isCollection(Class<?> aClass)
	{
		return (Collection.class.isAssignableFrom(aClass));
	}
}
