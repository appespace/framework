package com.appe.framework.data.impl;

import javax.inject.Singleton;

import com.appe.framework.data.DataSchema;
import com.appe.framework.data.DataStore;
import com.appe.framework.data.internal.AbstractDataManager;
/**
 * Very simple implementation of data manager in memory, good for automation & testing...
 * 
 * @author tobi
 *
 */
@Singleton
public class InMemoryDataManagerImpl extends AbstractDataManager {
	public InMemoryDataManagerImpl() {
	}
	
	/**
	 * return memory data store.
	 */
	@Override
	protected <T> DataStore<T> createStore(DataSchema<T> schema, int options) {
		return new InMemoryDataStoreImpl<T>(schema);
	}
}