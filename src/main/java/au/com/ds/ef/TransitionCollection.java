package au.com.ds.ef;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import au.com.ds.ef.err.DefinitionError;

/**
 * User: andrey
 * Date: 6/12/2013
 * Time: 2:08 PM
 */
final class TransitionCollection
{
    private Map<StateEnum, Map<EventEnum, Transition>> transitionFromState = new HashMap<StateEnum, Map<EventEnum, Transition>>();
    private Set<StateEnum> finalStates = new HashSet<StateEnum>();
    /**
     *
     * @param transitions collection of {@link Transition}
     * @param validate if <code>true</code> then validation will be performed
     */
    protected TransitionCollection(final Collection<Transition> transitions, final boolean validate)
    {
        if (transitions != null)
        {
            for (Transition transition : transitions)
            {
                Map<EventEnum, Transition> map = transitionFromState.get(transition.getStateFrom());
                if (map == null)
                {
                    map = new HashMap<EventEnum, Transition>();
                    transitionFromState.put(transition.getStateFrom(), map);
                }
                map.put(transition.getEvent(), transition);
                if (transition.isFinal())
                {
                    finalStates.add(transition.getStateTo());
                }
            }
        }

        if (validate)
        {
            if (transitions == null || transitions.isEmpty())
            {
                throw new DefinitionError("No transitions defined");
            }

            final HashSet<Transition> processedTransitions = new HashSet<Transition>();
            for (Transition transition : transitions)
            {
                final StateEnum stateFrom = transition.getStateFrom();
                final StateEnum stateTo = transition.getStateTo();
                if (finalStates.contains(stateFrom))
                {
                    throw new DefinitionError("Some events defined for final State: " + stateFrom);
                }

                if (processedTransitions.contains(transition))
                {
                    throw new DefinitionError("Ambiguous transitions: " + transition);
                }

                if (stateFrom.equals(stateTo))
                {
                    throw new DefinitionError("Circular transition: " + transition);
                }

                if (!finalStates.contains(stateTo))
                {
                    StateEnum state = stateTo;
                    boolean isFinal = !transitionFromState.containsKey(stateTo);
                    while (isFinal && (state = state.getParent()) != null)
                    {
                        isFinal = !transitionFromState.containsKey(state);
                    }
                    if (isFinal)
                    {
                        throw new DefinitionError("No events defined for non-final State: " + stateTo);
                    }
                }
                processedTransitions.add(transition);
            }
        }
    }

    /**
    *
    * @param stateFrom
    * @param event
    * @return
    */
   public Transition getTransition(final StateEnum stateFrom, final EventEnum event)
   {
       return getTransition(stateFrom, event, false);
   }
    /**
     *
     * @param stateFrom
     * @param event
     * @return
     */
    public Transition getTransition(final StateEnum stateFrom, final EventEnum event, final boolean currentStateOnly)
    {
        final Map<EventEnum, Transition> transitionMap = transitionFromState.get(stateFrom);
        final Transition result = transitionMap == null ? null : transitionMap.get(event);
        return result == null && !currentStateOnly && stateFrom.getParent() != null ? getTransition(stateFrom.getParent(), event) : result;
    }

    public List<Transition> getTransitions(final StateEnum stateFrom)
    {
        Map<EventEnum, Transition> transitionMap = transitionFromState.get(stateFrom);
        return transitionMap == null ? Collections.<Transition> emptyList() : new ArrayList<Transition>(transitionMap.values());
    }

    protected boolean isFinal(final StateEnum state)
    {
        return finalStates.contains(state);
    }
}
