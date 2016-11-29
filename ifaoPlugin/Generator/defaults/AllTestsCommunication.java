package net.ifao.arctic.agents._PACKAGE_.framework.communication;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests_Provider_Communication 
{

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for net.ifao.arctic.agents._PACKAGE_.framework.communication");
		//$JUnit-BEGIN$
		suite.addTestSuite(GdsRulesController_Provider_Test.class);
		//$JUnit-END$
		return suite;
	}

}
