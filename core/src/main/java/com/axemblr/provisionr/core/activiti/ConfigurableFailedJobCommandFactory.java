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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.UUID;
import org.activiti.engine.impl.calendar.BusinessCalendar;
import org.activiti.engine.impl.calendar.CycleBusinessCalendar;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.jobexecutor.FailedJobCommandFactory;
import org.activiti.engine.impl.persistence.entity.JobEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * We need a custom @{link FailedJobCommandFactory} implementation that allows
 * us to customize the global number of retries per job
 */
public class ConfigurableFailedJobCommandFactory implements FailedJobCommandFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurableFailedJobCommandFactory.class);

    /**
     * This class is a bit of a hack because there is no easy way to
     * change @{link JobEntity.DEFAULT_RETRIES}
     * <p/>
     * We get the desired behaviour by incrementing the number of retries until we
     * exceed maxNumberOfRetries + DEFAULT_RETRIES (considered to be 0)
     * <p/>
     * If maxNumberOfRetries is -1 (infinity) the job will be always retried.
     */
    public static class IncrementJobRetriesCmd implements Command<Object> {

        private final String LOCK_OWNER = "job-retries-" + UUID.randomUUID().toString();

        private final String jobId;
        private final Throwable exception;

        private final int maxNumberOfRetries;
        private final int waitBetweenRetriesInSeconds;

        public IncrementJobRetriesCmd(String jobId, Throwable exception, int maxNumberOfRetries,
                                      int waitBetweenRetriesInSeconds) {
            this.jobId = checkNotNull(jobId, "jobId is null");
            this.exception = exception;

            this.maxNumberOfRetries = maxNumberOfRetries;
            this.waitBetweenRetriesInSeconds = waitBetweenRetriesInSeconds;
        }

        @Override
        public Object execute(CommandContext commandContext) {
            JobEntity job = Context.getCommandContext()
                .getJobManager()
                .findJobById(jobId);

            updateNumberOfRetries(job);

            if (exception != null) {
                job.setExceptionMessage(exception.getMessage());
                job.setExceptionStacktrace(getExceptionStacktrace());
            }

            return null;
        }

        private void updateNumberOfRetries(JobEntity job) {
            if (maxNumberOfRetries == -1) {
                job.setLockOwner(LOCK_OWNER);
                job.setLockExpirationTime(calculateDueDate());
                return; /* always retry without counting */
            }

            int realUpperBound = maxNumberOfRetries + JobEntity.DEFAULT_RETRIES;
            if (job.getRetries() >= realUpperBound) {
                LOG.warn("Job {} from process {} has no more retries left. The process will block and may " +
                    "require human intervention.", job.getId(), job.getProcessInstanceId());

                job.setRetries(0);  /* stop retrying this job */
                job.setLockOwner(null);

            } else {
                final Date newDate = calculateDueDate();
                LOG.info("Scheduling job {} from process {} to be retried at {}. Try {}/{}",
                    new Object[]{job.getId(), job.getProcessInstanceId(), newDate,
                        job.getRetries() - JobEntity.DEFAULT_RETRIES, maxNumberOfRetries});

                /* I've tried to use job.setDuedate() but it doesn't work as expected */

                job.setLockOwner(LOCK_OWNER);
                job.setLockExpirationTime(newDate);

                job.setRetries(job.getRetries() + 1);
            }
        }

        /**
         * Based on code from @{link TimerEntity.calculateDueDate}
         */
        private Date calculateDueDate() {
            BusinessCalendar businessCalendar = Context
                .getProcessEngineConfiguration()
                .getBusinessCalendarManager()
                .getBusinessCalendar(CycleBusinessCalendar.NAME);

            return businessCalendar.resolveDuedate(
                String.format("R/PT%dS", waitBetweenRetriesInSeconds));
        }

        private String getExceptionStacktrace() {
            StringWriter stringWriter = new StringWriter();
            exception.printStackTrace(new PrintWriter(stringWriter));
            return stringWriter.toString();
        }
    }

    private final int maxNumberOfRetries;
    private final int waitBetweenRetriesInSeconds;

    public ConfigurableFailedJobCommandFactory(int maxNumberOfRetries, int waitBetweenRetriesInSeconds) {
        checkArgument(maxNumberOfRetries > 0 || maxNumberOfRetries == -1,
            "Max number of retries should be a positive number or -1 (infinite)");
        checkArgument(waitBetweenRetriesInSeconds > 0, "waitBetweenRetriesInSeconds should be positive");

        this.maxNumberOfRetries = maxNumberOfRetries;
        this.waitBetweenRetriesInSeconds = waitBetweenRetriesInSeconds;
    }

    @Override
    public Command<Object> getCommand(String jobId, Throwable exception) {
        return new IncrementJobRetriesCmd(jobId, exception, maxNumberOfRetries, waitBetweenRetriesInSeconds);
    }
}
