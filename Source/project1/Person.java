package project1;

// Represents a person, complete with all their data.
public class Person 
{
    // The maximum size a Person's name can be.
    public static final int MAX_NAME_SIZE = 10;
    
    // The next ID that will be applied to a created person.
    private static int currentId = 0;
    
    private char[] name = new char[MAX_NAME_SIZE];
    private int id;
    
    // Used to create a completely empty Person. ID is assigned -1.
    public Person()
    {
        for (int i = 0; i < name.length; i++)
            name[i] = ' ';
        id = -1;
    }
    
    // Most common way to create person. Assigns currentId and increments ID
    // counter.
    public Person(String name)
    {
        setName(name);
            
        id = currentId;
        currentId++;
    }
    
    // Useful for when the Person is being created just to have a name
    // and the ID doesn't matter.
    public Person (String name, boolean giveBadId)
    {
        setName(name);
        
        if (giveBadId)
            id = -2;
        else
        {
            id = currentId;
            currentId++;
        }
    }
    
    // To be used within this package as a means to create a person from
    // a record as it exists in the hash table.
    Person(String name, int id)
    {
        setName(name);
        this.id = id;
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
        return new String(name);
    }
    
    void setName(String name)
    {
        // Accept only the first 10 letters of any name. If go outside range
        // of string, start adding padding for every character after that.
        for (int i = 0; i < MAX_NAME_SIZE; i++)
            this.name[i] = (i < name.length()) ? name.charAt(i) : ' ';
    }
    
    public int getId()
    {   
        return id;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
    
    // Generates hash code for a person based on the person's name.
    @Override
    public int hashCode()
    {
        // Get hash code based on name using java's hashCode method, 
        // as the project instructions specify.
        // Also, java produces negative numbers with default hashCode, so take
        // abs val.
        return Math.abs(getName().hashCode() % RecordManager.getMaxTableSize());
    }
}
