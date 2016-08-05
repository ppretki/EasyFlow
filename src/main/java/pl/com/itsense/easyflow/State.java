package pl.com.itsense.easyflow;

import au.com.ds.ef.EventEnum;
import au.com.ds.ef.StateEnum;

/**
 * @author P.Pretki
 */
public class State implements StateEnum
{
    /** */
    private final String name;
    /** */
    private final State parent;
    /** */
    private final EventEnum[] actionEvents;
    /** */
    public State(final String name, final State parent, final EventEnum... actionEvents)
    {
    	this.name = name;
        this.parent = parent;
        this.actionEvents = actionEvents;
    }

    @Override
    public State getParent()
    {
        return parent;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        StateEnum state = this;
        while (state != null)
        {
            sb.append(state.name());
            state = state.getParent();
            if (state != null)
            {
                sb.append("->");
            }
        }
        return sb.toString();
    }

    @Override
    public boolean isAncestor(final StateEnum state)
    {
        boolean isAncestor = false;
        StateEnum s = getParent();
        while (s != null)
        {
            if (s == state)
            {
                isAncestor = true;
                break;
            }
            s = s.getParent();
        }
        return isAncestor;
    }

    @Override
    public EventEnum[] getActionEvents()
    {
        return actionEvents;
    }

	@Override
	public String name() 
	{
		return name;
	}
}
