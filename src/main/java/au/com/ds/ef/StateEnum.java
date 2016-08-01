package au.com.ds.ef;

/**
 * User: andrey
 * Date: 3/12/2013
 * Time: 9:52 PM
 */
public interface StateEnum
{
    /**
     *
     * @return name of the state
     */
    String name();

    /**
     *
     * @return
     */
    StateEnum getParent();

    /**
     *
     * @param state
     * @return
     */
    boolean isAncestor(final StateEnum state);
    /**
     *
     */
    EventEnum[] getActionEvents();
}
