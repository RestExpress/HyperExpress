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

import org.restexpress.ContentType;
import org.restexpress.RestExpress;
import org.restexpress.plugin.AbstractPlugin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.strategicgains.hyperexpress.HyperExpress;
import com.strategicgains.hyperexpress.ResourceFactoryStrategy;
import com.strategicgains.hyperexpress.domain.Resource;
import com.strategicgains.hyperexpress.domain.hal.HalResourceFactory;
import com.strategicgains.hyperexpress.domain.siren.SirenResourceFactory;
import com.strategicgains.hyperexpress.expand.Expander;
import com.strategicgains.hyperexpress.expand.ExpansionCallback;

/**
 * This plugin will convert any domain models returned from controller methods
 * into a Resource implementation based on the Accept header. It will render HAL
 * for both application/json and application/hal+json.
 * <p/>
 * To override this behavior, use the addResourceFactory() method, which puts
 * you in complete control of the behavior, not doing any default mappings.
 * <p/>
 * In other words:<br/>
 * <code>
 * HalResourceFactory hal = new HalResourceFactory();
 * plugin.addResourceFactory(hal, ContentType.HAL_JSON);
 * </code> Will cause the service suite to only support links in
 * 'application/hal+json' content types. Note that there are possibly other
 * configuration options on the factory, such as HalResourceFactory, which can
 * be configured before passing it to addResourceFactory().
 * 
 * @author toddf
 * @since May 7, 2014
 */
public class HyperExpressPlugin
extends AbstractPlugin
{
	private Class<?> domainMarkerClass;
	private boolean usesCustomFactory = false;

	/**
	 * Default constructor. Use this constructor if your domain classes
	 * implement the Linkable interface.
	 * 
	 * @see Linkable
	 */
	public HyperExpressPlugin()
	{
		this(Linkable.class);
	}

	/**
	 * Use this constructor to indicate the base class or interface that your
	 * domain model extends. Instances of this marker class will be converted to
	 * HyperExpress Resources and links injected into them.
	 * 
	 * @param domainMarkerClass
	 *            the base class or interface to indicate your linkable domain
	 *            objects.
	 */
	public HyperExpressPlugin(Class<?> domainMarkerClass)
	{
		super();
		this.domainMarkerClass = domainMarkerClass;
	}

	/**
	 * Register the HyperExpress plugin with a RestExpress server instance. Must
	 * occur before RestExpress.bind().
	 * 
	 * @param server
	 *            the RestExpress server instance.
	 */
	@Override
	public HyperExpressPlugin register(RestExpress server)
	{
		if (isRegistered()) return this;

		server.addPreprocessor(new RequestHeaderTokenBinder())
		    .addPostprocessor(new HyperExpressPostprocessor(domainMarkerClass));

		return (HyperExpressPlugin) super.register(server);
	}

	@Override
	public void bind(RestExpress server)
	{
		if (!usesCustomFactory)
		{
			HalResourceFactory hal = new HalResourceFactory();
			hal.excludeAnnotations(JsonIgnore.class);
			HyperExpress.registerResourceFactoryStrategy(hal, ContentType.JSON);
			HyperExpress.registerResourceFactoryStrategy(hal, ContentType.HAL_JSON);

			SirenResourceFactory siren = new SirenResourceFactory();
			siren.excludeAnnotations(JsonIgnore.class);
			HyperExpress.registerResourceFactoryStrategy(siren, ContentType.SIREN);
		}

		super.bind(server);
	}

	/**
	 * Add a custom {@link ResourceFactoryStrategy} for a given content type.
	 * 
	 * @param factoryStrategy
	 * @param contentType
	 * @return
	 */
	public HyperExpressPlugin addResourceFactory(ResourceFactoryStrategy factoryStrategy, String contentType)
	{
		HyperExpress.registerResourceFactoryStrategy(factoryStrategy, contentType);
		usesCustomFactory = true;
		return this;
	}

	/**
	 * Convenience method to register an {@link ExpansionCallback}
	 * implementation with HyperExpress. This method actually simply registers
	 * with Expander.
	 * 
	 * @param type
	 *            the model type that, once converted to a {@link Resource}
	 *            should be sent to the callback.
	 * @param callback
	 *            an {@link ExpansionCallback} instance.
	 */
	public HyperExpressPlugin registerCallback(Class<?> type, ExpansionCallback callback)
	{
		Expander.registerCallback(type, callback);
		return this;
	}
}
