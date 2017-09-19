/*
 * Joshua Truitt
 * COSC 311 Fall 2017
 * HW 0913

 * Performs a selection sort on a singly linked list.
 */
package hw0913;

public class Program 
{
    public static class LinkedList
    {
        Node head = null;
        
        private class Node
        {
            public int value;
            public Node next;
            
            public Node(int value)
            {
                this.value = value;
                next = null;
            }
            
            public Node(int value, Node next)
            {
                this.value = value;
                this.next = next;
            }
        }
        
        public LinkedList()
        {
            head = null;
        }
        
        public LinkedList(int[] values)
        {
            for (int val : values)
                append(val);
        }
        
        public void append(int value)
        {
            if (head == null)
                head = new Node(value, null);
            else
            {
                Node n = head;
                Node previous = head;
                while (n != null)
                {
                    n = n.next;
                    
                    if (n == null)
                        previous.next = new Node(value, null);
                                
                     previous = n;
                }
            }
        }
        
        public void prepend(int value)
        {
            if (head == null)
                head = new Node(value, null);
            else
            {
                Node n = new Node(value, head);
                head = n;
            }
        }
        
        public Node findAndRemoveMax()
        {
            if (head == null)
                return null;
            
            if (head.next == null)
            {
                Node returnNode = head;
                head = null;
                return returnNode;
            }
            
            Node max = head;
            Node n = head.next;
            
            while(n != null) // find
            {
                max = (n.value > max.value) ? n : max;
                
                n = n.next;
            }
            
            n = head;
            Node previous = null;
            while(n != null) // remove
            {
                if (n == max && previous != null)
                    previous.next = n.next;
                else if (n == max && n == head)
                    head = head.next;
                
                previous = n;
                n = n.next;
            }
            
            return max;
        }
        
        public void print()
        {
            Node n = head;
            
            System.out.print("{ ");
            while(n != null)
            {
                if (n == head)
                    System.out.print(n.value);
                else
                    System.out.print(", " + n.value);
                
                n = n.next;
            }
            
            System.out.println(" }");
        }
    }
    
    public static void main(String[] args) 
    {
        LinkedList unsorted = new LinkedList(new int[] { 5, 3, 1, 2, 7 });
        LinkedList sorted = new LinkedList();
        
        System.out.println("Data set to sort:");
        unsorted.print();
        
        while(unsorted.head != null)
            sorted.prepend((unsorted.findAndRemoveMax()).value);
        
        System.out.println("\nSorted data set:");
        sorted.print();
    }
}

/* OUTPUT
    Data set to sort:
    { 5, 3, 1, 2, 7 }

    Sorted data set:
    { 1, 2, 3, 5, 7 }
*/


