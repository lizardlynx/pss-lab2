import java.io.FileWriter; // Import the FileWriter class
import java.io.IOException; // Import the IOException class to handle errors
import java.util.concurrent.locks.ReentrantLock;

public class SyncWriterImpl {
    String name;
    FileWriter resultWriter;
    ReentrantLock lock = new ReentrantLock();
    Boolean append;

    public SyncWriterImpl(String resultWriterName) {
        this.name = resultWriterName;
        append = false;
        write("");
        append = true;
    }

    public void write(String text) {
        lock.lock();
        try {
            resultWriter = new FileWriter(name, append);
            resultWriter.write(text);
            System.out.println("Successfully wrote to the file.");
            resultWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } finally {
           lock.unlock();
        }
    }
    
}
