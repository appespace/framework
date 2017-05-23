package com.appe.framework.job.internal;

import java.util.Map;

import com.appe.framework.job.ExecutionAction;
import com.appe.framework.job.ExecutionStatus;
import com.appe.framework.job.ext.JobContext;
import com.appe.framework.job.ext.JobInfo;
import com.appe.framework.job.ext.JobManager;
import com.appe.framework.job.ext.JobPoller;
import com.appe.framework.job.ext.JobState;
import com.appe.framework.job.ext.JobWorker;

/**
 * Integrate with JobManager to safely set the correct JOB STATE
 * 
 * @author ho
 *
 */
public abstract class JobExecutor extends JobWorker {
	protected JobManager jobManager;
	public JobExecutor(JobManager jobManager, JobPoller jobPoller) {
		super(jobPoller);
		this.jobManager = jobManager;
	}
	
	/**
	 * Make sure to set JOB STATE to FAIL with a correct stack trace...Job already take of the QUEUE and
	 * will no longer submit back cause DONT know what todo.
	 */
	@Override
	protected void onUncaughtException(JobInfo job, Throwable ex) {
		super.onUncaughtException(job, ex);
		
		//TRACK CORRECT STATE
		job.setState(JobState.BLOCKED);
		job.setStatus(ExecutionStatus.FAIL);
		
		//TODO: collect stack trace
		jobManager.syncJob(job);
	}
	
	/**
	 * Make sure to re-submit JOB if it's
	 * 
	 * @param jobAction
	 * @param jobContext
	 */
	protected void notifyCompletion(ExecutionAction jobAction, JobContext jobContext) {
		JobInfo job = jobContext.getJob();
		
		//IF JOB IS COMPLETED => NOTHING ELSE NEED TO BE DONE
		if(jobAction.onCompletion(jobContext)) {
			job.setState(JobState.TERMINATED);
			jobManager.syncJob(job);
		} else {
			job.setState(JobState.RETRYING);
			job.setStatus(ExecutionStatus.RETRY);
			job.setRetryCount(job.getRetryCount() + 1);
			
			//PUSH JOB BACK TO JOB QUEUE
			jobManager.submitJob(job);
		}
	}
	
	/**
	 * return the completion status based on the combination of all child job statuses:
	 * 
	 * - If there is a CHILD that is not finished executing, => NOT COMPLETION
	 * - If there is any WARNING/FAIL => WARNING/FAIL
	 * - Otherwise => SUCCESS
	 * 
	 * @param childJobs
	 * @return
	 */
	public static final ExecutionStatus resolveStatus(Map<String, ExecutionStatus> childJobs) {
		ExecutionStatus finalStatus = ExecutionStatus.SUCCESS;
		for(Map.Entry<String, ExecutionStatus> entry: childJobs.entrySet()) {
			ExecutionStatus status = entry.getValue();
			
			//STILL IN WAITING STATE => PUSH BACK TO QUEUE
			if(!ExecutionStatus.isCompleted(status)) {
				finalStatus = status;
				break;
			}
			
			//COMPLETED STATUS
			if(status == ExecutionStatus.FAIL) {
				finalStatus = ExecutionStatus.FAIL;
			} else if(status == ExecutionStatus.WARNING && finalStatus != ExecutionStatus.FAIL) {
				finalStatus = ExecutionStatus.WARNING;
			}
		}
		return finalStatus;
	}
}
