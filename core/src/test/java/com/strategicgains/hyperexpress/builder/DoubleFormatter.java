/*
    Copyright 2015, Strategic Gains, Inc.

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

import com.strategicgains.hyperexpress.annotation.TokenFormatter;

/**
 * @author toddf
 * @since Aug 27, 2015
 */
public class DoubleFormatter
implements TokenFormatter
{
	@Override
	public String format(Object field)
	{
		return String.format("%4.2f", field);
	}
}
