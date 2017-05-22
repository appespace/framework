package com.appe.framework.job;

import java.util.concurrent.TimeUnit;

import com.appe.framework.util.Objects;

/**
 * 
 * @author ho
 *
 */
public class HelloAction implements ExecutionAction {

	@Override
	public ExecutionStatus onExecute(ExecutionContext executionContext) {
		System.out.println("<" + executionContext.getRetryCount() + "> Hello world!");
		executionContext.submitJob("HelloChildAction", null);
		Objects.sleep(2, TimeUnit.SECONDS);
		return ExecutionStatus.WAIT;
	}

	@Override
	public boolean onCompleted(ExecutionContext executionContext) {
		if(executionContext.getRetryCount() < 3) {
			return false;
		}
		System.out.println("Bye!!!");
		return true;
	}
}
