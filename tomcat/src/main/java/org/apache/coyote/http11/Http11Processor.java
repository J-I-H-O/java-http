package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.HandlerMapper;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final ResourceProvider resourceProvider;
    private final HandlerMapper handlerMapper;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.resourceProvider = new ResourceProvider();
        this.handlerMapper = new HandlerMapper();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            final var outputStream = connection.getOutputStream()) {
            HttpRequest httpRequest = HttpRequest.makeRequest(inputReader);
            String response = getResponse(httpRequest, new HttpResponse());
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponse(HttpRequest httpRequest, HttpResponse httpResponse) {
        String path = httpRequest.getRequestLine().getPath();
        if (resourceProvider.haveResource(path)) {
            return resourceProvider.staticResourceResponse(path);
        }
        if (handlerMapper.haveAvailableHandler(httpRequest)) {
            handlerMapper.process(httpRequest, httpResponse);
            return HttpResponseConverter.convert(httpResponse);
        }
        return String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: 12 ",
            "",
            "Hello world!");
    }
}
