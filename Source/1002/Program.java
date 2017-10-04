/*
 * Joshua Truitt
 * COSC 311
 * HW1002
 * https://github.com/jtruitt-student/homework/tree/master/Source/1002/Program.java
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
fib(0) = 0
Time Elapsed: 0

fib(1) = 1
Time Elapsed: 0

fib(2) = 1
Time Elapsed: 0

fib(3) = 2
Time Elapsed: 0

fib(4) = 3
Time Elapsed: 0

fib(5) = 5
Time Elapsed: 0

fib(6) = 8
Time Elapsed: 0

fib(7) = 13
Time Elapsed: 0

fib(8) = 21
Time Elapsed: 0

fib(9) = 34
Time Elapsed: 0
*/