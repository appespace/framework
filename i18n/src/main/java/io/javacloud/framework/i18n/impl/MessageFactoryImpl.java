package io.javacloud.framework.i18n.impl;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.javacloud.framework.i18n.ContextLocale;
import io.javacloud.framework.i18n.MessageFactory;
import io.javacloud.framework.i18n.internal.MessageBundlesControl;
import io.javacloud.framework.util.Objects;
import io.javacloud.framework.util.ResourceLoader;
import io.javacloud.framework.util.UncheckedException;
/**
 * 
 * @author ho
 *
 */
@Singleton
public class MessageFactoryImpl implements MessageFactory {
	private static final Logger logger = Logger.getLogger(MessageFactoryImpl.class.getName());
	
	private final ContextLocale contextLocale;
	private final MessageBundlesControl bundlesControl;
	/**
	 * 
	 * @param contextLocale
	 */
	@Inject
	public MessageFactoryImpl(ContextLocale contextLocale) {
		this.contextLocale = contextLocale;
		this.bundlesControl= new MessageBundlesControl(contextLocale);
		
		discoverBundles();
	}
	
	/**
	 * Re-discover resource bundles
	 */
	protected void discoverBundles() {
		try {
			bundlesControl.discoverBundles(ResourceLoader.getClassLoader());
			logger.fine("Discovered i18n resource bundles: " + bundlesControl.getBundleNames());
		}catch(IOException ex) {
			throw UncheckedException.wrap(ex);
		}
	}
	
	/**
	 * Lookup local string message from all bundles
	 */
	@Override
	public String getString(String key, Object... args) {
		ResourceBundle bundle = ResourceBundle.getBundle("", contextLocale.get(), ResourceLoader.getClassLoader(), bundlesControl);
		String message = bundle.getString(key);
		
		//RE-FORMAT THE MESSAGE
		if(args != null && args.length > 0) {
			message = new MessageFormat(message).format(args);
		}
		return message;
	}
	
	/**
	 * return the type safe bundle
	 */
	@Override
	public <T> T getBundle(Class<T> type) {
		InvocationHandler messageHandler = new MessageInvocationHandlerImpl(contextLocale, bundlesControl);
		return Objects.cast(Proxy.newProxyInstance(ResourceLoader.getClassLoader(), new Class<?>[]{ type }, messageHandler));
	}
}
