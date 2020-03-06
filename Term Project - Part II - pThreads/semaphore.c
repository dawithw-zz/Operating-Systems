#include "semaphore.h"

// // Adds a condition to the tail of a queue and returns the pointer to condition
// pthread_cond_t* add_condition(semaphore_t* sem)
// {
//   np = (struct entry*) malloc(sizeof(struct entry));  // create node
//   pthread_cond_init(&(np->condition),NULL);           // initialize condition
//   SIMPLEQ_INSERT_TAIL(&(sem->head), np, next);        // add node to queue
//   return &(np->condition);
// }

// // Get the first condition in queue
// pthread_cond_t* get_first_cond(semaphore_t* sem)
// {
//   np = SIMPLEQ_FIRST(&(sem->head));               
//   return &(np->condition);                            
// }

// // Remove a condition from the head of the queue and associated node
// void pop_condition(semaphore_t* sem)
// {
//   struct entry* temp = SIMPLEQ_FIRST(&(sem->head)); // get first node

//   // pop node from head of queue
//   SIMPLEQ_REMOVE_HEAD(&(sem->head), (np = SIMPLEQ_FIRST(&(sem->head))), next );
  
//   pthread_cond_destroy(&(temp->condition));         // delete condition
//   free(temp);                                       // free memory of node
// }


semaphore_t* createSemaphore(int initialCount)
{
  // allocate memory for semaphore
  semaphore_t *sem =  (semaphore_t *) malloc(sizeof(semaphore_t));

  // initialize semaphore variables
  // SIMPLEQ_INIT(&(sem->head)); 
  sem->count = initialCount;
  pthread_mutex_init(&(sem->mutex),NULL);
  pthread_cond_init(&(sem->condition),NULL);

  return sem;
}

void destroySemaphore(semaphore_t* sem )
{
  // free memory of semaphore variables
  pthread_mutex_destroy(&(sem->mutex));
  pthread_cond_destroy(&(sem->condition));
  
  // free memory of semaphore
  free(sem);
}

void down(semaphore_t* sem)
{
  //pthread_cond_t* condition;

  // get a lock on semaphore
  pthread_mutex_lock(&(sem->mutex)); 
  
  // check count and block if 0, or if queue is not empty
  while(sem->count == 0)
  {
    /* Everytime a thread is about to wait, a condition is created
    and added to a queue tail. The tread will then wait on this condition.
    Since there is a 1:1 mapping between a thread and a condition, notifying
    on a condition that was added first to the queue will ensure that the
    thread being notified is the one waiting the longest. This ensures fairness */
    //condition = add_condition(sem);
    //printf("CONDITION WAIT: %d\n", condition);
   
    //printf("** pause\n");
    //pthread_cond_wait(condition,&(sem->mutex));
    pthread_cond_wait(&(sem->condition), &(sem->mutex));
    //printf("** resume\n");
  }
  // decrement count
  -- sem->count;
  
  // release lock on semaphore
  pthread_mutex_unlock(&(sem->mutex));

  // proceed with action
}

void up (semaphore_t* sem)
{
  //pthread_cond_t* condition;

  // get lock on semaphore
  pthread_mutex_lock(&(sem->mutex));

  // increment count
  ++ sem->count;

  // thread is waiting on condition
  pthread_cond_signal(&(sem->condition));

  // if (sem->count == 1/*!SIMPLEQ_EMPTY(&sem->head)*/)
  // {
  //   //printf("CONDITION SIGNAL: %d\n", condition);
  //   //condition = get_first_cond(sem);          // get condition
  //   //if (pthread_cond_signal(condition) != 0)
  //   //      printf("condition not initialized! WARNING!\n");;           // signal thread
  //   //pop_condition(sem);                       // remove condition from queue
  // }
  
  // relese lock on semaphore
  pthread_mutex_unlock(&(sem->mutex));
}
