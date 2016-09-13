package com.appe.framework.resource.impl;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appe.framework.AppeException;
import com.appe.framework.AppeLoader;
import com.appe.framework.AppeLocale;
import com.appe.framework.resource.ConfigBundle;
import com.appe.framework.resource.MessageBundle;
import com.appe.framework.resource.ResourceBundleManager;
/**
 * TODO: Need to make the I18n handle to be able to load all combine resources if need.
 * 
 * @author ho
 *
 */
@Singleton
public class ResourceBundleManagerImpl implements ResourceBundleManager {
	private static final Logger logger = LoggerFactory.getLogger(ResourceBundleManagerImpl.class);
	private ConcurrentMap<Class<?>, Object> configCache = new ConcurrentHashMap<Class<?>, Object>();
	private AppeLocale appeLocale;
	
	/**
	 * 
	 * @param appeLocale
	 */
	@Inject
	public ResourceBundleManagerImpl(AppeLocale appeLocale) {
		this.appeLocale = appeLocale;
	}
	
	/**
	 * Always lookup from cache
	 * 
	 */
	@Override
	public <T extends ConfigBundle> T getConfigBundle(Class<T> type) {
		return getResourceBundle(type);
	}
	
	/**
	 * Always lookup from a cache
	 * 
	 */
	@Override
	public <T extends MessageBundle> T getMessageBundle(Class<T> type) {
		return getResourceBundle(type);
	}
	
	/**
	 * It's OK to load multiple of them and throw away. We just to make sure always keep the LAST ONE!
	 */
	@SuppressWarnings("unchecked")
	protected <T> T getResourceBundle(Class<T> type) {
		Object config = configCache.get(type);
		if(config == null) {
			//MAKE SURE LESS CONTENTION LOAD AS POSSIBLE
			synchronized(configCache) {
				if((config = configCache.get(type)) == null) {
					config = loadResourceBundle(type);
					configCache.put(type, config);
				}
			}
		}
		return (T)config;
	}
	
	/**
	 * Always using the annotation to identity the resource to be loaded. In theory we assuming just load them UP ONE.
	 * So we don't even need to cache them at all.
	 * 
	 * @param type
	 * 
	 * @return
	 */
	protected Object loadResourceBundle(Class<?> type) {
		ConfigBundle.Bundle bundle = type.getAnnotation(ConfigBundle.Bundle.class);
		String baseName = (bundle != null? bundle.value(): null);
		
		InvocationHandler configHandler;
		if(baseName == null || baseName.isEmpty()) {
			if(MessageBundle.class.isAssignableFrom(type)) {
				configHandler = createSystemI18nHandler(type);
			} else {
				configHandler = createSystemHandler(type);
			}
		} else {
			if(MessageBundle.class.isAssignableFrom(type)) {
				configHandler = createI18nHandler(baseName, type);
			} else {
				configHandler = createConfigHandler(baseName, type);
			}
		}
		return Proxy.newProxyInstance(AppeLoader.getClassLoader(), new Class<?>[]{ type }, configHandler);
	}
	
	/**
	 * Using the system property as source to resolve value.
	 * 
	 * @param config
	 * @return
	 */
	protected InvocationHandler createSystemHandler(Class<?> config) {
		logger.info("Bind config class: " + config.getName() + " to system properties.");
		return	new ConfigBundleHandlerImpl();
	}
	
	/**
	 * Always append the .properties to load the configuration.
	 * 
	 * @param baseName
	 * @param config
	 * @return
	 */
	protected InvocationHandler createConfigHandler(String baseName, Class<?> type) {
		//ALWAYS APPEND .properties to load the resource
		String resource = baseName + ".properties";
		logger.info("Bind the config class: " + type.getName() + " to resource bundle: " + resource);
		try {
			Properties properties = AppeLoader.loadProperties(resource);
			if(properties == null || properties.isEmpty()) {
				return new ConfigBundleHandler();
			}
			
			//USING HANDLER AND ALWAYS FALL BACK TO SYSTEM?
			return	new ConfigBundleHandlerImpl(properties);
		}catch(IOException ex) {
			throw AppeException.wrap(ex);
		}
	}
	
	/**
	 * TODO: create system universal I18n handler
	 * 
	 * @param type
	 * @return
	 */
	protected InvocationHandler createSystemI18nHandler(Class<?> type) {
		return new MessageBundleHandler(appeLocale) {
			@Override
			protected ResourceBundle resolveBundle() throws MissingResourceException {
				throw new MissingResourceException("Not implemented yet", MessageBundleHandler.class.getName(), "");
			}
		};
	}
	
	/**
	 * Since JDK already cache the bundle so we just pass on and lookup as NEED. We need a better way to be able to using
	 * bundle local at RUNTIME UI? NEED LOCAL THREAD TO PASSING DOWN..SINCE Locale.getDefault() is SYSTEM LEVEL.
	 * 
	 * @param baseName
	 * @param type
	 * @return
	 */
	protected InvocationHandler createI18nHandler(final String baseName, Class<?> type) {
		logger.info("Bind I18n config class: " + type.getName() + " to resource bundle: " + baseName);
		
		//TO BE CONSISTENT, FIRST CALLER WIN!!!
		final ClassLoader callerLoader = AppeLoader.getClassLoader();
		return	new MessageBundleHandler(appeLocale) {
					@Override
					protected ResourceBundle resolveBundle() throws MissingResourceException {
						return	ResourceBundle.getBundle(baseName, appeLocale.get(), callerLoader);
					}
				};
	}
}
