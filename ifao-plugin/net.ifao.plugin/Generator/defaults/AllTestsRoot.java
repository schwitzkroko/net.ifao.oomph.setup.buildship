import junit.framework.Test;
import junit.framework.TestSuite;
import net.ifao.arctic.agents._PACKAGEROOT_.AllTests_Provider_;

public class AllTests
{

	public static void main(String[] pArgs) {
		junit.textui.TestRunner.run(AllTests.suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for default package");
		//$JUnit-BEGIN$
		suite.addTest(AllTests_Provider_.suite());
		//$JUnit-END$
		return suite;
	}

}
