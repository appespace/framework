package javacloud.framework.cdi.impl;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.Scopes;

import javacloud.framework.cdi.LazySingleton;
import javacloud.framework.cdi.internal.GuiceModule;
/**
 * Special use to always lazy singleton.
 * 
 * @author ho
 *
 */
final class GuiceModuleImpl extends GuiceModule {
	private static final Scope singletonScope = new Scope() {
		@Override
		public <T> Provider<T> scope(Key<T> key, Provider<T> unscoped) {
			return Scopes.SINGLETON.scope(key, unscoped);
		}
	};
	
	@Override
	protected void configure() {
		bindScope(LazySingleton.class, singletonScope);
	}
}
