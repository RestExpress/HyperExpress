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

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.strategicgains.hyperexpress.RelTypes;
import com.strategicgains.hyperexpress.annotation.BindToken;
import com.strategicgains.hyperexpress.domain.Link;

/**
 * @author toddf
 * @since Oct 18, 2013
 */
public class DefaultLinkBuilderTest
{
	private static final String BASE_URL = "http://localhost:8081";
	private static final String URL_PATTERN = "/{id}";
	private static final String URL_PATTERN2 = "/{rootId}/{secondaryId}/{id}";

	@Test
	public void shouldBuildSimpleSingleIdTemplate()
	{
		Link link = new DefaultLinkBuilder(URL_PATTERN)
			.baseUrl(BASE_URL)
			.rel(RelTypes.SELF)
			.build(new DefaultTokenResolver()
				.bind("id", "42")
			);
		
		assertEquals(BASE_URL + "/42", link.getHref());
		assertEquals(RelTypes.SELF, link.getRel());
	}

	@Test
	public void shouldBuildComplexSingleIdTemplate()
	{
		TokenResolver r = new DefaultTokenResolver()
			.bind("rootId", "first")
			.bind("secondaryId", "second")
			.bind("id", "42")
			.bind("ignored", "ignored");

		Link link = new DefaultLinkBuilder(URL_PATTERN2)
			.baseUrl(BASE_URL)
			.rel(RelTypes.DESCRIBED_BY)
			.build(r);
		
		assertEquals(BASE_URL + "/first/second/42", link.getHref());
		assertEquals(RelTypes.DESCRIBED_BY, link.getRel());
	}

	@Test
	public void shouldBuildComplexQueryString()
	{
		String expectedUrl = "http://someserver/myapp/report/1234?accountId=400&accountId=401&accountId=402";
		
		  //--- the list of IDs would be variable...
		List<String> accountIds = Arrays.asList("400", "401", "402");

		LinkBuilder lb = new DefaultLinkBuilder("/myapp/report/{reportId}")
			.baseUrl("http://someserver")
			.withQuery("accountId={accountId}");
		TokenResolver r = new DefaultTokenResolver()
			.bind("reportId", "1234")
			.bind("accountId", accountIds);

		Link link = lb.build(r);
		assertEquals(expectedUrl, link.getHref());
	}

	@Test
	public void shouldBuildComplexQueryStringFromArray()
	{
		String expectedUrl = "http://someserver/myapp/report/1234?accountId=400&accountId=401&accountId=402";

		LinkBuilder lb = new DefaultLinkBuilder("/myapp/report/{reportId}")
			.baseUrl("http://someserver")
			.withQuery("accountId={accountId}");
		TokenResolver r = new DefaultTokenResolver()
			.bind("reportId", "1234")
			//--- the list of IDs would be variable...
			.bind("accountId", "400", "401", "402");

		Link link = lb.build(r);
		assertEquals(expectedUrl, link.getHref());
	}

	@Test
	public void shouldBuildQueryStringWithNoResolver()
	{
		String expectedUrl = "http://someserver/myapp/report/1234?param1=val1";

		Link link = new DefaultLinkBuilder("/myapp/report/1234")
		    .baseUrl("http://someserver")
		    .withQuery("param1=val1")
		    .build();

		assertEquals(expectedUrl, link.getHref());
	}

	@Test
	public void shouldAllowMissingRel()
	{
		new DefaultLinkBuilder(URL_PATTERN).build(new DefaultTokenResolver());
	}

	@Test
	public void shouldBindFromObject()
	{
		ToBindAnnotated tba = new ToBindAnnotated();
		tba.id = "12345";

		Link link = new DefaultLinkBuilder(URL_PATTERN).baseUrl(BASE_URL).build(tba, new DefaultTokenResolver(true));
		assertEquals(BASE_URL + "/12345", link.getHref());
	}

	public class ToBindAnnotated
	{
		@BindToken("id")
		String id;
	}
}
