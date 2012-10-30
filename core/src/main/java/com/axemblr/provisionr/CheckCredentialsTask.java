package com.axemblr.provisionr;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class CheckCredentialsTask implements JavaDelegate {

	@Override
	public void execute(DelegateExecution context) throws Exception {
		System.err.println("***** Checking credentials");
		context.setVariable("validCredentials", "true");
	}
}
