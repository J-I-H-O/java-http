package org.apache.coyote.http11.request;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpRequestHeaders {

    private static final String HEADER_SEPARATOR = System.lineSeparator();
    private static final String HEADER_KEY_VALUE_SPLIT = ":";
    private static final String COOKIE = "Cookie";
    private static final String CONTENT_LENGTH = "Content-Length";

    private final Map<String, String> headers;
    private final Optional<Cookies> cookies;

    public HttpRequestHeaders(String headers) {
        Map<String, String> headerValues = extractHeader(headers);
        this.headers = headerValues;
        this.cookies = extractCookie(headerValues);
    }

    private Optional<Cookies> extractCookie(Map<String, String> headerValues) {
        String cookieHeader = headerValues.get(COOKIE);
        if (cookieHeader == null) {
            return Optional.empty();
        }
        return Optional.of(new Cookies(cookieHeader));
    }

    private Map<String, String> extractHeader(String headers) {
        return Stream.of(headers.split(HEADER_SEPARATOR))
            .collect(Collectors.toMap(
                this::keyOf,
                this::valueOf
            ));
    }

    private String keyOf(String header) {
        String[] split = header.split(HEADER_KEY_VALUE_SPLIT);
        return split[0].trim();
    }

    private String valueOf(String header) {
        String[] split = header.split(HEADER_KEY_VALUE_SPLIT);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i < split.length; i++) {
            stringBuilder.append(split[i]);
        }
        return stringBuilder.toString().trim();
    }

    public Optional<String> contentLength() {
        return Optional.ofNullable(headers.get(CONTENT_LENGTH));
    }

    public Optional<Cookies> getCookie() {
        return cookies;
    }
}
