// DiningPhiliphers.java (skeleton)
//
// - a classic synchronization problem
//
// Skeleton code derived from Dave Small's DiningPhiliphers.java v4.0

import java.util.Random;

//================================================ class DiningPhilosophers

class DiningPhilosophers
{
  public static void main( String[] arg )
  {
    new DiningPhilosophers( 10, 60000 );
  }

  private String[] name = { "Seneca", "Aristotle", "Epicurius", "Voltaire", 
			    "Kant", "Machiavelli", "Nietzsche", "Socrates", 
			    "Frege", "Hume" };

  private Philosopher[] thinker;
  private Chopstick[]   chopstick;

  public DiningPhilosophers( int numPhilosophers, int duration )
  {
    initialize( numPhilosophers );  // construct the philosophers & chopsticks
    startSimulation(); 
    sleep( duration );              // let simulation run for desired time
    shutdownPhilosophers();         // *gracefully* shut down the philosophers
    printResults();
  }

  private void initialize( int n ) 
  {                                 // handles 2 to 10 philosophers
    if ( n > 10 )
      n = 10;
    else if ( n < 2 )
      n = 2;

    thinker = new Philosopher[n];
    chopstick = new Chopstick[n];

    for ( int i = 0 ; i < n ; i++ )
      chopstick[i] = new Chopstick(i);

    for ( int i = 0 ; i < n ; i++ )
      thinker[i] = new Philosopher( name[i], chopstick[i], chopstick[(i+1)%n] );
  }

  private void startSimulation()
  {
    int n = thinker.length; // the number of philosophers

    System.out.print( "Our " + n + " philosophers (" );
    for ( int i = 0 ; i < (n-1) ; i++ )
      System.out.print( name[i] + ", " );
    System.out.println( "and " + name[n-1] +
			") have gather to think and dine" );

    System.out.println( "-----------------------------------------------");

    for ( int i = 0 ; i < n ; i++ )
      thinker[i].start();
  }

  private void sleep( int duration )
  {
    try
    {
      Thread.currentThread().sleep( duration );
    }
    catch ( InterruptedException e )
    {
      Thread.currentThread().interrupt();
    }
  }

  private void shutdownPhilosophers()
  {
    int n = thinker.length;   // number of philosophers
    
    try
    {
      // Interrupt philosophers and wait till done
      for(int i = 0; i < n; ++i)
      {
        thinker[i].interrupt();
        thinker[i].join();
      }
    }
    catch ( InterruptedException e)
    {
      Thread.currentThread().interrupt();
    }
  }

  private void printResults()
  {
    System.out.println( "-----------------------------------------------");

    int n = thinker.length; // the number of philosophers

    for ( int i = 0 ; i < n ; i++ )
      System.out.println( thinker[i] );

    System.out.flush();
  }
}

//================================================ class Philosopher

class Philosopher extends Thread
{
  static private Random random = new Random();

  private String name;
  private Chopstick leftStick;
  private Chopstick rightStick;

  private int eatingTime   = 0;
  private int thinkingTime = 0;
  private int countEat     = 0;
  private int countThink   = 0;

  public Philosopher( String name, Chopstick leftStick, Chopstick rightStick )
  {
    this.name = name;
    this.leftStick = leftStick;
    this.rightStick = rightStick;
  }

  public String toString()
  {
    return name + " ate " + countEat + " times (" +
      eatingTime + " ms) and pondered " + countThink + " times (" +
      thinkingTime + "ms)";
  }
  
  public void run()
  {

    while (true)
    {
      try
      {
        countThink++;
        thinkingTime += doAction( "think" );
        pickupChopsticks();
        countEat++;
        eatingTime += doAction( "eat" );
        putdownChopsticks();
      }
      catch(InterruptedException e)
      {
        return;
      }
    } 

  }

  private int doAction( String act ) throws InterruptedException
  {
    int time = random.nextInt( 4000 ) + 1000 ;
    System.out.println( name + " is begining to " + act + " for " + time + 
      " milliseconds" );
    sleep( time );

    System.out.println( name + " is done " + act + "ing" );

    return time;
  }

  private void pickupChopsticks() throws InterruptedException
  {
    boolean haveChopsticks = false;


    while (!haveChopsticks)
    {
      
      System.out.println( name + " wants right " + rightStick );
    
      // Try to get rightStick
      while(!rightStick.pickUp(this))
      {
        synchronized (rightStick)
        {
          try    // Wait for right stick to be available
          {
            rightStick.wait();
          }
          catch (InterruptedException e)
          {
            throw e;
          }        
        }
      }
    
      System.out.println( name + " has right " + rightStick );
      System.out.println( name + " wants left " + leftStick );

      // Try to get leftStick
      if (!leftStick.pickUp(this))
      {
        // --- Left stick is unavailable -- 

        // Put down rightStick
        rightStick.putDown(this);

        System.out.println( name + " was unable to get the left " + leftStick );
        System.out.println( name + " politely returned right " + rightStick );

        synchronized (rightStick)
        {
          // Let philosopher to the right know rightStick is available
          rightStick.notify();
        }

        synchronized (leftStick)
        {  
          try   // Wait until leftStick is available
          {
             leftStick.wait();
          }
          catch (InterruptedException e)
          {
            throw e;
          }
         
        }

        // Try to get both chopsticks again
        continue;
      }
    
      // Mission accomplished!
      System.out.println( name + " has both left " + leftStick +
        " and right " + rightStick );

      haveChopsticks = true;
    }
    
  }

  private void putdownChopsticks()
  {
    try 
    {
      rightStick.putDown(this);
      System.out.println( name + " finished using right " + rightStick );

      synchronized (rightStick)
      {
        // Let the philosopher to the right know chopstick is available
        rightStick.notify();
      }

      leftStick.putDown(this);
      System.out.println( name + " finished using left " + leftStick );

      synchronized (leftStick)
      {
        // Let the philosopher to the left know chopstick is available
        leftStick.notify();
      }
    }
    catch (RuntimeException e)
    {
      Thread.currentThread().interrupt();
    }
  }
}

//================================================ class Chopstick

class Chopstick
{
  private final int id;
  private Philosopher heldBy = null;

  public Chopstick( int id )
  {
    this.id = id;
  }

  public String toString()
  {
    return "chopstick (" + id + ")";
  }

  synchronized public boolean pickUp( Philosopher p)
  {
    // Chopstick being used by a philosopher
    if (heldBy != null)
    {
      // Unable to pick up
      return false;
    }

    // Pick up chopstick
    heldBy = p;
    return true;
  }

  synchronized public void putDown( Philosopher p)
  {
    if (heldBy == p)
      heldBy = null;

    else
      throw new RuntimeException( "Exception: " + p + " attempted to put " +
        "down a chopstick he wasn't holding." );
  }
}
