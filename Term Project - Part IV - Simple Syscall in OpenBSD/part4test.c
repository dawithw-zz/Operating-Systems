/*========================================================================**
**  Demonstrates Dawit's example system calls in action                    **
**========================================================================*/

#include <sys/syscall.h>

char *testCases[9];


int test(int id, char *type, int lval, int nval);



int main()
{
  // Create test strings
  char upper[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  char lower[] = "abcdefghijklmnopqrstuvwxyz";
  char digits[] = "0123456789";
  char specials[] = "! @ # $ ^ & * ( ) _ + < > ? : { }";
  char emtpy[] = "";
  char mixed_upper_lower[] = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz";
  char alphanum[] = "A1b2C3d4E5 consits of 10 chars";
  char mixed[] = " ~~ Th1s 15 a b@d <P@$sw0rd> format; Pl3a$e, st0p us1ng th3s3 k1nd 0f tricks!? #security";
  char overflow[2002]; 

  int i;                // iterator
  
  // Create a random string of size 2000
  for(i = 0; i < 2000; ++i){
    overflow[i] = 'a' + (i%25);
  }
  overflow[i] = '\0';

  // instantiate test cases
  testCases[0] = upper;
  testCases[1] = lower;
  testCases[2] = digits;
  testCases[3] = specials;
  testCases[4] = emtpy;
  testCases[5] = mixed_upper_lower;
  testCases[6] = alphanum;
  testCases[7] = mixed;
  testCases[8] = overflow;

  printf("\n---------\nBEGIN TESTING\n---------\n");

  test(1, "Encrypt", 1, 1);         // basic test
  test(1, "Decrypt", -1, -1);

  test(2, "Encrypt", 0, 0);         // no shift
  test(2, "Decrypt", 0, 0);     

  test(3, "Encrypt", 2, 5);         // different vals
  test(3, "Decrypt", -2, -5); 
  
  test(4, "Encrypt", 1000, 1000);   // high vals
  test(4, "Decrypt", -1000, -1000);

  test(5, "Encrypt", -3, -10);      // random negatives
  test(5, "Decrypt", 3, 10);

  printf("\n---------\nEND TESTING\n---------\n");

  return (0);
}

int test(int id, char *type, int lval, int nval)
{
  int size;             // size of string
  int i;

  printf("\n************\nPHASE %d (%s)\nlval: %d   |  nval: %d\n************\n", id, type, lval, nval);
  for(i = 0; i < 9; ++i)
  {
    printf("\nString %d: %s\nRunning through ciper...  ", i+1, testCases[i]);
    size = syscall( SYS_cipher, testCases[i], lval, nval);
    printf("Complete!\nString %d: %s\tSize: %d\n", i+1, testCases[i], size);
  }
}
