import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CountDownLatch;

public class AsyncFileReaderWithCallback {
  public static void main(String[] args) {
    Path path = Path.of("data.txt");
    CountDownLatch latch = new CountDownLatch(1);

    try (AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.READ)) {
      ByteBuffer buffer = ByteBuffer.allocate(1024);
      readChunk(fileChannel, buffer, 0, latch);

      latch.await();
    } catch (IOException | InterruptedException e) {
      System.err.println("An error occurred: " + e.getMessage());
    }

    System.out.println("File reading completed.");
  }

  private static void readChunk(AsynchronousFileChannel fileChannel, ByteBuffer buffer, long position, CountDownLatch latch) {
    fileChannel.read(buffer, position, buffer, new CompletionHandler<>() {
      @Override
      public void completed(Integer bytesRead, ByteBuffer attachment) {
        if (bytesRead == -1) {
          latch.countDown();
          return;
        }

        attachment.flip();
        byte[] data = new byte[attachment.limit()];
        attachment.get(data);
        System.out.print(new String(data));

        attachment.clear();
        readChunk(fileChannel, attachment, position + bytesRead, latch);
      }

      @Override
      public void failed(Throwable exc, ByteBuffer attachment) {
        System.err.println("An error occurred: " + exc.getMessage());
        latch.countDown();
      }
    });
  }
}
