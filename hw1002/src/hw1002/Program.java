/*
 * Joshua Truitt
 * COSC 311
 * HW1002
 */
package hw1002;

import java.io.*;

public class Program 
{
    public static void main(String[] args) throws IOException
    {
        PrintWriter data = new PrintWriter("data.txt");
        
        long calcTime = 0;
        int numToOutput = 10;
        long longestCalcTimeInMillis = 300000;
        for (int i = 0; calcTime < longestCalcTimeInMillis; i++)
        {
            calcTime = System.currentTimeMillis();
            int result = fib(i);
            calcTime = System.currentTimeMillis() - calcTime;
            
            data.println(i + "," + calcTime);
            
            if (i >= 0 && i < numToOutput)
                System.out.println("fib(" + i + ") = " + result 
                                + "\nTime Elapsed: " + calcTime + "\n");
        }
        
        data.close();
    }
    
    public static int fib(int n)
    {
        if (n == 0)
            return 0;
        else if (n == 1)
            return 1;
        else
            return fib(n -1) + fib(n - 2);
    }
}
/*
OUTPUT

*/