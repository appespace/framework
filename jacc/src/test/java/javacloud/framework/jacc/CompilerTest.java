package javacloud.framework.jacc;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.junit.Test;

import javacloud.framework.cdi.ServiceRunlist;
import javacloud.framework.cdi.test.ServiceTest;
import javacloud.framework.jacc.JavaCompiler;
import javacloud.framework.jacc.JavaSource;
import javacloud.framework.jacc.internal.InMemoryClassCollector;
import javacloud.framework.jacc.internal.InMemoryClassLoader;
import javacloud.framework.jacc.internal.JavaSourceFile;

/**
 * 
 * @author ho
 *
 */
public class CompilerTest extends ServiceTest {
	static final String CODE = "package io.test; \npublic class Hello {\n public static void sayHello() {\n\n} \n}"; 
	@Inject
	private JavaCompiler javaCompiler;
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCompile() throws Exception {
		long starts = System.currentTimeMillis();
		try {
			List<JavaSource> sources = new ArrayList<JavaSource>();
			sources.add(new JavaSourceFile("io.test.Hello", CODE));
			
			InMemoryClassCollector collector = new InMemoryClassCollector();
			boolean success = javaCompiler.compile(sources, collector);
			if(!success) {
				System.out.println("COMPILATION ERROR:");
				for(URI file: collector.getFailures()) {
					for (InMemoryClassCollector.Metric metric: collector.getMetrics(file)) {
						System.out.println(file + ":" + metric);
					}
				}
			} else {
				System.out.println("COMPILATION SUCCESSFUL.");
				InMemoryClassLoader classLoader = new InMemoryClassLoader(collector, CompilerTest.class.getClassLoader());
				Class<?> helloClass = classLoader.loadClass("io.test.Hello");
				ServiceRunlist.get().runMethod(helloClass, "sayHello");
			}
		} finally {
			System.out.println("\nESLAPED: " + (System.currentTimeMillis() - starts));
		}
	}

}