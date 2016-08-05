package pl.com.itsense.easyflow.test;

import java.util.Objects;

import au.com.ds.ef.EventType;
import au.com.ds.ef.StateEnum;
import au.com.ds.ef.err.LogicViolationError;

/**
 * 
 * @author P.Pretki
 *
 */
public class TestCheckPoint 
{
	/** */
	private final EventType event; 
	/** */
	private final StateEnum state;
	/** */
	private final LogicViolationError error;
	/** */
	public TestCheckPoint()
	{
		event = null;
		state = null;
		error = null;
	}
	/**
	 * 
	 * @param from
	 * @param to
	 */
	public TestCheckPoint(final EventType event, final StateEnum state)
	{
		this.event = event;
		this.state = state;
		error = null;
	}
	/**
	 * 
	 * @param event
	 */
	public TestCheckPoint(final EventType event)
	{
		this.event = event;
		state = null;
		error = null;
	}
	
	/**
	 * 
	 * @param error
	 */
	public TestCheckPoint(final LogicViolationError error)
	{
		event = null;
		state = null;
		this.error = error;
	}
	
	@Override
	public boolean equals(final Object o) 
	{
        if (o == this) return true;
        if (!(o instanceof TestCheckPoint)) return false;
        final TestCheckPoint checkPoint = (TestCheckPoint) o;
        return event == checkPoint.event && state == checkPoint.state && Objects.equals(error, checkPoint.error) ; 
	}


	@Override
	public String toString() 
	{
		final StringBuilder sb = new StringBuilder("[");
		sb.append(event).append(", ").append(state).append(", ").append(error);		
		sb.append("]");
		return sb.toString();
	}
	
	/**
	 * 
	 * @return
	 */
	public static TestCheckPoint activate(final StateEnum state)
	{
		return new TestCheckPoint(EventType.STATE_ACTIVATE, state);
	}
	/**
	 * 
	 * @return
	 */
	public static TestCheckPoint deactivate(final StateEnum state)
	{
		return new TestCheckPoint(EventType.STATE_DEACTIVATE, state);
	}
	/**
	 * 
	 * @return
	 */
	public static TestCheckPoint activate()
	{
		return new TestCheckPoint(EventType.ANY_STATE_ACTIVATE);
	}
	/**
	 * 
	 * @return
	 */
	public static TestCheckPoint deactivate()
	{
		return new TestCheckPoint(EventType.ANY_STATE_DEACTIVATE);
	}
	/**
	 * 
	 * @return
	 */
	public static TestCheckPoint enter(final StateEnum state)
	{
		return new TestCheckPoint(EventType.STATE_ENTER, state);
	}
	/**
	 * 
	 * @return
	 */
	public static TestCheckPoint leave(final StateEnum state)
	{
		return new TestCheckPoint(EventType.STATE_LEAVE, state);
	}
	
	/**
	 * 
	 * @return
	 */
	public static TestCheckPoint enter()
	{
		return new TestCheckPoint(EventType.ANY_STATE_ENTER);
	}
	
	/**
	 * 
	 * @return
	 */
	public static TestCheckPoint leave()
	{
		return new TestCheckPoint(EventType.ANY_STATE_LEAVE);
	}
}
