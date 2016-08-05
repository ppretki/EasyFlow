package pl.com.itsense.easyflow.test;

import au.com.ds.ef.EasyFlow;
import au.com.ds.ef.EventEnum;
import au.com.ds.ef.err.LogicViolationError;

/**
 * 
 * @author P.Pretki
 *
 */
public class TestEasyFlow 
{
	/** */
	private final EasyFlow<TestContext> fsm;
	/** */
	private final TestContext context;
	/**
	 * 
	 */
	public TestEasyFlow(final EasyFlow<TestContext> fsm, final TestContext context)
	{
		this.fsm = fsm;
		this.context = context;
	}
	/**
	 * 
	 */
	public void dispatch(final EventEnum event)
	{
		try 
		{
			fsm.trigger(event, context);
		} 
		catch (LogicViolationError e) 
		{
			context.add(new TestCheckPoint(e));
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public TestContext getContext() 
	{
		return context;
	}
}
