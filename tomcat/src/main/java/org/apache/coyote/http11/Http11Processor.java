package org.apache.coyote.http11;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.catalina.RequestMapping;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.HttpRequestReader;
import org.apache.coyote.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapping requestMapping;

    public Http11Processor(final Socket connection, RequestMapping requestMapping) {
        this.connection = connection;
        this.requestMapping = requestMapping;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (
                final var reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), UTF_8));
                final var outputStream = connection.getOutputStream()
        ) {
            HttpRequest httpRequest = readHttpRequest(reader);
            HttpResponse httpResponse = new HttpResponse(httpRequest.httpVersion());

            requestMapping.process(httpRequest, httpResponse);

            outputStream.write(httpResponse.responseMessage().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest readHttpRequest(BufferedReader reader) throws IOException {
        HttpRequestReader httpRequestReader = new HttpRequestReader();
        String requestLine = httpRequestReader.readLine(reader);
        String requestHeader = httpRequestReader.readHeader(reader);
        HttpRequest request = HttpRequest.of(requestLine, requestHeader);

        request.setRequestBody(httpRequestReader.readBody(reader, request.contentLength()));
        return request;
    }
}
