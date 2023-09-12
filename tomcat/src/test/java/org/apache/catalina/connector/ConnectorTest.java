package org.apache.catalina.connector;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketImpl;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.coyote.RunnableProcessor;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import support.HttpClient;

class ConnectorTest {

    private static final Logger logger = LoggerFactory.getLogger(ConnectorTest.class);

    @Test
    void max_threads_검증() throws Exception {
        int requestsCount = 5;
        int acceptCount = 0;
        int maxThreadsCount = 2;
        Map<String, Integer> taskCountByThread = new ConcurrentHashMap<>();
        CountDownLatch latch = new CountDownLatch(requestsCount);

        Connector connector = getConnector(acceptCount, maxThreadsCount, taskCountByThread);
        for (int i = 0; i < requestsCount; i++) {
            new Thread(() -> {
                try {
                    HttpClient.send(8080, "/index.html", 150);
                } catch (Exception ignored) {
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        latch.await();

        assertAll(
            () -> assertEquals(maxThreadsCount, taskCountByThread.size()),
            () -> assertEquals(requestsCount, taskCountByThread.values().stream().mapToInt(Integer::intValue).sum())
        );

        connector.stop();
    }

    private static Connector getConnector(int acceptCount, int maxThreadsCount, Map<String, Integer> taskCountByThread) {
        Connector connector = new Connector(8080, acceptCount, maxThreadsCount,
            (socket, sem) -> new RunnableProcessor() {
                @Override
                public void process(Socket socket) {
                }

                @Override
                public void run() {
                    try {
                        taskCountByThread.merge(Thread.currentThread().getName(), 1, Integer::sum);
                        OutputStream outputStream = socket.getOutputStream();

                        outputStream.write(String.join("\r\n",
                            "HTTP/1.1 200 OK ",
                            "Content-Length: 12",
                            "Content-Type: text/html;charset=utf-8",
                            "",
                            "Hello world!").getBytes(StandardCharsets.UTF_8));
                        outputStream.flush();
                        outputStream.close();
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    } finally {
                        sem.release();
                    }
                }
            });

        connector.start();
        return connector;
    }
}
