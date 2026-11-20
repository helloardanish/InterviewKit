import java.util.concurrent.ConcurrentHashMap;

public class HashCode {
    public static void main(String[] args) {
        ConcurrentHashMap<String, String> hashtable = new ConcurrentHashMap<>();
        hashtable.put("1", "Apple");
        hashtable.put("2", "Mango");
        hashtable.put("3", "Banana");
        hashtable.put("4", "Orange");

        hashtable.forEach((k,v) -> System.out.println(k.hashCode() + ", "+k + " : "+v));
    }
    
}
