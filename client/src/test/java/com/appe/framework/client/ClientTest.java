package com.appe.framework.client;

import javax.inject.Inject;
import javax.ws.rs.client.Client;

import org.junit.Test;

import com.appe.framework.internal.RegistryTest;
/**
 * 
 * @author ho
 *
 */
public class ClientTest extends RegistryTest {
	@Inject
	private Client client;
	
	@Test
	public void testClient() {
		client.target("https://www.google.com").request().get();
	}
}
