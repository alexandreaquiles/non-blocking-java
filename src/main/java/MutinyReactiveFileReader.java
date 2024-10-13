import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;

import java.util.concurrent.CompletableFuture;

public class MutinyReactiveFileReader {
  public static void main(String[] args) {
    CompletableFuture<Void> future = new CompletableFuture<>();

    Vertx vertx = Vertx.vertx();

    Uni<Buffer> fileContent = vertx.fileSystem().readFile("data.txt");
    fileContent.toMulti().subscribe()
            .with(buffer -> System.out.print(buffer.toString()),
                    throwable -> { System.err.println(throwable);
                      closeVertx(vertx, future); },
                    () -> { System.out.println("File reading completed.");
                      closeVertx(vertx, future);
                    });

    future.join();
  }

  private static void closeVertx(Vertx vertx, CompletableFuture<Void> future) {
    vertx.close().subscribe()
            .with(
                    (Void v) -> future.complete(null),
                    (Throwable t) -> System.err.println(t.getMessage())
            );
  }
}