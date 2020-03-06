#ifndef SEMAPHORE
#define SEMAPHORE

#include <pthread.h>
#include <sys/queue.h>
#include <stdio.h>

// Node of SIMPLEQ
struct entry
{
  pthread_cond_t condition;             // condition variable
  SIMPLEQ_ENTRY(entry) next;            // link to next entry
} *np;

// Semaphore
typedef struct semaphore
{
  int count;                            // count 
  pthread_mutex_t mutex;                // lock
  pthread_cond_t condition;             // condition
  //SIMPLEQ_HEAD(queuehead, entry) head;  // queue to hold the threads
} semaphore_t;

// Function declarations
semaphore_t* createSemaphore( int initialCount );
void destroySemaphore( semaphore_t* sem );
void down( semaphore_t* sem);
void up( semaphore_t* sem);

#endif
