package javacloud.framework.util;

import org.junit.Test;

import javacloud.framework.util.Condition;

import org.junit.Assert;
import junit.framework.TestCase;
/**
 * 
 * AND <Variable, Condition>  <Variable, Condition>
 * 
 * @author ho
 *
 */
public class OpConditionTest extends TestCase {
	@Test
	public void testEQ() {
		Condition<String> eq = Condition.EQ("124");
		Assert.assertTrue(eq.test("124"));
	}
}
