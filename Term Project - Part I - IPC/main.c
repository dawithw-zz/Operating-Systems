#include <stdio.h>
#include <unistd.h>
#include <sys/types.h>

// function declarations
int send(int write_end);
int recieve(int read_end);

int main(int argc, char ** argv)
{
  int sender = 0;             // pid for sender
  int reciever = 0;           // pid for reciever
  int filedes[2];             // file descriptors
  int status = pipe(filedes); // create a pipe
  
  if (status == -1)           // handle pipe creation error
  { 
    fprintf(stderr,"Error creating pipe\n");
    return 0;
  }

  sender = fork(); // fork sender
  
  if (sender < 0)  // error forking
  {
    fprintf(stderr,"Error forking sender\n");
  }
  else if (sender == 0) // sender executes this code
  {
    close(filedes[0]);  // close read end
    send(filedes[1]);             
  }
  else  // parent executes this code
  {
    reciever = fork();   // fork reciever

    if (reciever < 0)    // error forking
    {
      fprintf(stderr, "Error forking reciever");
    }
    else if (reciever == 0) // reciever executes this code
    {
      close(filedes[1]);    // close write end
      recieve(filedes[0]);
    }
    else  // parent executes this code
    {
      close(filedes[0]);      // close reading end of pipe
      close(filedes[1]);      // close write end of pipe
      sleep(30);              // sleep 30 seconds 
    } 
  }

  // print process termination message
  printf("Your pal, Process_%d says, \"Auf Wiedersehenn.\n", getpid());
  
  return 0;
}

// Send message in reverse alphabetical order to pipe
int send(int write_end)
{
  char w = 'Z';
  size_t nbyte = sizeof(char);
  while(w >= 'A')
  {
    const void *buf = &w;
    write(write_end, buf, nbyte);
    --w;
    sleep(1);
  }
}

// Recieve message from pipe
int recieve(int read_end)
{
  char r;
  size_t nbyte = sizeof(char);
  while (r != 'A')
  {
    void *buf = &r;
    read(read_end, buf, nbyte); 
    write(1,buf,nbyte);
  }
  printf("\n");
}
