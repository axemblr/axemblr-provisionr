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

package com.axemblr.provisionr.core.activiti;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.sql.DataSource;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.activiti.engine.impl.jobexecutor.FailedJobCommandFactory;
import org.activiti.engine.impl.jobexecutor.JobExecutor;

public class ConfigurationFactory {

    private DataSource dataSource;
    private String databaseSchemaUpdate;

    private boolean jobExecutorActivate = true;
    private JobExecutor jobExecutor;

    private FailedJobCommandFactory failedJobCommandFactory;

    public StandaloneProcessEngineConfiguration getConfiguration() {
        StandaloneProcessEngineConfiguration conf = new StandaloneProcessEngineConfiguration();

        conf.setDataSource(dataSource);
        conf.setDatabaseSchemaUpdate(databaseSchemaUpdate);

        conf.setJobExecutorActivate(jobExecutorActivate);
        conf.setJobExecutor(jobExecutor);

        conf.setFailedJobCommandFactory(failedJobCommandFactory);

        return conf;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = checkNotNull(dataSource, "dataSource is null");
    }

    public void setDatabaseSchemaUpdate(String databaseSchemaUpdate) {
        this.databaseSchemaUpdate = checkNotNull(databaseSchemaUpdate, "databaseSchemaUpdate is null");
    }

    public void setJobExecutorActivate(boolean jobExecutorActivate) {
        this.jobExecutorActivate = jobExecutorActivate;
    }

    public void setJobExecutor(JobExecutor jobExecutor) {
        this.jobExecutor = checkNotNull(jobExecutor, "jobExecutor is null");
    }

    public void setFailedJobCommandFactory(FailedJobCommandFactory failedJobCommandFactory) {
        this.failedJobCommandFactory = checkNotNull(failedJobCommandFactory, "failedJobCommandFactory is null");
    }
}