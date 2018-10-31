package io.javacloud.framework.flow;

/**
 * 
 * @author ho
 *
 */
public interface StateFunction extends StateHandler<Object>, StateHandler.InputHandler<Object>, StateHandler.OutputHandler,
	StateHandler.FailureHandler, StateHandler.RetryHandler {
	/**
	 * Generic InputHandler need type for conversion
	 * @return
	 */
	public Class<?> getParametersType();
	
	/**
	 * Default function timeout
	 * @return
	 */
	default public int getTimeoutSeconds() {
		return 120;
	}
	
	/**
	 * Default heartbeat to keep alive
	 * @return
	 */
	default public int getHeartbeatSeconds() {
		return 10;
	}
}
