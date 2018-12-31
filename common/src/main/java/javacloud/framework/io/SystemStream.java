package javacloud.framework.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javacloud.framework.util.Objects;
/**
 * ThreadLocal to safely redirect IN/OUT/ERR. To avoid leaking, .get()/.remove() should be call to check-in/out.
 * 
 * @author ho
 *
 */
public final class SystemStream {
	//THREAD LOCAL STREAM
	private static class ThreadLocalStream extends PrintStream {
		public ThreadLocalStream(ThreadLocal<OutputStream> tout) {
			super(new OutputStream() {
				@Override
				public void write(int b) throws IOException {
					tout.get().write(b);
				}
				@Override
				public void write(byte[] b, int off, int len) throws IOException {
					tout.get().write(b, off, len);
				}
			}, true);
		}
		@Override
		public void close() {
			super.flush();
		}
	};
	//ORIGIN
	private static final PrintStream OUT = new PrintStream(System.out) {
		@Override
		public void close() {
			super.flush();
		}
	};
	private static final PrintStream ERR = new PrintStream(System.err) {
		@Override
		public void close() {
			super.flush();
		}
	};
	//THREAD SAFE
	private static ThreadLocal<OutputStream> TOUT = new ThreadLocal<OutputStream>() {
		@Override
		protected OutputStream initialValue() {
			return OUT;
		}
	};
	private static ThreadLocal<OutputStream> TERR = new ThreadLocal<OutputStream>() {
		@Override
		protected OutputStream initialValue() {
			return ERR;
		}
	};
	
	//PROTECTED
	private SystemStream() {
	}
	
	/**
	 * 
	 * @return
	 */
	public static PrintStream out() {
		if(System.out instanceof ThreadLocalStream) {
			return System.out;
		}
		return OUT;
	}
	
	/**
	 * 
	 * @return
	 */
	public static PrintStream err() {
		if(System.err instanceof ThreadLocalStream) {
			return System.err;
		}
		return ERR;
	}
	
	/**
	 * ACTIVATE THE THREAD PRINT. SHOULD BE DONE PRIOR TO bind() to be consistent.
	 */
	public static void bind() {
		bind(null, null);
	}
	
	/**
	 * FLUSH SAME AS CLOSE
	 */
	public static void flush() {
		System.out.flush();
		System.err.flush();
	}
	
	/**
	 * Bind the new out/err
	 * 
	 * @param out
	 * @param err
	 */
	public static void bind(OutputStream out, OutputStream err) {
		//OUT
		if(!(System.out instanceof ThreadLocalStream)) {
			System.setOut(new ThreadLocalStream(TOUT));
		}
		if(out != null) {
			TOUT.set(out);
		}
		
		//ERR
		if(!(System.err instanceof ThreadLocalStream)) {
			System.setErr(new ThreadLocalStream(TERR));
		}
		if(err != null) {
			TERR.set(err);
		}
	}
	
	/**
	 * Un-bind the new out/err
	 */
	public static void unbind() {
		try {
			flush();
			Objects.closeQuietly(TOUT.get(), TERR.get());
		} finally {
			TOUT.remove();
			TERR.remove();
		}
	}
}
