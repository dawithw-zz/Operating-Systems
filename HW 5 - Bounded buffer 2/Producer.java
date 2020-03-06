import java.util.Random;

public class Producer implements Runnable
{
	private static int counter = 1;
	private final int unique_id;
	private int slept;
	private int produced;
	private static BoundedBuffer<Item> buffer;
	private Random randomGenerator;

	public Producer(BoundedBuffer<Item> b)
	{
		// Set Uniquie ID for producer
		unique_id = counter;
		++counter;

		// Instatiate variables
		produced = 0;
		slept = 0;
		buffer = b;
		randomGenerator = new Random();
	}

	public void run()
	{
		while (true)
		{
			// Generate random numbers
			int randomNumber = randomGenerator.nextInt(5) + 1;
			int randomTime = randomGenerator.nextInt(91) + 10;
			
			System.out.println("Producer" + unique_id + " ready to produce " + randomNumber + " items");
			
			try
			{
				// Produce random number of items
				for (int i = 0; i < randomNumber; ++i)
				{
					// create item
					Item item = new Item();

					// insert item in buffer
					/* This might throw an InterruptedException if the producer
					is wait()ing and a Thread.interrupt is executed on this thread */
					buffer.insert(item ,this);
					++produced;
				}	

				// Make this sleep for random amount of time
				System.out.println("Producer " + unique_id + " napping " + randomTime + "ms");
				Thread.sleep(randomTime);

				// Keep track of time slept
				slept += randomTime;		
			} 
			catch (InterruptedException e) 	
			{
				// Notify and terminate gracefully
				System.out.println("Producer " + unique_id + " TERMINATING");
				return;			
			}	
		}
	}

	public String toString()
	{
		return "Producer" + unique_id;
	}

	public String getStatus()
	{
		String status = "Producer " + unique_id + " produced " + produced + " items and slept " + slept + "ms";
		return status;
	}
}