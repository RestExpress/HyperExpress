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
package com.strategicgains.hyperexpress.domain.naked;

import com.strategicgains.hyperexpress.AbstractResourceFactoryStrategy;
import com.strategicgains.hyperexpress.domain.Resource;

/**
 * NakedResourceFactory is a ResourceFactoryStrategy implementation that creates
 * NakedJSON-style Resource implementations (e.g. NakedResource).
 * <p/>
 * It can be used by itself or added to a ResourceFactory implemenation, such as
 * DefaultResourceFactory, with a given content type.
 * 
 * @author toddf
 * @since Apr 11, 2014
 */
public class NakedResourceFactory
extends AbstractResourceFactoryStrategy
{
	@Override
	public Resource createResource(Object object)
	{
		Resource r = new NakedResource();

		if (object != null)
		{
			copyProperties(object, r);
		}

		return r;
	}

	@Override
    public Class<? extends Resource> getResourceType()
    {
	    return NakedResource.class;
    }
}
