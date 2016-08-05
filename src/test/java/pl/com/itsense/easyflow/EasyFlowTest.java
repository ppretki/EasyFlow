package pl.com.itsense.easyflow;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import pl.com.itsense.easyflow.test.TestCheckPoint;
import pl.com.itsense.easyflow.test.TestContext;
import pl.com.itsense.easyflow.test.TestEasyFlow;
import pl.com.itsense.easyflow.test.TestEasyFlowBuilder;
import pl.com.itsense.easyflow.test.TestEasyFlowBuilder.TestEvent;

/**
 * Unit test for simple App.
 */
public class EasyFlowTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public EasyFlowTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( EasyFlowTest.class );
    }
    
    /** */
    public void testSimpleStateTransition()
    {
    	final TestEasyFlow fsm = TestEasyFlowBuilder.buildTestEasyFlow(TestEasyFlowBuilder.STATE_A1);
    	final TestContext expected = new TestContext(true);

    	expected.add(TestCheckPoint.leave(TestEasyFlowBuilder.STATE_A1));
    	expected.add(TestCheckPoint.leave());
    	
    	expected.add(TestCheckPoint.enter(TestEasyFlowBuilder.STATE_B1));
    	expected.add(TestCheckPoint.enter());
    	
    	fsm.getContext().start();
    	fsm.dispatch(TestEvent.EVENT_A1_B1);
    	
    	System.out.println(fsm.getContext());
    	assertEquals(expected, fsm.getContext());
    }
    
    
    /** */
    public void testFullStackStateTransition()
    {
    	final TestEasyFlow fsm = TestEasyFlowBuilder.buildTestEasyFlow(TestEasyFlowBuilder.STATE_A2);
    	final TestContext expected = new TestContext(true);

    	expected.add(TestCheckPoint.leave(TestEasyFlowBuilder.STATE_A2));
    	expected.add(TestCheckPoint.leave());
    	

    	expected.add(TestCheckPoint.deactivate(TestEasyFlowBuilder.STATE_A1));
    	expected.add(TestCheckPoint.deactivate());
    	

    	expected.add(TestCheckPoint.activate(TestEasyFlowBuilder.STATE_B1));
    	expected.add(TestCheckPoint.activate());
    	

    	expected.add(TestCheckPoint.enter(TestEasyFlowBuilder.STATE_B2));
    	expected.add(TestCheckPoint.enter());
    	
    	fsm.getContext().start();
    	fsm.dispatch(TestEvent.EVENT_A2_B2);
    	
    	System.out.println(fsm.getContext());
    	assertEquals(expected, fsm.getContext());
    }

    
}
