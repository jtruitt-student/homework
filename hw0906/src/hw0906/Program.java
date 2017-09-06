/*
    Joshua Truitt
    COSC 311
    Fall 2017
 */
package hw0906;

/**
 * Takes hardcoded data sets and inserts one into the other,
 * depending on an offset.
 */
public class Program 
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        int[] dataSet = { 1, 2, 3 };
        int[] dataSetToInsert = { 10, 11, 12, 13 };
        int offset = 0;
        
        for (int i = 0; i < 4; i++)
        {
            switch(i)
            {
                case 1:
                    offset = 1;
                    break;
                case 2:
                    offset = -2;
                    break;
                case 3: 
                    dataSet = new int[0];
        
                    dataSetToInsert = new int[3];
                    dataSetToInsert[0] = 10;
                    dataSetToInsert[1] = 11;
                    dataSetToInsert[2] = 12;

                    offset = 1;
                    break;
            }
            
            System.out.println("Data set: " + arrayToString(dataSet));
            System.out.println("Inserting: " + arrayToString(dataSetToInsert));
            System.out.println("Offset: " + offset);
            System.out.println("Result: " 
                + arrayToString(insert(dataSet, dataSetToInsert, offset)) 
                + "\n");
        }
        
        System.out.println("Program complete.");
        
    }
    
    public static int[] insert(int[] a, int[] b, int offset)
    {
        if (offset < 0 || offset > a.length - 1 || a.length == 0 
                || b.length == 0)
            return new int[0];
        
        int[] result = new int[a.length + b.length];
        int dataIndex = 0;
        
        for (int i = 0; i < result.length; i++)
        {
            if (i == offset)
            {
                for (int j = 0; j < b.length; j++)
                {
                    result[i] = b[j];
                    i++;
                }
            }
            
            result[i] = a[dataIndex];
            dataIndex++;
        }
        
        return result;
    }
    
    public static String arrayToString(int[] a)
    {
        String result = "{ ";
        
        for (int i = 0; i < a.length; i++)
            result += (i != a.length - 1) ? a[i] + ", " : a[i] + " ";
        
        return result + "}";
    }
}

/* OUTPUT

Data set: { 1, 2, 3 }
Inserting: { 10, 11, 12, 13 }
Offset: 0
Result: { 10, 11, 12, 13, 1, 2, 3 }

Data set: { 1, 2, 3 }
Inserting: { 10, 11, 12, 13 }
Offset: 1
Result: { 1, 10, 11, 12, 13, 2, 3 }

Data set: { 1, 2, 3 }
Inserting: { 10, 11, 12, 13 }
Offset: -2
Result: { }

Data set: { }
Inserting: { 10, 11, 12 }
Offset: 1
Result: { }

Program complete.

*/
