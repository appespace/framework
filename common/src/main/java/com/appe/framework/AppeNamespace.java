package com.appe.framework;

/**
 * The NAMESPACE in which each APPE is running, to help isolation & SANDBOX.
 * 
 * @author tobi
 */
public interface AppeNamespace {
	/**
	 * Set new namespace to current context
	 * 
	 * @param namespace
	 */
	public void set(String namespace);
	
	/**
	 * return current NAMESPACE
	 * 
	 * @return
	 */
	public String get();
	
	/**
	 * Clean out any NAMESPACE currently set
	 * 
	 */
	public void clear();
}
