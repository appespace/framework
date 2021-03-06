package javacloud.framework.flow.internal;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Named;

import com.google.inject.Inject;

import javacloud.framework.cdi.test.ServiceTest;
import javacloud.framework.flow.spec.FlowDefinition;
import javacloud.framework.flow.spec.StateDefinition;
import javacloud.framework.io.Externalizer;
import javacloud.framework.util.ResourceLoader;

import org.junit.Assert;
import org.junit.Test;
/**
 * 
 * @author ho
 *
 */
public class FlowSpecTest extends ServiceTest {
	@Inject @Named(Externalizer.JSON)
	Externalizer externalizer;
	
	@Test
	public void testSpec() throws IOException {
		FlowDefinition flowSpec = null;
		try(InputStream stream = ResourceLoader.getClassLoader().getResourceAsStream("hello-states.json")) {
			flowSpec = externalizer.unmarshal(stream, FlowDefinition.class);
		}
		Assert.assertNotNull(flowSpec);
		Assert.assertTrue(flowSpec.getStates().get("hello") instanceof StateDefinition.Task);
	}
}
