package javacloud.framework.flow.builder;

import java.util.HashMap;
import java.util.Map;

import javacloud.framework.flow.StateAction;
import javacloud.framework.flow.StateMachine;
import javacloud.framework.flow.StateFunction;

/**
 * 
 * @author hvho
 *
 */
public class FlowBuilder {
	private String startAt;
	private Map<String, StateAction> states;
	public FlowBuilder() {
	}
	
	/**
	 * 
	 * @param startAt
	 * @return
	 */
	public FlowBuilder withStartAt(String startAt) {
		this.startAt = startAt;
		return this;
	}
	
	/**
	 * 
	 * @param name
	 * @param handler
	 * @param next
	 * @return
	 */
	public FlowBuilder withState(String name, StateFunction<?, ?> handler, String next) {
		return withState(name, new ActionBuilder()
								.withStateFunction(handler)
								.withOutputHandler(TransitionBuilder.success(next))
								.build());
	}
	/**
	 * 
	 * @param name
	 * @param handler
	 * @param repeatHandler
	 * @param next
	 * @return
	 */
	public FlowBuilder withState(String name, StateFunction<?, ?> handler, StateFunction.RetryHandler retryHandler, String next) {
		return withState(name, new ActionBuilder()
								.withStateFunction(handler)
								.withRetryHandler(retryHandler)
								.withOutputHandler(TransitionBuilder.success(next))
								.build());
	}
	/**
	 * 
	 * @param name
	 * @param action
	 * @return
	 */
	public FlowBuilder withState(String name, StateAction action) {
		if(states == null) {
			states = new HashMap<>();
		}
		states.put(name, action);
		return this;
	}
	
	/**
	 * 
	 * @return
	 */
	public StateMachine build() {
		return new StateMachine() {
			@Override
			public String getStartAt() {
				return startAt;
			}
			
			@Override
			public StateAction getState(String stepName) {
				return (states == null? null : states.get(stepName));
			}
		};
	}
}
