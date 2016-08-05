package pl.com.itsense.easyflow.test;

import java.util.ArrayList;
import java.util.Arrays;

import au.com.ds.ef.EasyFlow;
import au.com.ds.ef.EventEnum;
import au.com.ds.ef.FlowBuilder;
import au.com.ds.ef.StateEnum;
import au.com.ds.ef.SyncExecutor;
import au.com.ds.ef.Transition;
import au.com.ds.ef.call.ContextHandler;
import au.com.ds.ef.call.EventHandler;
import au.com.ds.ef.call.StateHandler;
import pl.com.itsense.easyflow.State;

/**
 * 
 * @author P.Pretki
 *
 */
public class TestEasyFlowBuilder 
{
	/** */
	public static enum TestEvent implements EventEnum
	{
	    /** */
	    EVENT_A1_B1,
	    /** */
	    EVENT_A1_B2,
	    /** */
	    EVENT_A1_B3,
	    /** */
	    EVENT_A2_B1,
	    /** */
	    EVENT_A2_B2,
	    /** */
	    EVENT_A2_B3,
	    /** */
	    EVENT_A3_B1,
	    /** */
	    EVENT_A3_B2,
	    /** */
	    EVENT_A3_B3,
	    /** */
	    EVENT_B1_A1,
	    /** */
	    EVENT_B1_A2,
	    /** */
	    EVENT_B1_A3,
	    /** */
	    EVENT_B2_A1,
	    /** */
	    EVENT_B2_A2,
	    /** */
	    EVENT_B2_A3,
	    /** */
	    EVENT_B3_A1,
	    /** */
	    EVENT_B3_A2,
	    /** */
	    EVENT_B3_A3,
	}
	/** */
	private static final ArrayList<State> STATES = new ArrayList<State>();
	/** */
	public static State define(final State state)
	{
		STATES.add(state);
		return state;
	}
	/** */
	public static final State STATE_A1 = define(new State("A[1]", null));
	/** */
	public static final State STATE_A2 = define(new State("A[2]", STATE_A1));
	/** */
	public static final State STATE_A3 = define(new State("A[3]", STATE_A2));
	/** */
	public static final State STATE_B1 = define(new State("B[1]", null));
	/** */
	public static final State STATE_B2 = define(new State("B[2]", STATE_B1));
	/** */
	public static final State STATE_B3 = define(new State("B[3]", STATE_B2));
	
	/** */
	public static final Transition[] COMPLETE_TRANSITIONS = new Transition[]
	{
		new Transition(TestEvent.EVENT_A1_B1, STATE_A1, STATE_B1),
		new Transition(TestEvent.EVENT_A1_B2, STATE_A1, STATE_B2),
		new Transition(TestEvent.EVENT_A1_B3, STATE_A1, STATE_B3),

		new Transition(TestEvent.EVENT_A2_B1, STATE_A2, STATE_B1),
        new Transition(TestEvent.EVENT_A2_B2, STATE_A2, STATE_B2),
        new Transition(TestEvent.EVENT_A2_B3, STATE_A2, STATE_B3),
        
        new Transition(TestEvent.EVENT_A3_B1, STATE_A3, STATE_B1),
		new Transition(TestEvent.EVENT_A3_B2, STATE_A3, STATE_B2),
        new Transition(TestEvent.EVENT_A3_B3, STATE_A3, STATE_B3),
	    
        new Transition(TestEvent.EVENT_B1_A1, STATE_B1, STATE_A1),
        new Transition(TestEvent.EVENT_B1_A2, STATE_B1, STATE_A2),
        new Transition(TestEvent.EVENT_B1_A3, STATE_B1, STATE_A3),

        new Transition(TestEvent.EVENT_B2_A1, STATE_B2, STATE_A1),
        new Transition(TestEvent.EVENT_B2_A2, STATE_B2, STATE_A2),
        new Transition(TestEvent.EVENT_B2_A3, STATE_B2, STATE_A3),

        new Transition(TestEvent.EVENT_B3_A1, STATE_B3, STATE_A1),
        new Transition(TestEvent.EVENT_B3_A2, STATE_B3, STATE_A2),
        new Transition(TestEvent.EVENT_B3_A3, STATE_B3, STATE_A3),
    };
	/** */
	private static final EventHandler<TestContext> ANY_STATE_ACTIVATE_HANDLER = new EventHandler<TestContext>() 
	{
		@Override
		public void call(final EventEnum event, final StateEnum from, final StateEnum to, final TestContext context) throws Exception 
		{
			context.add(TestCheckPoint.activate());
		}
	};
	/** */
	private static final EventHandler<TestContext> ANY_STATE_DEACTIVATE_HANDLER = new EventHandler<TestContext>() 
	{
		@Override
		public void call(final EventEnum event, final StateEnum from, final StateEnum to, final TestContext context) throws Exception 
		{
			context.add(TestCheckPoint.deactivate());
		}
	};
	/** */
	private static final ContextHandler<TestContext> STATE_ACTIVATE_CONTEXT_HANDLER = new ContextHandler<TestContext>() 
	{
		@Override
		public void call(TestContext context) throws Exception 
		{
			context.add(TestCheckPoint.activate(context.getState()));
		}
	};
	
