package io.javacloud.framework.flow.spi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.javacloud.framework.flow.StateTransition;
import io.javacloud.framework.util.Objects;

/**
 * 
 * @author ho
 *
 */
public class StateSpec {
	public static enum Type {
		Task,
		Pass,
		Wait,
		Succeed,
		Fail,
		Choice,
		Parallel
	}
	
	public static class Retrier implements StateTransition.Retry {
		@JsonProperty("ErrorEquals")
		private List<String> errorEquals;
		
		@JsonProperty("TimeoutSeconds")
		private int timeoutSeconds	= -1;
		
		@JsonProperty("MaxAttempts")
		private int maxAttempts		= 0;
		
		@JsonProperty("IntervalSeconds")
		private int intervalSeconds	= 5;
		
		@JsonProperty("BackoffRate")
		private double backoffRate 	= 1.0;
		
		public Retrier() {
		}
		
		@Override
		public boolean isEnd() {
			return false;
		}
		@Override
		public int getMaxAttempts() {
			return maxAttempts;
		}
		public Retrier withMaxAttempts(int maxAttempts) {
			this.maxAttempts = maxAttempts;
			return this;
		}
		
		@Override
		public int getIntervalSeconds() {
			return intervalSeconds;
		}
		public Retrier withIntervalSeconds(int intervalSeconds) {
			this.intervalSeconds = intervalSeconds;
			return this;
		}
		
		@Override
		public double getBackoffRate() {
			return backoffRate;
		}
		public Retrier withBackoffRate(int backoffRate) {
			this.backoffRate = backoffRate;
			return this;
		}
		
		@Override
		public int getTimeoutSeconds() {
			return timeoutSeconds;
		}
		public Retrier withTimeoutSeconds(int timeoutSeconds) {
			this.timeoutSeconds = timeoutSeconds;
			return this;
		}

		public List<String> getErrorEquals() {
			return errorEquals;
		}
		public Retrier withErrorEquals(List<String> errorEquals) {
			this.errorEquals = errorEquals;
			return this;
		}
		public Retrier withErrorEquals(String... errorEquals) {
			this.errorEquals = Objects.asList(errorEquals);
			return this;
		}
	}
	
	public static class Catcher implements StateTransition.Success {
		@JsonProperty("ErrorEquals")
		private List<String> errorEquals;
		
		@JsonProperty("Result")
		private Object result;
		
		@JsonProperty("Output")
		private Object output;
		
		@JsonProperty("Next")
		private String next;
		public Catcher() {
		}
		
		@Override
		public boolean isEnd() {
			return false;
		}
		@Override
		public String getNext() {
			return next;
		}
		public Catcher withNext(String next) {
			this.next = next;
			return this;
		}
		
		public Object getResult() {
			return result;
		}
		public Catcher withResult(Object result) {
			this.result = result;
			return this;
		}
		
		public Object getOutput() {
			return output;
		}
		public Catcher withOutput(Object output) {
			this.output = output;
			return this;
		}
		
		public List<String> getErrorEquals() {
			return errorEquals;
		}
		public Catcher withErrorEquals(List<String> errorEquals) {
			this.errorEquals = errorEquals;
			return this;
		}
		public Catcher withErrorEquals(String... errorEquals) {
			this.errorEquals = Objects.asList(errorEquals);
			return this;
		}
	}
	
	@JsonProperty("Type")
	private Type 	type;
	
	@JsonProperty("Resource")
	private String	resource;
	
	@JsonProperty("Input")
	private Object	input;
	
	@JsonProperty("Result")
	private Object	result;
	
	@JsonProperty("Output")
	private Object	output;
	
	@JsonProperty("Next")
	private String next;
	
	@JsonProperty("Retry")
	private List<Retrier> retriers;
	
	@JsonProperty("Catch")
	private List<Catcher> catchers;
	
	@JsonProperty("Comment")
	private String comment;
	public StateSpec() {
	}
	
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	
	public String getResource() {
		return resource;
	}
	public void setResource(String resource) {
		this.resource = resource;
	}
	
	public Object getInput() {
		return input;
	}
	public void setInput(Object input) {
		this.input = input;
	}
	
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
	
	public Object getOutput() {
		return output;
	}
	public void setOutput(Object output) {
		this.output = output;
	}
	
	public String getNext() {
		return next;
	}
	public void setNext(String next) {
		this.next = next;
	}
	
	public List<Retrier> getRetriers() {
		return retriers;
	}
	public void setRetriers(List<Retrier> retriers) {
		this.retriers = retriers;
	}
	
	public List<Catcher> getCatchers() {
		return catchers;
	}
	public void setCatchers(List<Catcher> catchers) {
		this.catchers = catchers;
	}

	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
}