package project1;

public class Person 
{
    public static final int MAX_NAME_SIZE = 10;
    
    private static int currentId = 0;
    
    private char[] name = new char[MAX_NAME_SIZE];
    private int id;
    // Keep one reference so a new string isn't created every time getName is 
    // called.
    private String nameAsString = "";
    
    public Person()
    {
        id = -1;
    }
    
    public Person(String name)
    {
        // Accept only the first 10 letters of any name. If go outside range
        // of string, start adding padding for every character after that.
        for (int i = 0; i < MAX_NAME_SIZE; i++)
            this.name[i] = (i < name.length()) ? name.charAt(i) : ' ';
        
        nameAsString = new String(name);
            
        id = currentId;
        currentId++;
    }
    
    public static int getCurrentId()
    {
        return currentId;
    }
    
    static void setCurrentId(int value)
    {
        currentId = value;
    }
    
    public String getName()
    {
        return nameAsString;
    }
    
    public int getId()
    {   
        return id;
    }
}
