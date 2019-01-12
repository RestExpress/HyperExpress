/*
    Copyright 2013, Strategic Gains, Inc.

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
package com.strategicgains.hyperexpress.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.strategicgains.hyperexpress.RelTypes;
import com.strategicgains.hyperexpress.annotation.BindToken;

/**
 * @author toddf
 * @since Oct 18, 2013
 */
public class DefaultConditionalLinkBuilderTest
{
	private static final String BASE_URL = "http://localhost:8081";

	@Test
	public void shouldBuildHonorNotBoundAndOptional()
	{
		ConditionalLinkBuilder lb = (ConditionalLinkBuilder) new DefaultConditionalLinkBuilder("/clients?offset={nextOffset}")
			.baseUrl(BASE_URL)
			.rel(RelTypes.SELF)
			.withQuery("limit={limit}")
			.withQuery("filter={filter}")
			.withQuery("sort={sort}");
		lb.ifNotBound("accountId");
		lb.optional();

		ToBeBound tbb = new ToBeBound();
		assertNull(lb.build());
		assertNull(lb.build(new DefaultTokenResolver()));
		assertNull(lb.build(new DefaultTokenResolver(true)));
		assertNull(lb.build(tbb, new DefaultTokenResolver(true)));
		tbb.id = "12345";
		assertNull(lb.build());
		assertNull(lb.build(new DefaultTokenResolver()));
		assertNull(lb.build(new DefaultTokenResolver(true)));
		assertNull(lb.build(tbb, new DefaultTokenResolver(true)));
		tbb.offset = "42";
		assertNull(lb.build());
		assertNull(lb.build(new DefaultTokenResolver()));
		assertNull(lb.build(new DefaultTokenResolver(true)));
		assertNull(lb.build(tbb, new DefaultTokenResolver(true)));
		tbb.id = null;
		assertNull(lb.build());
		assertNull(lb.build(new DefaultTokenResolver()));
		assertNull(lb.build(new DefaultTokenResolver(true)));
		assertEquals(BASE_URL + "/clients?offset=42", lb.build(tbb, new DefaultTokenResolver(true)).getHref());
	}

	@Test
	public void shouldBuildHonorBoundAndOptional()
	{
		ConditionalLinkBuilder lb = (ConditionalLinkBuilder) new DefaultConditionalLinkBuilder("/accounts/{accountId}/clients?offset={nextOffset}")
			.baseUrl(BASE_URL)
			.rel(RelTypes.SELF)
			.withQuery("limit={limit}")
			.withQuery("filter={filter}")
			.withQuery("sort={sort}");
		lb.ifBound("accountId");
		lb.optional();

		ToBeBound tbb = new ToBeBound();
		assertNull(lb.build());
		assertNull(lb.build(new DefaultTokenResolver()));
		assertNull(lb.build(new DefaultTokenResolver(true)));
		assertNull(lb.build(tbb, new DefaultTokenResolver(true)));
		tbb.id = "12345";
		assertNull(lb.build());
		assertNull(lb.build(new DefaultTokenResolver()));
		assertNull(lb.build(new DefaultTokenResolver(true)));
		assertNull(lb.build(tbb, new DefaultTokenResolver(true)));
		tbb.offset = "42";
		assertNull(lb.build());
		assertNull(lb.build(new DefaultTokenResolver()));
		assertNull(lb.build(new DefaultTokenResolver(true)));
		assertEquals(BASE_URL + "/accounts/12345/clients?offset=42", lb.build(tbb, new DefaultTokenResolver(true)).getHref());
		tbb.offset = null;
		assertNull(lb.build());
		assertNull(lb.build(new DefaultTokenResolver()));
		assertNull(lb.build(new DefaultTokenResolver(true)));
		assertNull(lb.build(tbb, new DefaultTokenResolver(true)));
	}

	public class ToBeBound
	{
		@BindToken("accountId")
		String id;

		@BindToken("nextOffset")
		String offset;
	}
}
