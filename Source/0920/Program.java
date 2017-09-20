/*
 * Joshua Truitt
 * COSC 311 Fall 2017
 * github.com/jtruitt-student/homework/tree/master/Source/0920/Program.java
 */
package hw0920;

import java.io.*;

public class Program 
{
    public static void main(String[] args) throws IOException
    {
        RandomAccessFile dataFile = new RandomAccessFile("data.raf", "r");
        int numInts = (int)dataFile.length() / 4;
        
        System.out.println("The numbers from data.raf in reverse order are:");
        
        for (int i = numInts - 1; i >= 0; i--)
        {
            dataFile.seek(i * 4);
            System.out.println(dataFile.readInt() + " ");
        }
    }
    
}
/*
OUTPUT:
The numbers from data.raf in reverse order are:
-46 
-17 
-44 
-4 
-35 
-7 
28 
-10 
-26 
-47 
-26 
36 
38 
-22 
-34 
38 
47 
-32 
-44 
49 
-38 
-36 
-44 
32 
-17 
29 
15 
-20 
4 
43 
40 
-10 
*/