/*
 * Joshua Truitt
 * COSC 311
 * HW1004
 * https://github.com/jtruitt-student/homework/tree/master/Source/1004/Program.java
 */
package hw1004;

import java.io.*;

public class Program 
{
    class FibResult
    {
        public int n;
        public int answer;
        
        public FibResult(int n, int answer)
        {
            this.n = n;
            this.answer = answer;
        }
    }
    
    //public static FibResult[] results;
    public static long[] results;
    
    public static void main(String[] args) throws IOException
    {
        int numFibsToDo = 500000;
        
        results = new long[numFibsToDo];
        // Initialize base cases
        results[0] = 0;
        results[1] = results[2] = 1;
        
        PrintWriter data = new PrintWriter("data.txt");
        
        long calcTime = 0;
        int numToOutput = 100000;
        for (int i = 0; i < numFibsToDo; i++)
        {
            calcTime = System.currentTimeMillis();
            long result = fib(i);
            calcTime = System.currentTimeMillis() - calcTime;
            
            data.println(i + "," + calcTime);
            
            if (i >= 0 && i < numToOutput)
                System.out.println("fib(" + i + ") = " + result 
                                + "\nTime Elapsed: " + calcTime + "\n");
        }
        
        data.close();
    }
    
    public static long fib(int n)
    {   
        if (results[n] != 0)
            return results[n];
        else if (n == 0)
            return 0;
        else if (n == 1)
            return 1;
        else
        {
            long result = fib(n - 1) + fib(n - 2);
            results[n] = result;
            return result;
        }
            
    }
}