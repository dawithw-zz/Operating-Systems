import java.lang.Object;

public class BoundedBuffer<E> {
	private E[] buffer;							// buffer
	private final int n;						// buffer size
	private int in;								// slot for addition
	private int out;							// slot for consumption
	private Object pLock = new Object();		// producer lock
	private Object cLock = new Object();		// consumer lock
	private int added;							// number of items added
	private int removed;						// number of items removed
	private final static String empty = "---";	// empty slot

	// Constructor
	public BoundedBuffer(int size)
	{
		n = size;
		// create a buffer array of specified size
		buffer = (E[]) new Object[size];
	}

	// Insert method
	public void insert (E item, Producer p)
	{
		synchronized (pLock) 
		{
			// buffer is full 
			if (in == out && buffer[out] != null)
			{
				if (p.locked())
				{
					return;
				}

				p.block();	
				// No space available for inserting
				System.out.println(p.toString() + " waiting to insert " + item.toString());
				return;
			}
			else
			{
				p.unblock();
				// insert item in buffer
				buffer[in] = item;
				in = (in+1)%n;
				++added;
			}
		}

		int m = added - removed;
		System.out.println(p.toString() + " inserted " + item.toString() + "\t(" + m + " of " + n + " slots full)");
	}

	// Remove method
	public E remove (Consumer c)
	{
		E item;
		synchronized (cLock)
		{
			// buffer is empty
			if (in == out && buffer[out] == null)
			{
				if (c.locked())
				{
					return null;
				}

				c.block();
				// No item available for removing
				System.out.println(c.toString() + " waiting to remove an item");
				return null;

			}
			else
			{
				c.unblock();
				// remove item from buffer
				item = buffer[out];
				buffer[out] = null;
				out = (out+1)%n;
				++removed;
			}
			
		}
		int m = added - removed;
		// Item removed
		System.out.println(c.toString() + " removed " + item.toString() + "\t(" + m + " of " + n + " slots full)");
		return item;
	}

	// Print buffer to String
	public String toString()
	{
		String list = "[";
		for (int i = 0; i < buffer.length; ++i)
		{
			if(buffer[i]==null)
			{
				// No object
				list = list + empty;
			}
			else
			{
				// Print object
				list = buffer[i].toString();
			}

			// Add separator
			if (i < buffer.length - 1)
			{
				list = list + ", ";
			}
		}
		// closing bracket
		list = list + "]";
		return list;
	}
}