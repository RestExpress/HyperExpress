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

import org.restexpress.Request;
import org.restexpress.pipeline.Preprocessor;

import com.strategicgains.hyperexpress.HyperExpress;

/**
 * Iterates the headers on the request and binds them to the HyperExpress URL token binder.
 * This supports automatic binding of incoming IDs, etc. on the URL to outbound links created
 * in Resource instances for the response.
 * <p/>
 * If there is more-than one value for a given header, the first one is bound to the token.
 * <p/>
 * These automatic bindings are overridden by calls to HyperExpress.bindToken() and @BindToken
 * annotations on your domain model.
 * 
 * @author toddf
 * @since May 7, 2014
 * @see HyperExpress
 */
public class RequestHeaderTokenBinder
implements Preprocessor
{
	private static final String BASE_URL_TOKEN = "baseUrl";
	private static final String REFERER_HEADER_NAME = "referer";

	@Override
	public void process(Request request)
	{
		String host = request.getHeader(REFERER_HEADER_NAME);

		if (host == null)
		{
			host = request.getHost();
		}

		if (host != null)
		{
			HyperExpress.bind(BASE_URL_TOKEN, request.getProtocol() + "://" + host);
		}

		for (String token : request.getHeaderNames())
		{
			HyperExpress.bind(token, request.getHeader(token));
		}
	}
}
