package project1;

import java.io.*;
import java.nio.file.*;

public class RecordManager 
{
    public static class Record
    {
        // Every Record is 25 bytes long.
        public static final int SIZE = 25;
        
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
        
        public boolean isValid()
        {
            return meta == DATA_VALID;
        }
        
        public boolean hasTombstone()
        {
            return meta == DATA_TOMBSTONE;
        }
        
        // Marks the record as deleted and overwrites the name with a tombstone.
        public void delete()
        {
            meta = DATA_TOMBSTONE;
            person.setName("#");
            person.setId(-1);
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
        
        @Override
        public boolean equals(Object other)
        {
            // For our purposes, two Records are equal if the names of their 
            // people match.
            return person.getName().equals(((Record)other).person.getName());
        }
    }
    
    public static final String TABLE_PATH = "table.dat";
     
    // Meta-data codes.
    public static final byte DATA_EMPTY = 0b0000,
                             DATA_TOMBSTONE = 0b0001,
                             DATA_VALID = 0b0100,
                             DATA_DIRTY = 0b0101;
    
    private static RandomAccessFile table;
    private static int maxTableSize = 16, numRecords = 0;
    
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
                maxTableSize = table.readInt();
                numRecords = table.readInt();
                Person.setCurrentId(table.readInt());
                
