package QuadraticProbingHashTable;
import java.util.*;

/**
 * Bowen Kruse
 * 5/25/2019
 * Program to Implement a HashTable with quadratic
 * probing for collision solution.
 * Simplified version of Huffman Tree example
 * from "Algorithms, 4th Edition by Robert
 * Sedgewick and Kevin Wayne"
 */

public class QuadHashTable<Key, Value> {
    private static final int InitialCapacity = 3;

    private int N;           // number of key-value pairs in the symbol table
    private int M;           // size of table
    private Key[] keys;      // the keys
    private Value[] vals;    // the values


    // create an empty hash table - use 16 as default size
    public QuadHashTable() {
        this(InitialCapacity);
    }

    // create linear proving hash table of given capacity
    public QuadHashTable(int capacity) {
        M = capacity;
        N = 0;
        keys = (Key[])   new Object[M];
        vals = (Value[]) new Object[M];
    }

    // return the number of key-value pairs in the symbol table
    public int size() {
        return N;
    }

    // is the symbol table empty?
    public boolean isEmpty() {
        return size() == 0;
    }

    // does a key-value pair with the given key exist in the symbol table?
    public boolean contains(Key key) {
        return get(key) != null;
    }

    // hash function for keys - returns value between 0 and M-1
    private int hash(Key key) {
        return (key.hashCode() & 0x7fffffff) % M;
    }

    // resize the hash table to the given capacity by re-hashing all of the keys
    private void resize(int capacity) {
        QuadHashTable<Key, Value> temp = new QuadHashTable<>(capacity);
        for (int i = 0; i < M; i++) {
            if (keys[i] != null) {
                temp.put(keys[i], vals[i]);
            }
        }
        keys = temp.keys;
        vals = temp.vals;
        M    = temp.M;
    }

    // insert the key-value pair into the symbol table
    public void put(Key key, Value val) {
        if (val == null) delete(key);

        // double table size if 80% full
        if (N >= M/8) resize(2*M);

        int i;
        int h = 1;
        for (i = hash(key); keys[i] != null; i = (i + h * h++) % M) {       //quadratic probing
            if (keys[i].equals(key)) { vals[i] = val; return; }
        }
        keys[i] = key;
        vals[i] = val;
        N++;
    }

    // return the value associated with the given key, null if no such value
    public Value get(Key key) {
        int h = 1;
        for (int i = hash(key); keys[i] != null; i = (i + h * h++) % M)     //quadratic probing
            if (keys[i].equals(key))
                return vals[i];
        return null;
    }

    // delete the key (and associated value) from the symbol table
    public void delete(Key key) {
        if (!contains(key)) return;

        // find position i of key
        int i = hash(key);
        while (!key.equals(keys[i])) {
            i = (i + 1) % M;
        }

        // delete key and associated value
        keys[i] = null;
        vals[i] = null;

        // rehash all keys in same cluster
        i = (i + 1) % M;
        while (keys[i] != null) {
            int h = 1;
            // delete keys[i] an vals[i] and reinsert
            Key   keyToRehash = keys[i];
            Value valToRehash = vals[i];
            keys[i] = null;
            vals[i] = null;
            N--;
            put(keyToRehash, valToRehash);
            i = (i + h * h++) % M;
        }

        N--;

        // halves size of array if it's 12.5% full or less
        if (N > 0 && N <= M/8) resize(M/2);

        assert check();
    }

    // return all of the keys as in Iterable
    public Iterable<Key> keys() {
        Queue<Key> queue = new LinkedList<Key>();
        for (int i = 0; i < M; i++)
            if (keys[i] != null) queue.add(keys[i]);
        return queue;
    }

    // integrity check - don't check after each put() because
    // integrity not maintained during a delete()
    private boolean check() {

        // check that hash table is at most 50% full
        if (M < 2*N) {
            System.err.println("Hash table size M = " + M + "; array size N = " + N);
            return false;
        }

        // check that each key in table can be found by get()
        for (int i = 0; i < M; i++) {
            if (keys[i] == null) continue;
            else if (get(keys[i]) != vals[i]) {
                System.err.println("get[" + keys[i] + "] = " + get(keys[i]) + "; vals[i] = " + vals[i]);
                return false;
            }
        }
        return true;
    }

    public void printTable() {
        System.out.println("Current Hash Table:");
        System.out.println("----------------------");
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] != null && vals[i] != null)
                System.out.println(keys[i]+" " + vals[i]);
        }
        System.out.println("----------------------");
    }

    public static void main(String[] args) {
        QuadHashTable<String, Integer> HT = new QuadHashTable<>();
        Scanner scan = new Scanner(System.in);

        System.out.println("Hash Table Test");
        char ch;
        /**  Perform QuadraticProbingHashTable operations  **/
        do
        {
            System.out.println("\nHash Table Operations:\n");
            System.out.println("1. Insert ");
            System.out.println("2. Delete");
            System.out.println("3. Search");
            System.out.println("4. Print current size");

            int choice = scan.nextInt();
            switch (choice)
            {
                case 1 :
                    System.out.println("Enter key (String) and value (int)");
                    HT.put(scan.next(), scan.nextInt() );
                    break;
                case 2 :
                    System.out.println("Enter key:");
                    HT.delete(scan.next());
                    break;
                case 3 :
                    System.out.println("Enter key:");
                    System.out.println("Value: "+ HT.get(scan.next()));

                    break;
                case 4 :
                    System.out.println("Current Table Size: "+ HT.size()+"\n");
                    break;
                default :
                    System.out.println("Wrong Entry \n ");
                    break;
            }
            /** Display hash table **/
            HT.printTable();

            System.out.println("\nDo you want to continue (Type y or n) \n");
            ch = scan.next().charAt(0);
        } while (ch == 'Y'|| ch == 'y');
        System.out.println("<END>");
    }
}
