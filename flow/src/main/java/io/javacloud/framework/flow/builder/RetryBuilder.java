package io.javacloud.framework.flow.builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.javacloud.framework.flow.StateContext;
import io.javacloud.framework.flow.StateHandler;
import io.javacloud.framework.flow.StateTransition;
import io.javacloud.framework.flow.spi.StateSpec;
import io.javacloud.framework.util.Objects;

/**
 * 
 * * {
 * 		"Retry":[
 * 			{ "ErrorEquals": ["a", "b"],
 * 			  "MaxAttempts": 10
 * 			}
 * 		]
 * }
 * 
 * @author ho
 *
 */
public class RetryBuilder {
	private Map<String, StateSpec.Retrier> retriers;
	public RetryBuilder() {
	}
	
	/**
	 * 
	 * @param retrier
	 * @param errors
	 * @return
	 */
	public RetryBuilder withRetrier(StateSpec.Retrier retrier) {
		if(retriers == null) {
			retriers = new HashMap<>();
		}
		
		//ADD EQUAL ERRORS
		List<String> errors = retrier.getErrorEquals();
		if(Objects.isEmpty(errors)) {
			retriers.put(StateHandler.ERROR_ALL, retrier);
		} else {
			for(String error: errors) {
				retriers.put(error, retrier);
			}
		}
		return this;
	}
	
	/**
	 * 
	 * @param retriers
	 * @return
	 */
	public RetryBuilder withRetriers(List<StateSpec.Retrier> retriers) {
		if(!Objects.isEmpty(retriers)) {
			for(StateSpec.Retrier retrier: retriers) {
				withRetrier(retrier);
			}
		}
		return this;
	}
	
	/**
	 * return a repeat builder with re-trier definition
	 * @return
	 */
	public StateHandler.RepeatHandler build() {
		return new StateHandler.RepeatHandler() {
			@Override
			public StateTransition onResume(StateContext context) {
				String error = context.getAttribute(StateContext.ATTRIBUTE_ERROR);
				StateSpec.Retrier retrier;
				if(error != null) {
					retrier = (retriers == null? null : retriers.get(error));
				} else {
					retrier = null;
				}
				//USING DEFAULT
				if(retrier == null && retriers != null) {
					retrier = retriers.get(StateHandler.ERROR_ALL);
				}
				
				//CAN'T RE-TRY
				if(retrier == null) {
					return	TransitionBuilder.failure();
				}
				
				//CALCULATE FINAL DELAYS
				int delaySeconds = (int)(retrier.getIntervalSeconds() * Math.pow(retrier.getBackoffRate(), context.getRunCount()));
				return TransitionBuilder.repeat(delaySeconds);
			}
		};
	}
}
