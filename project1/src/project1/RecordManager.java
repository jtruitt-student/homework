package project1;

import java.io.*;
import java.nio.file.*;

// Handles Record manipulation and all operations on the hash table. All
// methods are static, no object of RecordManager should be created.
public class RecordManager 
{
    // Represents a Record, complete with all its data.
    private static class Record
    {
        // The size of a Record in bytes. Every byte is 25 bytes long because
        // person.name == 20, person.id == 4, meta == 1. Because of how the name
        // is read, a byte containing a terminating char for a string isn't
        // necessary.
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
        
        // Writes the record to the random access file, starting at the current
        // file pointer.
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
        
        // Returns whether meta == DATA_VALID.
        public boolean isValid()
        {
            return meta == DATA_VALID;
        }
        
        // Returns whether meta == DATA_EMPTY.
        public boolean isEmpty()
        {
            return meta == DATA_EMPTY;
        }
        
        // Returns whether meta == DATA_TOMBSTONE.
        public boolean hasTombstone()
        {
            return meta == DATA_TOMBSTONE;
        }
        
        // Gives the meta data as a bit string (i.e. 0b0000).
        public String metaAsBitString()
        {
            switch(meta)
            {
                case DATA_EMPTY:
                    return "0b0000";
                case DATA_TOMBSTONE:
                    return "0b0001";
                case DATA_VALID:
                    return "0b0100";
                case DATA_DIRTY:
                    return "0b0101";
                default:
                    return "INVALID META DATA";
            }
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
            return person.getName() + " " 
                    + ((person.getId() == -1) ? " " : person.getId())
                    + " " + metaAsBitString();
        }
        
        // Compares two Records, returning whether their names are equal.
        @Override
        public boolean equals(Object other)
        {
            // For our purposes, two Records are equal if the names of their 
            // people match.
            return person.getName().equals(((Record)other).person.getName());
        }
    }
    
    // The unchanging path to the file containing the hash table.
    public static final String TABLE_PATH = "table.dat";
     
    // Meta-data codes.
    public static final byte DATA_EMPTY = 0b0000,
                             DATA_TOMBSTONE = 0b0001,
                             DATA_VALID = 0b0100,
                             DATA_DIRTY = 0b0101;
    
    // The byte size of the table's header.
    public static final int HEADER_SIZE = 12;
    
    private static RandomAccessFile table; // Used frequently, ready ref.
    private static int maxTableSize = 16, numRecords = 0;
    
    // Initializes the hash table if it doesn't exist. If it does exist,
    // the header of the table is read to give the program information it needs
    // to run correctly by setting variables to their appropriate values.
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
    
    // Serves as an easy way for a user to input a record. Takes the name of the
    // person and whether you'd like to suppress the information the method
    // can give (i.e. "Input {name} - {id}").
    public static void input(String name, boolean suppressInform)
    { 
        try
        {
            RandomAccessFile table = new RandomAccessFile(TABLE_PATH, "rw");
            
            Person p = new Person(name);
            Record r = new Record(p);
            
            input(table, r, suppressInform);
            
            table.close();
        }
        catch(IOException e)
        {
            System.out.println("There was a problem when trying to input"
                            + " a new record: " + e.getMessage());
        }
    }
    
