public class Item
{
	public static int counter = 0;
	public final int unique_id;

	public Item ()
	{
		counter = counter + 1;
		unique_id = counter;
	}

	public String toString()
	{
		return "Item #" + unique_id;
	}
}