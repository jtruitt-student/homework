/*
 * Joshua Truitt
 * COSC 311 Fall 2017
 * Project 1: Hashing.
 */
package project1;

import java.io.*;

public class Program 
{
    // Initializes the program and calls any other inits that are necessary.
    static void init()
    {
        RecordManager.init();
    }
    
    public static void main(String[] args)
    {
        init();
        
        try
        {
            // Set up file reader.
            BufferedReader bf = new BufferedReader(new FileReader("input.dat"));
        
            // Read in and perform all table operations from text file.
            RecordManager.readCommandFile(bf);
        }
        catch (IOException e)
        {
            System.out.println("There was a problem when trying to set up "
                    + "the buffer to read the input.dat file: " 
                    + e.getMessage());
        }
    }
}
/* OUTPUT
Input "cervantes " - 0
Input "proust    " - 1
Input "joyce     " - 2
Input "shakespear" - 3
Input "homer     " - 4
Input "tolstoy   " - 5
Input "melville  " - 6
Input "dante     " - 7
Table is overcapacity (8/16). Rehashing.
Input "twain     " - 8
Input "dostoyevsk" - 9
Input "flaubert  " - 10
Delete "cervantes "
Deleted "cervantes " - 0

TABLE (Bytes: 812 | Records: 32 total, 10 valid.)
0: flaubert   10 0b0100
1:              0b0000
2: dante      7 0b0100
3:              0b0000
4: tolstoy    5 0b0100
5:              0b0000
6:              0b0000
7:              0b0000
8:              0b0000
9:              0b0000
10:              0b0000
11:              0b0000
12:              0b0000
13: homer      4 0b0100
14:              0b0000
15:              0b0000
16:              0b0000
17: #            0b0001
18:              0b0000
19: shakespear 3 0b0100
20:              0b0000
21:              0b0000
22: joyce      2 0b0100
23: proust     1 0b0100
24:              0b0000
25:              0b0000
26:              0b0000
27:              0b0000
28:              0b0000
29: twain      8 0b0100
30: melville   6 0b0100
31: dostoyevsk 9 0b0100

Delete "cervantes "
There was a problem when trying to delete a record: No record for "cervantes " exists.

TABLE (Bytes: 812 | Records: 32 total, 10 valid.)
0: flaubert   10 0b0100
1:              0b0000
2: dante      7 0b0100
3:              0b0000
4: tolstoy    5 0b0100
5:              0b0000
6:              0b0000
7:              0b0000
8:              0b0000
9:              0b0000
10:              0b0000
11:              0b0000
12:              0b0000
13: homer      4 0b0100
14:              0b0000
15:              0b0000
16:              0b0000
17: #            0b0001
18:              0b0000
19: shakespear 3 0b0100
20:              0b0000
21:              0b0000
22: joyce      2 0b0100
23: proust     1 0b0100
24:              0b0000
25:              0b0000
26:              0b0000
27:              0b0000
28:              0b0000
29: twain      8 0b0100
30: melville   6 0b0100
31: dostoyevsk 9 0b0100

Delete "flaubert  "
Deleted "flaubert  " - 10
Input "austen    " - 11
Delete "dostoyevsk"
Deleted "dostoyevsk" - 9
Input "nabokov   " - 12
Input "orwell    " - 13
Input "carroll   " - 14
Input "conrad    " - 15
Input "virgil    " - 16
Input "sophocles " - 17
Delete "proust    "
Deleted "proust    " - 1

TABLE (Bytes: 812 | Records: 32 total, 14 valid.)
0: #            0b0001
1:              0b0000
2: dante      7 0b0100
3: austen     11 0b0100
4: tolstoy    5 0b0100
5:              0b0000
6:              0b0000
7:              0b0000
8:              0b0000
9:              0b0000
10:              0b0000
11:              0b0000
12:              0b0000
13: homer      4 0b0100
14: conrad     15 0b0100
15: orwell     13 0b0100
16: carroll    14 0b0100
17: #            0b0001
18: nabokov    12 0b0100
19: shakespear 3 0b0100
20: sophocles  17 0b0100
21: virgil     16 0b0100
22: joyce      2 0b0100
23: #            0b0001
24:              0b0000
25:              0b0000
26:              0b0000
27:              0b0000
28:              0b0000
29: twain      8 0b0100
30: melville   6 0b0100
31: #            0b0001

*/
