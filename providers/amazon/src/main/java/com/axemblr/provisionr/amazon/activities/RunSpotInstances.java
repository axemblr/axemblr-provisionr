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

import com.amazonaws.services.ec2.AmazonEC2;
import com.axemblr.provisionr.amazon.core.ProviderClientCache;
import com.axemblr.provisionr.api.pool.Pool;
import org.activiti.engine.delegate.DelegateExecution;


public class RunSpotInstances extends AmazonActivity {

    public RunSpotInstances(ProviderClientCache cache) {
        super(cache);
    }

    @Override
    public void execute(AmazonEC2 client, Pool pool, DelegateExecution execution) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
