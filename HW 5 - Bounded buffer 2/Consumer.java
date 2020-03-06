import java.util.Random;

public class Consumer implements Runnable
{
	private static int counter = 1;
	private final int unique_id;
	private int slept;
	private int consumed;
	private static BoundedBuffer<Item> buffer;
	private Random randomGenerator;

	public Consumer(BoundedBuffer<Item> b)
	{
		// Set Unique ID for consumer
		unique_id = counter;
		++counter;

		// Instantiate variables
		consumed = 0;
		slept = 0;
		buffer = b;
		randomGenerator = new Random();
	}

	public void run()
	{
		while(true)
		{
			// random number range: [1-5]
			int randomNumber = randomGenerator.nextInt(5) + 1;
			int randomTime = randomGenerator.nextInt(91) + 10;

			System.out.println("Consumer" + unique_id + " ready to consume " + randomNumber + " items");
			
			try {
				// Consume random number of times
				for (int i = 0; i < randomNumber; ++i)
				{
					// Consume item
					Item item = buffer.remove(this);
					++consumed;
				}

				// Make this sleep for random amount of time
				System.out.println("Consumer " + unique_id + " napping " + randomTime + "ms");
				Thread.sleep(randomTime);

				// Keep track of time slept
				slept += randomTime;			
			} 
			catch (InterruptedException e) 	
			{
				// Notify and terminate gracefully
				System.out.println("Consumer " + unique_id + " TERMINATING");
				return;			
			}	
		}
	}


	public String toString()
	{
		return "Consumer" + unique_id;
	}

	public String getStatus()
	{
		String status = "Consumer " + unique_id + " consumed " + consumed + " items and slept " + slept + "ms";
		return status;
	}
}