package au.com.ds.ef.err;

import java.util.Objects;

public class LogicViolationError extends Exception
{
	/** */
    private static final long serialVersionUID = 904045792722645067L;
    /**
     * @param message
     */
    public LogicViolationError(final String message)
    {
        super(message);
    }
    
    @Override
    public boolean equals(final Object o) 
    {
        if (o == this) return true;
        if (!(o instanceof LogicViolationError)) return false;
    	return Objects.equals(getMessage(), ((LogicViolationError)o).getMessage());
    }
    
    
}