	/** */
	private static final ContextHandler<TestContext> STATE_DEACTIVATE_CONTEXT_HANDLER = new ContextHandler<TestContext>() 
	{
		@Override
		public void call(TestContext context) throws Exception 
		{
			context.add(TestCheckPoint.deactivate(context.getState()));
		}
	};
	/** */
	private static final StateHandler<TestContext> ANY_STATE_ENTER_HANDLER = new StateHandler<TestContext>() 
	{
		@Override
		public void call(final StateEnum state, final TestContext context) throws Exception 
		{
			context.add(TestCheckPoint.enter());
		}
	};

	/** */
	private static final StateHandler<TestContext> ANY_STATE_LEAVE_HANDLER = new StateHandler<TestContext>() 
	{
		@Override
		public void call(final StateEnum state, final TestContext context) throws Exception 
		{
			context.add(TestCheckPoint.leave());
		}
	};
	/** */
	private static final ContextHandler<TestContext> STATE_ENTER_HANDLER = new ContextHandler<TestContext>() 
	{
		@Override
		public void call(final TestContext context) throws Exception 
		{
			context.add(TestCheckPoint.enter(context.getState()));
		}
	};

	/** */
	private static final ContextHandler<TestContext> STATE_LEAVE_HANDLER = new ContextHandler<TestContext>() 
	{
		@Override
		public void call(final TestContext context) throws Exception 
		{
			context.add(TestCheckPoint.leave(context.getState()));
		}
	};
	
	/**
	 * @return
	 */
	public static TestEasyFlow buildTestEasyFlow(final StateEnum initialState)
	{
		final TestContext context = new TestContext();
		final EasyFlow<TestContext> fsm = FlowBuilder.fromTransitions(initialState, Arrays.asList(COMPLETE_TRANSITIONS), false);
		fsm.whenActivate(ANY_STATE_ACTIVATE_HANDLER);
		fsm.whenDeactivate(ANY_STATE_DEACTIVATE_HANDLER);
		fsm.whenEnter(ANY_STATE_ENTER_HANDLER);
		fsm.whenLeave(ANY_STATE_LEAVE_HANDLER);
		for (final State state : STATES)
		{
			fsm.whenActivate(state, STATE_ACTIVATE_CONTEXT_HANDLER);
			fsm.whenDeactivate(state, STATE_DEACTIVATE_CONTEXT_HANDLER);
			fsm.whenEnter(state, STATE_ENTER_HANDLER);
			fsm.whenLeave(state, STATE_LEAVE_HANDLER);
		}
        fsm.executor(new SyncExecutor());
        fsm.start(false, context);
		return new TestEasyFlow(fsm, context);
	}
}
