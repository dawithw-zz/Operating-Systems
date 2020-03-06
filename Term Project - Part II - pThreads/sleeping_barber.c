#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>

#include "semaphore.h"

#define CHAIRS 5
#define MAX_BARBERS 2
#define MAX_CUSTOMERS 10

// Global variables - Semaphores
semaphore_t *lock;
semaphore_t *customer;
semaphore_t *barber;

int waiting;  // number of people waiting
int customers_handled;     // number of customers processed

// Print Buffer
void print_buffer(int id, int type)
{
  // int i,max;

  // if (type == 0){
  //   printf("Producer %d added item to buffer. \t",id);
  // }
  // else {
  //   printf("Consumer %d removed item from buffer. \t", id);
  // }

  // // print buffer
  // printf("[");
  // for(i = 0; i < BUFFER_SIZE-1; ++i){
  //  printf(" %s , ",buffer[i]);
  // }
  // printf(" %s ]\n", buffer[BUFFER_SIZE-1]);
}

// void get_haircut()
// {
  
// }

// void cut_hair()
// {
//   printf(" is giving a haircut\n");
//   usleep(1000);
//   printf(" Barber is done giving haircut\n");
//   ++haircuts;
// }

void * customerAction(void *threadID)
{
  printf("Customer %d: Enter barbershop\n", threadID);
  
  down(lock);             // get mutex lock
  
  printf("** %d unoccupied chairs\n", waiting);

  if(waiting < CHAIRS)    // proceed only if chairs available
  {
    ++waiting;            // increment number of people waiting
    up(customer);       // wake if barber sleeping
    
    printf("** %d barbers are currently awake\n", barber->count);

    printf("Customer %d: wake up barber\n", threadID);
    //printf("  semaphore customer: %d\n", customer->count);
    

    ++customers_handled;
    up(lock);             // release mutex lock
    
    printf("Customer %d: waiting on barber's signal\n", threadID);
    //printf("  semaphore barber: %d\n", barber->count);
    
    printf("** %d customers are currently waiting.\n", customer->count);

    down(barber);         // wait for barber's signal
    
    printf("Customer %d: has recived barber's signal\n", threadID);
    //printf("  semaphore barber: %d\n", barber->count);

    
    usleep(10000);        // get haircut
    printf("Customer %d: is leaving after having a haircut\n", threadID);
  }
  else
  {
    printf("Customer %d: leaves since barbershop is full\n", threadID);
    ++customers_handled;
    up(lock);              // release mutex lock
  }

  //printf("Total customers handled: %d\n", done);
}

void * barberAction(void *threadID)
{
  int otherBarbers = MAX_BARBERS - 1;
  while(customers_handled < MAX_CUSTOMERS - otherBarbers)
  {
    printf("Barber %d: is checking if customers are available\n", threadID);
    printf("** %d customers are currently waiting\n", customer->count);

    down(customer);      // get customer
    
    //printf("Barber %d: awake\n", threadID);
    //printf("  semaphore customer: %d\n", customer->count);

    down(lock);          // get mutex lock
    --waiting;           // customer leaves waiting area
    
    up(barber);          // notify customer
    
    printf("Barber %d: waves over to a customer\n", threadID);

    printf("** %d customers are currently waiting\n", customer->count);

    up(lock);            // release mutex lock
    
    printf("** %d barbers are currently giving haircuts\n", barber->count);

    usleep(10000);
    printf("Barber %d: finished giving haircut\n", threadID);

    //++done;
  }
  printf("Barber %d: has finished work and is going home\n", threadID);
}

void createBarbers(pthread_t *threads)
{
  int i;
  for(i = 1; i <= MAX_BARBERS; ++i)
  {
    printf("Creating Barber %d\n", i);
    if(pthread_create(&threads[i],NULL,barberAction,(void*)i))
    {
      printf("Failed to create BARBER %d\n", i);
      exit(-1);
    }
  }
}

// Instantiate threads that simulate customers and run them
void createCustomers(pthread_t *threads)
{
  int i = 1;

  // -----------------------
  printf("\n -- Show customer wakes up a barber and gets haircut -- \n\n");
  
  printf("Creating Customer %d\n", 1);
  if(pthread_create(&threads[i],NULL,customerAction,(void*)i))
  {
    printf("Failed to create CUSTOMER %d\n", 1);
    exit(-1);
  }
  ++i;

  usleep(100);
  
  printf("Creating Customer %d\n", 1);
  if(pthread_create(&threads[i],NULL,customerAction,(void*)i))
  {
    printf("Failed to create CUSTOMER %d\n", 2);
    exit(-1);
  }
  ++i;

  usleep(1000000);
  // -----------------------
  
  printf("\n -- Show cutomer leaves when barbershop is full -- \n\n");
  
  for(; i < 9; ++i)
  {
    printf("Creating Customer %d\n", i);
    if(pthread_create(&threads[i],NULL,customerAction,(void*)i))
    {
      printf("Failed to create CUSTOMER %d\n", i);
      exit(-1);
    }
  }
  
  usleep(1000000);
  // -----------------------

  printf("\n -- Ending Scene -- \n\n");
  
  for(; i <= MAX_CUSTOMERS; ++i)
  {
    printf("Creating Customer %d\n", i);
    if(pthread_create(&threads[i],NULL,customerAction,(void*)i))
    {
      printf("Failed to create CUSTOMER %d\n", i);
      exit(-1);
    }
  }
}

// Wait for threads to finish executing
void join(pthread_t *threads, int num_of_threads, int type)
{
  int i;

  //if(type == 1)
  //  printf("Trying to join barbers\n");
  //else
  //  printf("Trying to join customers\n");

  for ( i = 1; i <= num_of_threads; ++i)
  {
    if(pthread_join(threads[i],NULL))
    {
    //  if (type == 1)
    //    printf("ERROR joining Barber %d\n", i);
    //  else
    //    printf("ERROR joining Customer %d\n", i);
      exit(-1);
    }
    //if (type == 1)
    //  printf("Joined Barber %d\n", i);
    //else
    //  printf("Joined Customer %d\n", i);
  }
}

//--------------------------------
int main(void)
{
  // declare variables
  pthread_t barbers[MAX_BARBERS+1];
  pthread_t customers[MAX_CUSTOMERS+1];

  // instantiate variables
  customer = createSemaphore(0);  // waiting customers
  barber = createSemaphore(0);    // active barbers
  lock = createSemaphore(1);      // create a mutex
  
  customers_handled = 0;                       // instantiate haircuts 

  printf("\n-- START SIMULATION -- \n");

  createBarbers(barbers);
  createCustomers(customers);

  // join threads
  join(barbers, MAX_BARBERS, 1);
  join(customers, MAX_CUSTOMERS, 0);

  printf("\n-- END SIMULATION -- \n");

  destroySemaphore(customer);
  destroySemaphore(barber);
  destroySemaphore(lock);

  return 0;
}
