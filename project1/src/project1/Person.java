package project1;

public class Person 
{
    public static final int MAX_NAME_SIZE = 10;
    
    // The next ID that will be applied to a created person.
    private static int currentId = 0;
    
    private char[] name = new char[MAX_NAME_SIZE];
    private int id;
    
    public Person()
    {
        for (int i = 0; i < name.length; i++)
            name[i] = ' ';
        id = -1;
    }
    
    public Person(String name)
    {
        setName(name);
            
        id = currentId;
        currentId++;
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
    
    @Override
    public int hashCode()
    {
//        int sum = 0;
//        for (char c : name)
//            sum += c;
//        
//        return sum % RecordManager.getMaxTableSize();
        // Get hash code based on name using java's hashCode method, 
        // as the project instructions specify.
        // Also, java produces negative numbers with default hashCode, so take
        // abs val.
        return Math.abs(getName().hashCode() % RecordManager.getMaxTableSize());
    }
    
    @Override
    public String toString()
    {
        return (new String(name)) + "\nID: " + id + "\nHash Code: " + hashCode();
    }
}
