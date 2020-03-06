public class Producer
{
	private static int counter = 0;
	private final int unique_id;
	private final int quota;
	private int produced;
	private boolean blocked;
	private boolean stop;
	private Item item;

	public Producer(int q)
	{
		counter = counter + 1;
		unique_id = counter;
		quota = q;
		produced = 0;
		blocked = false;
		stop = false;
		item = null;
		
	}

	public void run(BoundedBuffer<Item> b)
	{
		if (produced < quota)
		{
			if (item == null)
			{
				// create a new item
				item = new Item();
			}

			// add item to buffer
			b.insert(item, this);

			// signal insert was successful
			if(!blocked)
			{
				item = null;
				++produced;
			}
		}
		
		if (produced == quota)
		{
			System.out.println("Producer" + unique_id + " FINISHED producing " + quota + " items.");
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
		return "Producer" + unique_id;
	}
}