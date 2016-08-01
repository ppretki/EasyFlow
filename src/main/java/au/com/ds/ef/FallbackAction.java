package au.com.ds.ef;

import au.com.ds.ef.call.Handler;

/**
 * Action is invoked by unhandled event (event which doesn't trigger any transition).
 * @author P.Pretki
 * @param <C>
 */
public interface FallbackAction<C extends StatefulContext> extends Handler
{
    /**
     * @param context {@link StatefulContext}
     * @param event {@link EventEnum}
     * @return <code>true</code> if action consumes the event
     * @throws Exception {@link Exception}
     */
    boolean call(C context, EventEnum event) throws Exception;
}
