package au.com.ds.ef;

import au.com.ds.ef.call.ContextHandler;
import au.com.ds.ef.call.EventHandler;
import au.com.ds.ef.call.ExecutionErrorHandler;
import au.com.ds.ef.call.StateHandler;
import au.com.ds.ef.err.ExecutionError;
import au.com.ds.ef.err.LogicViolationError;
import pl.com.itsense.easyflow.Action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executor;


/**
 * Event processing occurs recursively in the following order:
 * a) outbounds {@link Transition}s,
 * b) {@link Action}s.
 * c) parent {@link StateEnum} processing
 * User: andrey
 * Date: 3/12/2013
 * Time: 9:52 PM
 */
public class EasyFlow<C extends StatefulContext>
{
    /**
     *
     */
    public class DefaultErrorHandler implements ExecutionErrorHandler<StatefulContext>
    {
        @Override
        public void call(ExecutionError error, StatefulContext context)
        {
            String msg = "Execution Error in StateHolder [" + error.getState() + "] ";
            if (error.getEvent() != null)
            {
                msg += "on EventHolder [" + error.getEvent() + "] ";
            }
            msg += "with Context [" + error.getContext() + "] ";
            System.out.println(msg);
        }
    }

    /** */
    private StateEnum startState;
    /** */
    private TransitionCollection transitions;
    /** */
    private Executor executor;
    /** */
    private final HandlerCollection handlers = new HandlerCollection();
    /** */
    private final HashMap<StateEnum, Action> actions = new HashMap<StateEnum, Action>();
    protected EasyFlow(StateEnum startState)
    {
        this.startState = startState;
        this.handlers.setHandler(EventType.ERROR, null, null, new DefaultErrorHandler());
    }

    protected void setTransitions(Collection<Transition> collection, final boolean skipValidation)
    {
        transitions = new TransitionCollection(collection, !skipValidation);
    }

    /** */
    private void prepare()
    {
        if (executor == null)
        {
            executor = new AsyncExecutor();
        }
    }

    public void start(final C context)
    {
        start(false, context);
    }

    public void start(boolean enterInitialState, final C context)
    {
        prepare();
        context.setFlow(this);
        if (context.getState() == null)
        {
            setCurrentState(startState, false, context);
        }
        else if (enterInitialState)
        {
            setCurrentState(context.getState(), true, context);
        }
    }

    protected void setCurrentState(final StateEnum state, final boolean enterInitialState, final C context)
    {
        execute(new Runnable()
        {
            @Override
            public void run()
            {
                StateEnum prevState = context.getState();
                if (!enterInitialState)
                {
                    if (prevState != null)
                    {
                        leave(prevState, context);
                        StateEnum parent = prevState;
                        while ((parent = parent.getParent()) != null && state != parent && !state.isAncestor(parent))
                        {
                            deactivate(parent, context);
                        }
                    }
                }
                if (!enterInitialState && prevState != null)
                {
                    final ArrayList<StateEnum> states = new ArrayList<StateEnum>();
                    StateEnum parent = state;
                    while ((parent = parent.getParent()) != null && !prevState.isAncestor(parent))
                    {
                        states.add(parent);
                    }
                    for (int i = states.size() - 1; i > -1; i--)
                    {
                        activate(states.get(i), context);
                    }
                }
                context.setState(state);
                enter(state, context);
            }
        }, context);
    }

    protected void execute(final Runnable task, final C context)
    {
        if (!context.isTerminated())
        {
            executor.execute(task);
        }
    }

    public <C1 extends StatefulContext> EasyFlow<C1> setAction(final StateEnum state, final Action<C1> action)
    {
        if (state.getActionEvents() != null && state.getActionEvents().length > 0)
        {
            actions.put(state, new FallbackWrapperAction(action, state.getActionEvents()));
        }
        return (EasyFlow<C1>) this;
    }

    public <C1 extends StatefulContext> EasyFlow<C1> whenEvent(final EventEnum event, final StateEnum state, final ContextHandler<C1> onEvent)
    {
        handlers.setHandler(EventType.EVENT_TRIGGER, state, event, onEvent);
        return (EasyFlow<C1>) this;
    }

    public <C1 extends StatefulContext> EasyFlow<C1> whenEvent(EventEnum event, ContextHandler<C1> onEvent)
    {
        handlers.setHandler(EventType.EVENT_TRIGGER, null, event, onEvent);
        return (EasyFlow<C1>) this;
    }

    public <C1 extends StatefulContext> EasyFlow<C1> whenEvent(EventHandler<C1> onEvent)
    {
        handlers.setHandler(EventType.ANY_EVENT_TRIGGER, null, null, onEvent);
        return (EasyFlow<C1>) this;
    }

