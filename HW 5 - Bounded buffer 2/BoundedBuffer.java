import java.lang.Object;

public class BoundedBuffer<E> {
	private E[] buffer;							// buffer
	private final int n;						// buffer size
	private int in;								// slot for addition
	private int out;							// slot for consumption
	private Object aLock;						// lock
	private long inserted;						// number of items inserted
	private long removed;						// number of items removed

	// Constructor
	public BoundedBuffer(int size)
	{
		// Create buffer
		n = size;
		buffer = (E[]) new Object[size];
		
		// Clean buffer empty
		for (int i = 0; i < n; ++i)
		{
			buffer[i] = null;
		}

		// Instantiate variables
		in = 0;
		out = 0;
		inserted = 0;
		removed = 0;
		aLock = new Object();
	}

	// Insert method
	public void insert (E item, Producer p) throws InterruptedException
	{
		// Critical section of producer
		synchronized (aLock) 
		{
			
			/* Note: in == out only when buffer is empty/full. 
			buffer[out] is null only when buffer is empty. */
			while((in == out) && (buffer[out] != null)) 	
			{
				//System.out.println("FULL BUFFER");
				// If buffer is full
				try {
					// wait until notified
					System.out.println(p.toString() + " waiting to insert " + item.toString());
					aLock.wait();
				} catch (InterruptedException e) {
					throw e; 	// propagate the exception
				}
			}

			// Add item to buffer
			buffer[in] = item;
			in = (in+1)%n;
			++inserted;
			
			// notify waiting threads
			aLock.notifyAll();
			
			// available space in buffer
			long m = inserted - removed;				
			System.out.println(p.toString() + " inserted " + item.toString() + "\t(" + m + " of " + n + " slots full)");
		}
	}

	// Remove method
	public E remove (Consumer c) throws InterruptedException
	{	
		E item;

		// Cirtical section of consumer
		synchronized(aLock)
		{
			// If buffer is empty
			while((in == out) && (buffer[out] == null))
			{
				//System.out.println("EMPTY BUFFER");
				try 
				{
					// No item available for removing
					System.out.println(c.toString() + " waiting to remove an item");
					aLock.wait();
				} catch (InterruptedException e){
					throw e;		// propagate the exception
				}
			}

			// Remove item from buffer
			item = buffer[out];
			buffer[out] = null;
			out = (out+1)%n;
			++removed;
			
			aLock.notifyAll();
			
			long m = inserted - removed;		// available space in buffer
			System.out.println(c.toString() + " removed " + item.toString() + "\t(" + m + " of " + n + " slots full)");		
		}
		return item;
	}


	// Print buffer to String
	public String toString()
	{
		String empty = "---";
		String list = "[";
		for (int i = 0; i < buffer.length; ++i)
		{
			if(buffer[i]==null)
			{
				// No object
				list += empty;
			}
			else
			{
				// Print object
				list += buffer[i].toString();
			}

			// Add separator
			if (i < buffer.length - 1)
			{
				list += ", ";
			}
		}
		// closing bracket
		list += "]";
		return list;
	}
}