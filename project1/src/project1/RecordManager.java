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
                
                f.close();
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
    }
    
    public static final String TABLE_PATH = "data\\table.dat";
     
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
                table = new RandomAccessFile(temp, "r");
                
                // Read in all values that need to be initialized.
                tableSize = table.readInt();
                Person.setCurrentId(table.readInt());
                
                table.close();
            }
            else // Make the initial hash table.
            {
                temp.createNewFile();
                
                // Format is: 4 bytes for tableSize, 4 bytes for currentId,
                // then (tableSize * 26) bytes for records.
                table = new RandomAccessFile(TABLE_PATH, "rw");
                table.writeInt(tableSize);
                table.writeInt(Person.getCurrentId());
                
                // Initialize the table with records that can be overwritten.
                Record initRecord = new Record();
                for (int i = 0; i < tableSize; i++)
                    initRecord.write(table);
                
                table.close();
            }
        }
        catch (IOException e)
        {
            System.out.println("There was a problem when reading/writing to"
                            + " the table: " + e.getMessage());
        }
    }
    
    // Returns wheher succeeded.
    public static boolean hashRecord(Person p)
    {
        return false;
    }
}