    public <C1 extends StatefulContext> EasyFlow<C1> whenEnter(StateEnum state, ContextHandler<C1> onEnter)
    {
        handlers.setHandler(EventType.STATE_ENTER, state, null, onEnter);
        return (EasyFlow<C1>) this;
    }
    /**
     * 
     * @param onEvent
     * @return
     */
    public <C1 extends StatefulContext> EasyFlow<C1> whenDeactivate(EventHandler<C1> onEvent)
    {
        handlers.setHandler(EventType.ANY_STATE_DEACTIVATE, null, null, onEvent);
        return (EasyFlow<C1>) this;
    }

    public <C1 extends StatefulContext> EasyFlow<C1> whenDeactivate(StateEnum state, ContextHandler<C1> onDeactivate)
    {
        handlers.setHandler(EventType.STATE_DEACTIVATE, state, null, onDeactivate);
        return (EasyFlow<C1>) this;
    }


    public <C1 extends StatefulContext> EasyFlow<C1> whenActivate(EventHandler<C1> onEvent)
    {
        handlers.setHandler(EventType.ANY_STATE_ACTIVATE, null, null, onEvent);
        return (EasyFlow<C1>) this;
    }
    
    public <C1 extends StatefulContext> EasyFlow<C1> whenActivate(StateEnum state, ContextHandler<C1> onActivate)
    {
        handlers.setHandler(EventType.STATE_ACTIVATE, state, null, onActivate);
        return (EasyFlow<C1>) this;
    }

    public <C1 extends StatefulContext> EasyFlow<C1> whenEnter(StateHandler<C1> onEnter)
    {
        handlers.setHandler(EventType.ANY_STATE_ENTER, null, null, onEnter);
        return (EasyFlow<C1>) this;
    }

    public <C1 extends StatefulContext> EasyFlow<C1> whenLeave(StateEnum state, ContextHandler<C1> onEnter)
    {
        handlers.setHandler(EventType.STATE_LEAVE, state, null, onEnter);
        return (EasyFlow<C1>) this;
    }

    public <C1 extends StatefulContext> EasyFlow<C1> whenLeave(StateHandler<C1> onEnter)
    {
        handlers.setHandler(EventType.ANY_STATE_LEAVE, null, null, onEnter);
        return (EasyFlow<C1>) this;
    }

    public <C1 extends StatefulContext> EasyFlow<C1> whenError(ExecutionErrorHandler<C1> onError)
    {
        handlers.setHandler(EventType.ERROR, null, null, onError);
        return (EasyFlow<C1>) this;
    }

    public <C1 extends StatefulContext> EasyFlow<C1> whenFinalState(StateHandler<C1> onFinalState)
    {
        handlers.setHandler(EventType.FINAL_STATE, null, null, onFinalState);
        return (EasyFlow<C1>) this;
    }

    public void waitForCompletion(C context)
    {
        context.awaitTermination();
    }

    public <C1 extends StatefulContext> EasyFlow<C1> executor(Executor executor)
    {
        this.executor = executor;
        return (EasyFlow<C1>) this;
    }

    public boolean safeTrigger(final EventEnum event, final C context)
    {
        try
        {
            return trigger(event, true, context);
        }
        catch (LogicViolationError logicViolationError)
        {
            return false;
        }
    }

    public boolean trigger(final EventEnum event, final C context) throws LogicViolationError
    {
        return trigger(event, false, context);
    }

    public List<Transition> getAvailableTransitions(StateEnum stateFrom)
    {
        return transitions.getTransitions(stateFrom);
    }

    public boolean isEventHandledByState(final StateEnum state, final EventEnum event)
    {
        for (Transition transition : transitions.getTransitions(state))
        {
            if (transition.getEvent() == event)
            {
                return true;
            }
        }
        return false;
    }
    /**
     * Test if state machine can handle the given event for the current context.
     * @param event {@link EventEnum}
     * @param context {@link C}
     * @return
     */
    public boolean isEventHandled(final EventEnum event, final C context)
    {
        boolean eventHandled = false;
        if (!context.isTerminated())
        {
            StateEnum state = context.getState();
            Transition transition = null;
            while (!eventHandled && state != null)
            {
                transition = transitions.getTransition(state, event);
                if (transition == null)
                {
                    eventHandled = (actions.get(state) instanceof FallbackWrapperAction) && ((FallbackWrapperAction)actions.get(state)).isHandled(event);
                    state = state.getParent();
                }
                else
                {
                    eventHandled = true;
                }
            }
        }
        return eventHandled;
    }
    /**
     * Null-safe action execution
     * @param <E>
     * @return
     */
    private boolean executeAction(final Action<C> action, final EventEnum event, final C context, final StateEnum state)
    {
        boolean consumed = false;
        if (action != null)
        {
            try
            {
                consumed = action.call(context, event);
            }
            catch (Exception e)
            {
                doOnError(new ExecutionError(state, event, e, "Execution Error during action execution" + e.getMessage(), context));
            }
        }
        return consumed;
    }

