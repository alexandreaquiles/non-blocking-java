import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class BlockingFileReader {
  public static void main(String[] args) {
    String fileName = "data.txt";

    try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
      String line;
      while ((line = reader.readLine()) != null) {
        System.out.println(line);
      }
      System.out.println("File reading completed");
    } catch (IOException e) {
      System.err.println("An error occurred: " + e.getMessage());
    }
  }
}
