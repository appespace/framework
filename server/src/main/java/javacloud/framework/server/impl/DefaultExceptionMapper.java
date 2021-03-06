package javacloud.framework.server.impl;

import javacloud.framework.i18n.MessageManager;
import javacloud.framework.server.GenericExceptionMapper;

import javax.inject.Inject;

/**
 * The least caught exception handler, in case of the application doesn't have any better way to handler them.
 * 
 * @author ho
 *
 */
public final class DefaultExceptionMapper extends GenericExceptionMapper<Throwable> {
	private final MessageManager messageFactory;
	/**
	 * 
	 * @param messageFactory
	 */
	@Inject
	public DefaultExceptionMapper(MessageManager messageFactory) {
		this.messageFactory = messageFactory;
	}
	
	/**
	 * Using default message bundle to translate message
	 * 
	 */
	@Override
	protected String toLocalizedMessage(String message) {
		return messageFactory.getString(message);
	}
}
