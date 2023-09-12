package org.apache.coyote.request.header;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class RequestHeader {

    private static final String DELIMITER = ":";
    private static final int HEADER_FIELD_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;
    private static final int LIMIT = 2;
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String JSESSIONID = "JSESSIONID";
    private static final String COOKIE = "Cookie";
    private static final int EMPTY_CONTENT_LENGTH = 0;

    private final Map<String, String> headersMap;
    private final HttpCookie httpCookie;

    private RequestHeader(Map<String, String> headersMap) {
        this(headersMap, new HttpCookie());
    }

    public RequestHeader(Map<String, String> headersMap, HttpCookie httpCookie) {
        this.headersMap = headersMap;
        this.httpCookie = httpCookie;
    }

    public static RequestHeader from(String headers) {
        Map<String, String> headersMap = Arrays.stream(headers.split(System.lineSeparator()))
                .map(headerLine -> headerLine.split(DELIMITER, LIMIT))
                .collect(Collectors.toMap(
                        fieldAndValue -> fieldAndValue[HEADER_FIELD_INDEX].trim(),
                        fieldAndValue -> fieldAndValue[HEADER_VALUE_INDEX].trim()
                ));

        if (headersMap.containsKey(COOKIE)) {
            return new RequestHeader(headersMap, HttpCookie.from(headersMap.get(COOKIE)));
        }
        return new RequestHeader(headersMap);
    }

    public boolean hasContent() {
        return contentLength() > EMPTY_CONTENT_LENGTH;
    }

    public int contentLength() {
        String contentLength = Objects.requireNonNullElse(
                headersMap.get(CONTENT_LENGTH),
                String.valueOf(EMPTY_CONTENT_LENGTH)
        );
        return Integer.parseInt(contentLength);
    }

    public boolean hasSessionId() {
        if (httpCookie != null) {
            return httpCookie.contains(JSESSIONID);
        }
        return false;
    }

    public String sessionId() {
        return httpCookie.get(JSESSIONID);
    }

    public String get(String key) {
        return headersMap.get(key);
    }

    public Map<String, String> headersMap() {
        return new HashMap<>(headersMap);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestHeader that = (RequestHeader) o;
        return Objects.equals(headersMap, that.headersMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(headersMap);
    }

    @Override
    public String toString() {
        return "RequestHeader{" +
                "headersMap=" + headersMap +
                '}';
    }
}
