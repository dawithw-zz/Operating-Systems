import java.awt.*;

public class ProducerConsumerDemo
{
	public static void main (String[] args)
	{
		BoundedBuffer<Item> b = new BoundedBuffer<Item> (3);
		
		// Create Producers and Consumers
		Producer p1 = new Producer(b);
		Producer p2 = new Producer(b);
		Consumer c1 = new Consumer(b);
		Consumer c2 = new Consumer(b);

		// Create Threads for Ps and Cs
		Thread thread_p1 = new Thread(p1);
		Thread thread_p2 = new Thread(p2);
		Thread thread_c1 = new Thread(c1);
		Thread thread_c2 = new Thread(c2);

		// Start threads
		thread_p1.start();
		thread_p2.start();
		thread_c1.start();
		thread_c2.start();

		try {
			// sleep for 5 seconds
			Thread.sleep(5000);

			// Interrupt everyone
			thread_p1.interrupt();
			thread_p2.interrupt();
			thread_c1.interrupt();
			thread_c2.interrupt();

			// Wait for threads to finish execution
			thread_p1.join();
			thread_p2.join();
			thread_c1.join();
			thread_c2.join();
		} catch (InterruptedException e){
			System.out.println("Exception occured in main");
		}
		

		// Get status of each thread
		System.out.println(p1.getStatus());
		System.out.println(p2.getStatus());
		System.out.println(c1.getStatus());
		System.out.println(c2.getStatus());

		// Print contents of bounded buffer
		String bufferContents = b.toString();
		System.out.println("BoundedBuffer := " + bufferContents);	
	}
}