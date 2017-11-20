/*
 * Joshua Truitt COSC 311
 * HW1115
 * Gets a number of dice, a threshold, and outputs the combinations of dice
 * that sum to give numbers of that threshold or higher.
 * https://github.com/jtruitt-student/homework/tree/master/Source/1115/
Program.java
 */
package hw115;

import java.util.*;

public class Program
{
    public static List<Integer> vals = new ArrayList<Integer>();
    
    public static int numDice, threshold, numGoodCombos;
    public static long calcTime, maxRunTime = 15 * 60 * 1000; // 15 min max.
    
    public static void main(String[] args)
    {
        Scanner in = new Scanner(System.in);
        
        boolean doAgain;
        do
        {
            numGoodCombos = 0;
            
            System.out.print("Please enter the number of dice: ");
            numDice = in.nextInt();
            System.out.print("Please enter the threshold: ");
            threshold = in.nextInt();
            
            System.out.println("d = " + numDice + ", n = " + threshold);
            calcTime = System.currentTimeMillis();
            roll(numDice, vals);
            calcTime = System.currentTimeMillis() - calcTime;
            System.out.println("It took " + (calcTime) + " milliseconds to"
                    + " compute that there are " + numGoodCombos 
                    + " combinations that sum to >= " + threshold);
            
            System.out.print("Do again? (y/*) ");
            String response = in.next().toLowerCase();
            
            doAgain = response.equals("y");
            System.out.println("\n");
        } while (doAgain);
    }
    
    // Adapted recursive backtracking approach, base method taken from:
    // https://courses.cs.washington.edu/courses/cse143/
    // Calculates all permutations.
    private static void roll(int numDice, List<Integer> vals) 
    {
        if (System.currentTimeMillis() - calcTime >= maxRunTime)
        {
            System.out.println("Calculation didn't finish. Calculation"
                    + " timed out.");
            return;
        }
        
        if (numDice == 0)
        {
            if (sumVals(vals) >= threshold)
            {
                numGoodCombos++;
                printVals(vals);
            }
        }
        else 
        {
            for (int i = 1; i < 7; i++) 
            {
                vals.add(i);                  
                roll(numDice - 1, vals);      
                vals.remove(vals.size() - 1);
            }
        }
    }
    
    private static int sumVals(List<Integer> vals)
    {
        int sum = 0;
        for (Integer i : vals)
            sum += i;
        
        return sum;
    }
    
    private static void printVals(List<Integer> vals)
    {
        for (int i = 0; i < vals.size(); i++)
        {
            System.out.print(vals.get(i) 
                    + ((i == vals.size() - 1) ? "" : ", "));
        }
        System.out.println();
    }
}
