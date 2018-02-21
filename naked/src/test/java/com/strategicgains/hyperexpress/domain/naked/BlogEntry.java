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
package com.strategicgains.hyperexpress.domain.naked;

import java.util.UUID;

/**
 * @author toddf
 * @since Apr 8, 2014
 */
public class BlogEntry
extends AbstractEntity
{
	private Reference<Blog> blog;
	private Reference<User> author;
	private String title;
	private String content;
	private String tags;

	public UUID getAuthorId()
	{
		return author == null ? null : author.getId();
	}

	public void setAuthor(User author)
	{
		this.author = (author == null ? null : new Reference<User>(author));
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public String getTags()
	{
		return tags;
	}

	public void setTags(String tags)
	{
		this.tags = tags;
	}

	public UUID getBlogId()
	{
		return blog == null ? null : blog.getId();
	}

	public void setBlog(Blog blog)
	{
		this.blog = (blog == null ? null : new Reference<Blog>(blog));
	}
}
