import java.awt.*;
import java.util.Random;

public class ProducerConsumerDemo
{
	public static void main (String[] args)
	{
		BoundedBuffer<Item> b = new BoundedBuffer<Item> (3);
		Random randomGenerator = new Random();
		int done = 0; 				// increments when a process is done

		Producer p1 = new Producer(20);
		Producer p2 = new Producer(20);
		Consumer c1 = new Consumer(20);
		Consumer c2 = new Consumer(20);

		while (done < 4)  	// exists when all processes are done
		{
			// random selector for which process runs
			int randomNumber = randomGenerator.nextInt(4);
			switch(randomNumber)		// Randomly select a "process" to run
			{
				case 0:		// run Producer 1
				{
					if(!p1.done())
					{
						p1.run(b);

						// check if done
						if (p1.done())
						{
							++done;
						}
					}
					break;
				}
				case 1: 	// run Producer 2
				{
					if(!p2.done())
					{
						p2.run(b);

						// check if done
						if (p2.done())
						{
							++done;
						}
					}
					break;
				}
				case 2: 	// run Consumer 1
				{
					if(!c1.done())
					{
						c1.run(b);

						// check if done
						if (c1.done())
						{
							++done;
						}
					}
					break;
				}
				case 3: 	// run Consumer 2
				{
					if(!c2.done())
					{
						c2.run(b);

						// check if done
						if (c2.done())
						{
							++done;
						}
					}
					break;
				}
			}
		}
	}
}