public class Item
{
	public static int counter = 1;
	public final int unique_id;

	public Item ()
	{
		unique_id = counter;
		++counter;
	}

	public String toString()
	{
		return "Item #" + unique_id;
	}
}