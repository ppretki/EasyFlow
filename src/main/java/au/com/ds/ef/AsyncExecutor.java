package au.com.ds.ef;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AsyncExecutor implements Executor
{
    /** */
	private final Executor executor = Executors.newSingleThreadExecutor();
	
	@Override
	public void execute(final Runnable task) 
	{
		executor.execute(task);
	}
}
