package project1;

import java.io.*;

public class RecordManager 
{
    public static class Record
    {
        // Every Record is 26 bytes long.
        public static final int SIZE = 26;
        
        private Person person;
        private byte meta;
        
        public Record()
        {
            person = new Person();
            meta = DATA_EMPTY;
        }
        
        public Record(Person p)
        {
            person = p;
            meta = DATA_VALID;
        }
        
        public Record(Person p, byte meta)
        {
            person = p;
            this.meta = meta;
        }
        
        // Writes the record to the random access file.
        public void write(RandomAccessFile f)
        {
            try
            {
                f.writeChars(person.getName());
                f.writeInt(person.getId());
                f.writeByte(meta);
            }
            catch (IOException e)
            {
                System.out.println("There was a problem when writing the" 
                            + " record: " + e.getMessage());
            }
        }
        
        public boolean hasTombstone()
        {
            return meta == DATA_TOMBSTONE;
        }
        
        // Returns true if the meta data indicates that the record contains
        // a tombstone or if the data is empty.
        public boolean canBeOverwritten()
        {
            return meta == DATA_TOMBSTONE || meta == DATA_EMPTY;
        }
        
        @Override
        public String toString()
        {
            return "RECORD FOR: " + person.getName() + " (" + person.getId()
                   + ")\n" + "Meta-data: " + meta;
        }
    }
    
    public static final String TABLE_PATH = "table.dat";
     
    // Meta-data codes.
    public static final byte DATA_EMPTY = 0b0000,
                             DATA_TOMBSTONE = 0b0001,
                             DATA_VALID = 0b0100,
                             DATA_DIRTY = 0b0101;
    
    private static RandomAccessFile table;
    private static int tableSize = 16;
    
    public static void init()
    {
        try
        {
            // Used to test if the file exists
            File temp = new File(TABLE_PATH);
            
            if (temp.exists())
            {
                table = new RandomAccessFile(TABLE_PATH, "r");
                
                // Read in all values that need to be initialized.
                tableSize = table.readInt();
                Person.setCurrentId(table.readInt());
                
                table.close();
            }
            else // Make the initial hash table.
            {
                temp.createNewFile();
                
                // Format is: 4 bytes for tableSize, 4 bytes for currentId,
                // then (tableSize * Record.SIZE) bytes for records.
                table = new RandomAccessFile(TABLE_PATH, "rw");
                table.writeInt(tableSize);
                table.writeInt(Person.getCurrentId());
                
                // Initialize the table with records that can be overwritten.
                Record initRecord = new Record();
                for (int i = 0; i < tableSize; i++)
                    initRecord.write(table);
                
                System.out.println("File size after init: " + table.length());
                
                table.close();
            }
        }
        catch (IOException e)
        {
            System.out.println("There was a problem when reading/writing to"
                            + " the table: " + e.getMessage());
        }
    }
    
    // Takes a name of a new Persn and creates a Person with that name,
    // generates a record, hashes it, and puts it into the hash table.
    // Returns whether it succeeded.
    public static boolean input(String name)
    { 
        try
        {
            // Get the output ready.
            table = new RandomAccessFile(TABLE_PATH, "rw");
            System.out.println("Length of file: " + table.length());
            
            // Prepare the person, record, and hash.
            Person p = new Person(name);
            Record recordToInput = new Record(p);
            int location = p.hashCode();
            
            if (getRecord(table, location).canBeOverwritten())
            {
                // All's good, data can immediately be written to the table.
                table.seek(getRecordPosition(location)); // Move to record pos.
                recordToInput.write(table); // Write to table.
            }
            else
            {
                // Have to find appropriate place for new record.
            }
            
            table.close();
        }
        catch (IOException e)
        {
            System.out.println("There was a problem when trying to input"
                            + " a new record: " + e.getMessage());
        }
        
        
        
        return false;
    }
    
    // Helper method that takes a Record's index and converts that into
    // an offset that can be used with a file.
    private static int getRecordPosition(int index)
    {
        return (index * Record.SIZE) + 8;
    }
    
    // Returns the Record at index in the hash table file.
    private static Record getRecord(RandomAccessFile f, int index)
    {
        try
        {
            f.seek(getRecordPosition(index));
            
            String name = "";
            // For some reason, RandomAccessFile.readLine doesn't actually stop
            // reading chars when met with the terminating chars it says it
            // recognizes, so we'll just read the characters one by one
            // manually.
            for (int i = 0; i < Person.MAX_NAME_SIZE; i++)
                name += f.readChar();
            
            int id = f.readInt();
            byte meta = f.readByte();
            
            return new Record(new Person(name, id), meta);
        }
        catch (IOException e)
        {
            System.out.println("There was a problem when trying to retrieve"
                            + " a record: " + e.getMessage());
            
            return null;
        }
    }
    
    public static int getTableSize()
    {
        return tableSize;
    }
    
    public static void test()
    {
        try
        {
            table = new RandomAccessFile(TABLE_PATH, "r");
            System.out.println(getRecord(table, 3));
        }
        catch (Exception e)
        {
            System.exit(0);
        }
    }
}
