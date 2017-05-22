package com.appe.framework.job.impl;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.appe.framework.job.internal.JobInfo;
import com.appe.framework.job.internal.JobQueue;
import com.appe.framework.util.Objects;
/**
 * 
 * @author ho
 *
 */
public class DelayJobQueue implements JobQueue {
	private final AtomicLong SEQ = new AtomicLong(); 
	private DelayQueue<DelayedJob> delayQueue = new DelayQueue<DelayedJob>();
	/**
	 * 
	 */
	public DelayJobQueue() {
	}
	
	/**
	 * Poll and wait until time out in seconds specified.
	 */
	@Override
	public JobInfo poll(int timeoutSeconds) throws InterruptedException {
		DelayedJob delayed = delayQueue.poll(timeoutSeconds, TimeUnit.SECONDS);
		return (delayed == null? null : delayed.job);
	}
	
	/**
	 * PUT TO THE QUEUE
	 */
	@Override
	public void offer(JobInfo job) {
		int delaySeconds = (job.getRetryCount() > 0? 0 : 0);
		delayQueue.offer(new DelayedJob(job, delaySeconds, SEQ.incrementAndGet()));
	}
	
	/**
	 * DELAYED TASK QUEUE, USING TIME TO EXECUTE & SEQ number to determine order of execution.
	 * @author tobi
	 *
	 * @param <T>
	 */
	static class DelayedJob implements Delayed {
		final JobInfo job;
		final long timer;
		final long seq;
		public DelayedJob(JobInfo job, int delaySeconds, long seq) {
			this.job   = job;
			this.timer = System.currentTimeMillis() + delaySeconds * 1000L;
			this.seq  = seq;
		}
		
		//MAKE SURE COMPARE EXECUTION SEQ
		@Override
		public int compareTo(Delayed delayed) {
			DelayedJob tdelayed = (DelayedJob)delayed;
			int sign = Objects.signum(this.timer - tdelayed.timer);
			if(sign == 0) {
				sign = Objects.signum(this.seq - tdelayed.seq);
			}
			return sign;
		}
		
		//RETURN REMAINING EXECUTION TIME
		@Override
		public long getDelay(TimeUnit unit) {
			return unit.convert(timer - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
		}
	}
}
