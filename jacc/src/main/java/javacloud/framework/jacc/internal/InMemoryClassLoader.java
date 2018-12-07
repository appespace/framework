package javacloud.framework.jacc.internal;

import java.nio.ByteBuffer;

/**
 * Find the class from compile collector
 * @author ho
 *
 */
public class InMemoryClassLoader extends ClassLoader {
	private final InMemoryClassCollector collector;
	public InMemoryClassLoader(InMemoryClassCollector collector, ClassLoader parent) {
		super(parent);
		this.collector = collector;
	}
	
	/**
	 * loadClass() always delegate to find class from PARENT first, if no such class found it will invoke this class.
	 */
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		ByteBuffer buf = collector.asBytes(name);
		if(buf == null) {
			throw new ClassNotFoundException(name);
		}
		return defineClass(name, buf, null);
	}
}
