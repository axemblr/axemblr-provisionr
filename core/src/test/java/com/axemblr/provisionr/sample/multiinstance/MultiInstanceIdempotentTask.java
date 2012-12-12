package com.axemblr.provisionr.sample.multiinstance;

import java.util.concurrent.TimeUnit;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class MultiInstanceIdempotentTask implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {

		final String aDude = String.class.cast(execution
				.getVariable("singlePerson"));

		int activeInstances = Integer.class.cast(execution
				.getVariable("nrOfActiveInstances"));
		int nrOfInstances = Integer.class.cast(execution
				.getVariable("nrOfInstances"));
		int nrOfCompletedInstances = Integer.class.cast(execution
				.getVariable("nrOfCompletedInstances"));

		System.err.printf(
				"Hello dude %s! -- total: %d, completed:%d, active:%d \n",
				aDude, nrOfInstances, nrOfCompletedInstances, activeInstances);
		
		if (aDude.equalsIgnoreCase("andrei")) {
			TimeUnit.MILLISECONDS.sleep(100);
		}
	}

}