    /**
     * Null-safe transition processing
     * @param transition
     * @param event
     * @param context
     */
    private void doTransition(final Transition transition, final EventEnum event, final C context)
    {
        if (transition != null)
        {
            execute(new Runnable()
            {
                @Override
                public void run()
                {
                    final StateEnum stateFrom = transition.getStateFrom();
                    final StateEnum stateTo = transition.getStateTo();
                    try
                    {
                        context.setCurrentTransition(transition);
                        handlers.callOnEventTriggered(event, stateFrom, stateTo, context);
                        context.setLastEvent(event);
                        setCurrentState(stateTo, false, context);
                    }
                    catch (Exception e)
                    {
                        doOnError(new ExecutionError(stateFrom, event, e, "Execution Error in [trigger]" + e.getMessage(), context));
                    }
                }
            }, context);
        }
    }
    /** */
    private boolean trigger(final EventEnum event, final boolean safe, final C context) throws LogicViolationError
    {
        boolean eventConsumed = false;
        final StateEnum stateFrom = context.getState();
        if (!context.isTerminated())
        {
            StateEnum state = stateFrom;
            Transition transition = null;
            while (!eventConsumed && state != null)
            {
                transition = transitions.getTransition(state, event, true);
                if (transition == null)
                {
                    eventConsumed = executeAction(actions.get(state), event, context, state);
                    state = state.getParent();
                }
                else
                {
                    doTransition(transition, event, context);
                    eventConsumed = true;
                }
            }
        }
        if (!eventConsumed)
        {
            if (safe)
            {
                throw new LogicViolationError("Invalid Event: " + event + " triggered while in state: " + stateFrom + " for " + context);
            }
        }
        if (!eventConsumed && safe)
        {
            throw new LogicViolationError("Invalid Event: " + event + " triggered while in State: " + stateFrom + " for " + context);
        }
        return eventConsumed;
    }
    /**
     * Invokes a handler which is registered for "onEnter" event
     * @param state {@link StateEnum}
     * @param context
     */
    public void invokeOnStateEnteredHandler(final StateEnum state, final C context)
    {
        if (state != null && context != null)
        {
            enter(state, context);
        }
    }
    private void enter(final StateEnum state, final C context)
    {
        if (context.isTerminated())
        {
            return;
        }
        try
        {
            handlers.callOnStateEntered(state, context);
            if (transitions.isFinal(state))
            {
                doOnTerminate(state, context);
            }
        }
        catch (Exception e)
        {
            doOnError(new ExecutionError(state, null, e, "Execution Error in [whenEnter] handler", context));
        }
    }

    /** */
    private void deactivate(final StateEnum state, final C context)
    {
        if (context.isTerminated())
        {
            return;
        }
        try
        {
            handlers.callOnStateDeactivate(state, context);
        }
        catch (Exception e)
        {
            doOnError(new ExecutionError(state, null, e, "Execution Error in [whenLeave] handler", context));
        }
    }

    /** */
    private void activate(final StateEnum state, final C context)
    {
        if (context.isTerminated())
        {
            return;
        }
        try
        {
            handlers.callOnStateActivate(state, context);
        }
        catch (Exception e)
        {
            doOnError(new ExecutionError(state, null, e, "Execution Error in [whenLeave] handler", context));
        }
    }

    /** */
    private void leave(StateEnum state, final C context)
    {
        if (context.isTerminated())
        {
            return;
        }
        try
        {
            handlers.callOnStateLeaved(state, context);
        }
        catch (Exception e)
        {
            doOnError(new ExecutionError(state, null, e, "Execution Error in [whenLeave] handler", context));
        }
    }

    protected void doOnError(final ExecutionError error)
    {
        handlers.callOnError(error);
        doOnTerminate(error.getState(), (C) error.getContext());
    }

    protected StateEnum getStartState()
    {
        return startState;
    }

    protected void doOnTerminate(StateEnum state, final C context)
    {
        if (!context.isTerminated())
        {
            try
            {
                context.setTerminated();
                handlers.callOnFinalState(state, context);
            }
            catch (Exception e)
            {
            }
        }
    }

    private class FallbackWrapperAction<C1 extends StatefulContext> implements Action<C1>
    {
        /** */
        private final Action<C1> action;
        /** */
        private final HashSet<EventEnum> events = new HashSet<EventEnum>();
        /** */
        private FallbackWrapperAction(final Action<C1> action, final EventEnum... events)
        {
            this.action = action;
            this.events.addAll(Arrays.asList(events));
        }

        @Override
        public boolean call(final C1 context, final EventEnum event) throws Exception
        {
            return isHandled(event) && action.call(context, event);
        }
        /**
         *
         * @return
         */
        private boolean isHandled(final EventEnum event)
        {
            return events.contains(event);
        }
    }
}
