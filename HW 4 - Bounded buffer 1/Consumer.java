public class Consumer
{
	private static int counter = 0;
	private final int unique_id;
	private final int quota;
	private int consumed;
	private boolean blocked;
	private boolean stop;
	private Item item;
	

	public Consumer(int q)
	{
		counter = counter + 1;
		unique_id = counter;
		quota = q;
		consumed = 0;
		blocked = false;
		stop = false;
		item = null;
	}

	public void run(BoundedBuffer<Item> b)
	{
		if (consumed < quota)
		{
			item = b.remove(this);
			if (!blocked)		// consume failed due to block
			{
				item = null;
				++consumed;

			}
		}

		// Reached quota
		if (consumed == quota)
		{
			System.out.println("Consumer" + unique_id + " FINISHED consuming " + quota + " items.");
			stop = true;
		}
	}

	public void block()
	{
		blocked = true;
	}

	public void unblock()
	{
		blocked = false;
	}

	public boolean done()
	{
		if (stop)
		{
			return true;
		}
		return false;
	}

	public boolean locked()
	{
		if (blocked)
		{
			return true;
		}
		return false;
	}

	public String toString()
	{
		return "Consumer" + unique_id;
	}
}