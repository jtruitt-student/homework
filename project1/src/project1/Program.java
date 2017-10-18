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
        
        Person josh = new Person("Josh");
        Person roland = new Person("Roland");
        Person cyril = new Person("Cyril");
        
        RecordManager.test();
        
        //RecordManager.input("Cyril");
        
        RecordManager.test();
    }
}