    // Helper method to the input method above. Accepts information for the
    // table being used, a reference to the record being deleted, and whether
    // information giving should be suppressed.
    private static void input(RandomAccessFile table, Record recordToInput,
            boolean suppressInform) throws IOException
    {
        // Inform user
        if (!suppressInform)
            System.out.println("Input \"" + recordToInput.person.getName() 
                    + "\" - " + recordToInput.person.getId());
                 
        
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
            System.out.println("Table is overcapacity (" + numRecords + "/" 
                    + maxTableSize + "). Rehashing.");

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
                    input(temp, r, true); // Input into new table (also increments
                                          // numRecords.)
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
        }
    }
    
    // Serves as an easy way for a user to delete a record. Takes 
    // the name of the person and whether you'd like to suppress the information 
    // the method can give (i.e. "Delete {name} - {id}").
    public static void delete(String name, boolean suppressInform)
    {
        try
        {
            RandomAccessFile table = new RandomAccessFile(TABLE_PATH, "rw");
            
            Person p = new Person(name, true); // Use const that doesn't alter
                                               // currentId. Name is all that
                                               // matters here.
            Record r = new Record(p);
            
            delete(table, r, suppressInform);
            
            table.close();
        }
        catch(IOException e)
        {
            System.out.println("There was a problem when trying to delete"
                            + " a record: " + e.getMessage());
        }
    }
    
    // Helper method to the delete method above. Accepts information for the
    // table being used, a reference to the record being deleted, and whether
    // information giving should be suppressed.
    private static void delete(RandomAccessFile table, Record recordToDelete,
            boolean suppressInform) throws IOException
    {
        // Inform user
        if (!suppressInform)
            System.out.println("Delete \"" + recordToDelete.person.getName() 
                    + "\"");
        
        int location = recordToDelete.person.hashCode();
        
        // Try to remove record at hashed location.
        if (!removeRecord(table, recordToDelete, location))
        {
            // Employ linear probe if the record doesn't match
            Record r;
            while(true)
            {
                // Increment to next location to see if it's suitable.
                location = (location + 1) % maxTableSize;
                r = getRecord(table, location);
                
                if (!r.isEmpty()) // Record's not empty...
                {
                    // ... try to remove.
                    if (removeRecord(table, recordToDelete, location))
                        break; // Leave loop if record matches and is removed.
                }
                else // Reached empty record...
                {
                    // ... probe ends, no such record exists, inform user.
                    throw new IOException("No record for \"" 
                        + recordToDelete.person.getName() + "\" exists.");
                }
            }
        }
    }
    
    // Prints the hash table and some information about it to standard
    // output.
    public static void printTable()
    {
        try
        {
            table = new RandomAccessFile(TABLE_PATH, "r");
            
            System.out.println("\nTABLE (Bytes: " + table.length()
                    + " | Records: " + ((table.length() - HEADER_SIZE) / Record.SIZE)
                    + " total, " + numRecords + " valid.)");
            
            Record r; // Holds ref to input record in loop.
            for (int i = 0; i < maxTableSize; i++)
            {
                r = getRecord(table, i);
                
                if (r != null)
                    System.out.println(i + ": " + r);
                else
                    throw new IOException("Record retrieval failed. Table may"
                            + " be corrupt.");
            }
            System.out.println(); // Good spacing.
            
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
        return (index * Record.SIZE) + HEADER_SIZE;
    }
    
    // Helper method that overwrites the record at the given index. 
    // If it can't be overwritten, returns false. Otherwise, returns true.
    // Used in conjunction with input method. Also updates header info.
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
    
    // Helper method that removes a record from the specified table by checking 
    // to see if the  record given matches the one at the provided index. If it
    // does, the record is marked as deleted and the method returns true. If not,
    // returns false. Also updates header info.
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
            
            // Inform user of successful deletion.
            System.out.println("Deleted \"" + r.person.getName() + "\" - "
                        + r.person.getId());
            
            return true;
        }
        else
            return false;
    }
    
    // Returns the Record at index in the provided hash table file, or null
    // if there was an issue obtaining the Record.
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
    
    // Makes a table with maxTableSize empty records in the specified file.
    private static void makeInitialTable(RandomAccessFile f) throws IOException
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
    
    // Reads a text file with commands relevant to hashing operations and
    // performs them on the table.
    public static void readCommandFile(BufferedReader f)
    {
        try
        {
            String line;
            while ((line = f.readLine()) != null)
            {
                String cmd = line.substring(0, line.indexOf('(')).toLowerCase();
                String arg;
                
                // Determine action to take based on cmd.
                switch (cmd)
                {
                    case "input":
                        arg = line.substring(line.indexOf('"') + 1,
                                line.lastIndexOf('"'));
                        
                        input(arg, false);
                        break;
                    case "delete":
                        arg = line.substring(line.indexOf('"') + 1,
                                line.lastIndexOf('"'));
                        
                        delete(arg, false);
                        break;
                    case "printtable":
                        printTable();
                        break;
                }
            }
        }
        catch(IOException e)
        {
            System.out.println("There was a problem when trying to read the "
                    + "command file: " + e.getMessage());
        }
        catch(NullPointerException e)
        {
            System.out.println("An unexpected null reference occurred when "
                    + "trying to read the command file.");
        }
    }
}
