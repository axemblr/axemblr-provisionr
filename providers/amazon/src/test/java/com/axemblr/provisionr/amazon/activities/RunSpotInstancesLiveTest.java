/*
* Copyright (c) 2012 S.C. Axemblr Software Solutions S.R.L
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.axemblr.provisionr.amazon.activities;

import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class RunSpotInstancesLiveTest extends RunInstancesLiveTest<RunSpotInstances> {
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		
		// TODO: is adding the bid as an option for the pool ok?
		Map<String, String> options = new HashMap<String, String>();
		options.put("bid", "0.03");
		when(pool.getOptions()).thenReturn(options);
	}
	
	@Test
	public void testRunSpotInstances() {
		// TODO 
	}
	
}