                table.close();
            }
            else // Make the initial hash table.
            { 
                table = new RandomAccessFile(TABLE_PATH, "rw");
                makeInitialTable(table);

                table.close();
            }
        }
        catch (IOException e)
        {
            System.out.println("There was a problem when reading/writing to"
                            + " the table: " + e.getMessage());
        }
    }
    
    // Takes a name of a new Person and creates a Person with that name,
    // generates a record, hashes it, and puts it into the hash table.
    // Returns whether it succeeded.
    public static void input(String name)
    { 
        try
        {
            RandomAccessFile table = new RandomAccessFile(TABLE_PATH, "rw");
            
            Person p = new Person(name);
            Record r = new Record(p);
            
            input(table, r);
            
            table.close();
        }
        catch(IOException e)
        {
            System.out.println("There was a problem when trying to input"
                            + " a new record: " + e.getMessage());
        }
    }
    
    // The backbone of input funcionality, meant to be used only by
    // the RecordManager. Allows specification of table file and copying
    // of a record from one location to another.
    private static void input(RandomAccessFile table, Record recordToInput)
            throws IOException
    {
        // Prepare the hash.
        int location = recordToInput.person.hashCode();

        // If the record can't be overwritten, employ linear probe.
        if (!overwriteRecord(table, recordToInput, location))
        {
            int failSafeCounter = 0; // Counts to prevent endless loop.
            do // Loop until the record can be overwritten.
            {
                // Increment to next location to see if it's suitable.
                location = (location + 1) % maxTableSize;

                failSafeCounter++;
                // With rehashing, this is technically redundant and 
                // shouldn't be encountered, but it's
                // a good fail-safe against an infinite loop.
                if (failSafeCounter >= maxTableSize)
                    throw new IOException("There was no suitable space "
                            + "available in the hash table.");

            } while(!overwriteRecord(table, recordToInput, location));
        }

        // Check the load factor after an insert.
        if (numRecords >= (maxTableSize / 2))
        {
            // Rehash
            System.out.print("Table is overcapacity (" + numRecords + "/" 
                    + maxTableSize + "). Rehashing...");

            maxTableSize *= 2; // Double table size.
            
            // Create a temp file where we'll build the new table so we don't
            // lose data if the transfer process is interrupted.
            RandomAccessFile temp = new RandomAccessFile("temp.dat", "rw");
            
            // Prepare temp table.
            makeInitialTable(temp);

            // Go through all records in the original table and copy over
            // any holding valid data.
            Record r;
            for (int i = 0; i < (maxTableSize / 2); i++)
            {
                r = getRecord(table, i);
                if (r.isValid())
                {
                    numRecords--; // Stop record count from changing.
                    input(temp, r); // Input into new table (also increments
                                    // numRecords.
                }
            }
            
            // Close both files so that they can be moved/removed.
            temp.close();
            table.close();
            
            // Set up refs of type File to both Files.
            File tempFile = new File("temp.dat");
            File tableFile = new File(TABLE_PATH);
            
            // Copy the temp file to the main table's file location, overwriting
            // it.
            Files.copy(tempFile.toPath(), tableFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
            
            // Clean up.
            tempFile.delete();

            System.out.println("Done");
        }
    }
    
    public static void delete(String name)
    {
        try
        {
            RandomAccessFile table = new RandomAccessFile(TABLE_PATH, "rw");
            
            Person p = new Person(name);
            // Every time a person is instantiated, the current id is
            // incremented to make adding records easier. Have to create a 
            // new Person here for the delete method, but don't want the
            // currentId to increment. Undo incrementation here.
            Person.setCurrentId(Person.getCurrentId() - 1);
            Record r = new Record(p);
            
            delete(table, r);
            
            table.close();
        }
        catch(IOException e)
        {
            System.out.println("There was a problem when trying to delete"
                            + " a record: " + e.getMessage());
        }
    }
    
    private static void delete(RandomAccessFile table, Record recordToDelete)
            throws IOException
    {
        int location = recordToDelete.person.hashCode();
        
        // If the record can't be overwritten, employ linear probe.
        if (!removeRecord(table, recordToDelete, location))
        {
            int failSafeCounter = 0; // Counts to prevent endless loop.
            do // Loop until the record can be overwritten.
            {
                // Increment to next location to see if it's suitable.
                location = (location + 1) % maxTableSize;

                failSafeCounter++;
                // With rehashing, this is technically redundant and 
                // shouldn't be encountered, but it's
                // a good fail-safe against an infinite loop.
                if (failSafeCounter >= maxTableSize)
                    throw new IOException("There was no suitable record found "
                            + "that could be deleted.");

            } while(!removeRecord(table, recordToDelete, location));
        }
    }
    
    // Prints the hash table to standard output.
    public static void printTable()
    {
        try
        {
            table = new RandomAccessFile(TABLE_PATH, "r");
            
            Record r; // Holds ref to input record in loop.
            for (int i = 0; i < maxTableSize; i++)
            {
                r = getRecord(table, i);
                
                if (r != null)
                    System.out.println(i + ": " + r.person.getName() + " ("
                        + r.person.getId() + ")");
                else
                    throw new IOException("Record retrieval failed. Table may"
                            + " be corrupt.");
            }
            
            table.close();
        }
        catch (IOException e)
        {
            System.out.println("There was a problem reading the hash table"
                    + " when attempting to print it out: " + e.getMessage());
        }
    }
    
    // Helper method that takes a Record's index and converts that into
    // an offset that can be used with a file.
    private static int getRecordPosition(int index)
    {
        // + 12 because there are three ints saved in the header of the file,
        // each encoded in 4 bytes.
        return (index * Record.SIZE) + 12;
    }
    
    // Overwrites the record at the given index. If it can't be overwritten,
    // the method returns false. Otherwise, returns true.
    private static boolean overwriteRecord(RandomAccessFile f, 
            Record recordToInput, int index) throws IOException
    {
        if (getRecord(f, index).canBeOverwritten())
        {
            // All's good, data can immediately be written to the table.
            f.seek(getRecordPosition(index)); // Move to record pos.
            recordToInput.write(f); // Write to table.

            // Update the header data.
            numRecords++;
            f.seek(0);
            f.writeInt(maxTableSize);
            f.writeInt(numRecords);
            f.writeInt(Person.getCurrentId());
            
            return true;
        }
        else
            return false;
    }
    
    // Removes a record from the specified table by checking to see if the 
    // record given matches the one at the provided index. If it does, the
    // record is maarked as deleted and the method returns true. If not,
    //r eturns false.
    private static boolean removeRecord(RandomAccessFile f,
            Record recordToDelete, int index) throws IOException
    {
        Record r = getRecord(f, index);
        if (r.equals(recordToDelete))
        {
            recordToDelete.delete(); // Mark deleted.
            // Seek back to the start of record and overwrite with new data.
            f.seek(getRecordPosition(index));
            recordToDelete.write(f);
            
            // Update header data.
            numRecords--;
            f.seek(0);
            f.writeInt(maxTableSize);
            f.writeInt(numRecords);
            f.writeInt(Person.getCurrentId());
            
            return true;
        }
        else
            return false;
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
    
    private static void makeInitialTable(RandomAccessFile f)
            throws IOException
    {
        // Format is: 4 bytes for fSize, 4 bytes for currentId,
        // then (fSize * Record.SIZE) bytes for records.
        f.writeInt(maxTableSize);
        f.writeInt(numRecords);
        f.writeInt(Person.getCurrentId());

        // Initialize the f with records that can be overwritten.
        Record initRecord = new Record();
        for (int i = 0; i < maxTableSize; i++)
            initRecord.write(f);
    }
    
    public static int getNumRecords()
    {
        return numRecords;
    }
    
    public static int getMaxTableSize()
    {
        return maxTableSize;
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
