package pl.com.itsense.easyflow.test;

import java.util.ArrayList;
import java.util.List;

import au.com.ds.ef.StatefulContext;

/**
 * 
 * @author P.Pretki
 *
 */
public class TestContext extends StatefulContext
{
	/** */
	private final ArrayList<TestCheckPoint> checkpoints = new ArrayList<TestCheckPoint>();
	/** */
	private boolean started;
	/** */
	public TestContext()
	{
	}

	/** */
	public TestContext(boolean started)
	{
		if (started)
		{
			start();
		}
	}

	/**
	 * 
	 * @param checkPoint
	 */
	public void add(final TestCheckPoint point)
	{
		if (started)
		{
			checkpoints.add(point);
		}
	}
	/**
	 * 
	 */
	public void addAll(final List<TestCheckPoint> points)
	{
		if (started)
		{
			checkpoints.addAll(points);
		}
	}

	@Override
	public boolean equals(final Object o) 
	{
        if (o == this) return true;
        if (!(o instanceof TestContext)) return false;
		return checkpoints.equals(((TestContext)o).checkpoints);
	}
	
	/**
	 * 
	 */
	public void start()
	{
		started = true;
	}
	
	@Override
	public String toString() 
	{
		final StringBuilder sb = new StringBuilder("----------------- CHECKPOINTS -----------------\n");
		for (final TestCheckPoint checkPoint : checkpoints)
		{
			sb.append(checkPoint).append("\n");
		}
		return sb.toString();
	}
}
