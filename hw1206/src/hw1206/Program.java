/*
 * This program implements an O(n) algorithm to search
 * through a sorted 2D array of values.
 * Joshua Truitt
 * COSC 311
 * HW 1206

 * https://github.com/jtruitt-student/homework/tree/master/Source/1206/
   Program.java
 */
package hw1206;

public class Program
{
    public static void main(String[] args)
    {
        int[][] arr1 = new int[][]{
           { 10, 20, 20, 30 },
           { 11, 22, 23, 31 },
           { 12, 30, 30, 50 },
           { 12, 31, 32, 60 }  
        };
        
        int[][] arr2 = new int[][]{ 
            { 1,   2,  3,  3,   4,   5 },
            { 2,   4,  8, 12,  13,  31 },
            { 3,   4, 12, 30,  30,  50 },
            { 12, 31, 32, 60,  90,  92 }, 
            { 12, 31, 32, 90, 100, 120 },
            { 31, 31, 90, 90, 120, 120 } 
        };
        
        System.out.println("Run on first problem set (find element 24): " 
                + search(arr1, 24));
        System.out.println("Run on second problem set (find element 30): " 
                + search(arr1, 30));
        System.out.println("Run on third problem set (find element 12): " 
                + search(arr2, 12));
    } 
    
    // O(n) because never more than 2n - 1 compares
    public static boolean search(int[][] arr, int target)
    {
        int col = arr.length - 1;
        for (int i = 0; i < arr.length; i++)
        {
            for (int j = col; j >= 0; j--, col--)
            {
                if (target == arr[i][j])
                    return true;
                else if (target > arr[i][j])
                    break; // go down
                // else go left
            }
        }       
        return false;
    }
}
 
/* OUTPUT
    Run on first problem set (find element 24): false
    Run on second problem set (find element 30): true
    Run on third problem set (find element 12): true
*/