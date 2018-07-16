package net.ifao.arctic.agents._PACKAGEROOT_;

import net.ifao.arctic.agents._PACKAGE_.framework.communication.AllTests_Provider_Communication;
import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests_Provider_
{

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for net.ifao.arctic.agents._PACKAGEROOT_");
		//$JUnit-BEGIN$
		suite.addTest(AllTests_Provider_Communication.suite());
		//$JUnit-END$
		return suite;
	}

}
