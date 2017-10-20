/*
 * Joshua Truitt
 * COSC 311 Fall 2017
 * Project 1: Hashing.
 */
package project1;

public class Program 
{
    // Initializes the program and calls any other inits that are necessary.
    public static void init()
    {
        RecordManager.init();
    }
    
    public static void main(String[] args)
    {
        init();

        System.out.println("Num records: " + RecordManager.getNumRecords());
        System.out.println("Max Table Size: " + RecordManager.getMaxTableSize());
        System.out.println("ID: " + Person.getCurrentId());
        
//        RecordManager.input("Josh");
//        RecordManager.input("Lilly");
//        RecordManager.input("Cyril");
//        RecordManager.delete("Cyril");
//        
//        RecordManager.printTable();
//        
//        RecordManager.input("Cyril");
//        
//        RecordManager.printTable();
        
        RecordManager.input("Cyril");
        RecordManager.input("Josh");
        RecordManager.input("Lilly");
        
        for (int i = 0; i < 6; i++)
            RecordManager.input("Test Person");
        
        RecordManager.printTable();
        
        //RecordManager.test();
        
        //RecordManager.input("Cyril");
        
        //RecordManager.test();
    }
}
