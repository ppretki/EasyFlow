package au.com.ds.ef;

import java.util.HashMap;
import java.util.Map;

import au.com.ds.ef.call.ContextHandler;
import au.com.ds.ef.call.EventHandler;
import au.com.ds.ef.call.ExecutionErrorHandler;
import au.com.ds.ef.call.Handler;
import au.com.ds.ef.call.StateHandler;
import au.com.ds.ef.err.ExecutionError;

/**
 * User: andrey
 * Date: 6/12/2013
 * Time: 11:03 AM
 */
public class HandlerCollection
{
    private static final class HandlerType
    {
    	/** */
        EventType eventType;
    	/** */
        EventEnum event;
    	/** */
        StateEnum state;
        /**
         * 
         * @param eventType
         * @param event
         * @param state
         */
        private HandlerType(EventType eventType, EventEnum event, StateEnum state)
        {
            this.eventType = eventType;
            this.event = event;
            this.state = state;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            HandlerType that = (HandlerType) o;

            if (event != null ? !event.equals(that.event) : that.event != null)
                return false;
            if (eventType != that.eventType)
                return false;
            if (state != null ? !state.equals(that.state) : that.state != null)
                return false;

            return true;
        }

        @Override
        public int hashCode()
        {
            int result = eventType.hashCode();
            result = 31 * result + (event != null ? event.hashCode() : 0);
            result = 31 * result + (state != null ? state.hashCode() : 0);
            return result;
        }
    }

    private Map<HandlerType, Handler> handlers = new HashMap<HandlerType, Handler>();

    public void setHandler(EventType eventType, StateEnum state, EventEnum event, Handler handler)
    {
        handlers.put(new HandlerType(eventType, event, state), handler);
    }

    @SuppressWarnings("unchecked")
    public <C extends StatefulContext> void callOnEventTriggered(EventEnum event, StateEnum stateFrom, StateEnum stateTo, C context) throws Exception
    {
        Handler h = handlers.get(new HandlerType(EventType.EVENT_TRIGGER, event, null));
        if (h != null)
        {
            ContextHandler<C> contextHandler = (ContextHandler<C>) h;
            contextHandler.call(context);
        }
        h = handlers.get(new HandlerType(EventType.ANY_EVENT_TRIGGER, null, null));
        if (h != null)
        {
            final EventHandler<C> eventHandler = (EventHandler<C>) h;
            eventHandler.call(event, stateFrom, stateTo, context);
        }
    }

    public <C extends StatefulContext> void callOnStateEntered(StateEnum state, C context) throws Exception
    {
        Handler h = handlers.get(new HandlerType(EventType.STATE_ENTER, null, state));
        if (h != null)
        {
            ContextHandler<C> contextHandler = (ContextHandler<C>) h;
            contextHandler.call(context);
        }

        h = handlers.get(new HandlerType(EventType.ANY_STATE_ENTER, null, null));
        if (h != null)
        {
            StateHandler<C> stateHandler = (StateHandler<C>) h;
            stateHandler.call(state, context);
        }
    }

    public <C extends StatefulContext> void callOnStateActivate(StateEnum state, C context) throws Exception
    {
        final Handler h = handlers.get(new HandlerType(EventType.STATE_ACTIVATE, null, state));
        if (h != null)
        {
            ContextHandler<C> contextHandler = (ContextHandler<C>) h;
            contextHandler.call(context);
        }
    }

    public <C extends StatefulContext> void callOnStateLeaved(StateEnum state, C context) throws Exception
    {
        Handler h = handlers.get(new HandlerType(EventType.STATE_LEAVE, null, state));
        if (h != null)
        {
            ContextHandler<C> contextHandler = (ContextHandler<C>) h;
            contextHandler.call(context);
        }

        h = handlers.get(new HandlerType(EventType.ANY_STATE_LEAVE, null, null));
        if (h != null)
        {
            StateHandler<C> stateHandler = (StateHandler<C>) h;
            stateHandler.call(state, context);
        }
    }

    public <C extends StatefulContext> void callOnStateDeactivate(StateEnum state, C context) throws Exception
    {
        final Handler h = handlers.get(new HandlerType(EventType.STATE_DEACTIVATE, null, state));
        if (h != null)
        {
            ContextHandler<C> contextHandler = (ContextHandler<C>) h;
            contextHandler.call(context);
        }
    }

    public <C extends StatefulContext> void callOnFinalState(StateEnum state, C context) throws Exception
    {
        final Handler h = handlers.get(new HandlerType(EventType.FINAL_STATE, null, null));
        if (h != null)
        {
            StateHandler<C> contextHandler = (StateHandler<C>) h;
            contextHandler.call(state, context);
        }
    }

    public void callOnError(ExecutionError error)
    {
        final Handler h = handlers.get(new HandlerType(EventType.ERROR, null, null));
        if (h != null)
        {
            ExecutionErrorHandler errorHandler = (ExecutionErrorHandler) h;
            errorHandler.call(error, error.getContext());
        }
    }
}
