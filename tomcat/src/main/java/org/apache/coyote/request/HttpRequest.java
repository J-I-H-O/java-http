package org.apache.coyote.request;

import java.util.Objects;
import org.apache.coyote.request.body.RequestBody;
import org.apache.coyote.request.header.RequestHeader;
import org.apache.coyote.request.line.HttpMethod;
import org.apache.coyote.request.line.RequestLine;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeader requestHeader;
    private RequestBody requestBody;

    private HttpRequest(RequestLine requestLine, RequestHeader requestHeader) {
        this(requestLine, requestHeader, null);
    }

    private HttpRequest(RequestLine requestLine, RequestHeader requestHeader, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
    }

    public static HttpRequest of(String requestLine, String requestHeader) {
        return new HttpRequest(
                RequestLine.from(requestLine),
                RequestHeader.from(requestHeader)
        );
    }

    public boolean hasBody() {
        return requestHeader.hasContent();
    }

    public boolean consistsOf(HttpMethod httpMethod) {
        return requestLine.consistsOf(httpMethod);
    }

    public boolean consistsOf(String requestUri) {
        return requestLine.consistsOf(requestUri);
    }

    public boolean hasQueryString() {
        return requestLine.hasQueryString();
    }

    public boolean hasSessionId() {
        return requestHeader.hasSessionId();
    }

    public String sessionId() {
        return requestHeader.sessionId();
    }

    public int contentLength() {
        return requestHeader.contentLength();
    }

    public String requestUri() {
        return requestLine.requestUri();
    }

    public String httpVersion() {
        return requestLine.httpVersion();
    }

    public RequestLine requestLine() {
        return requestLine;
    }

    public RequestHeader requestHeader() {
        return requestHeader;
    }

    public RequestBody requestBody() {
        return requestBody;
    }

    public String getBodyValue(String fieldName) {
        return requestBody.get(fieldName);
    }

    public String getQueryStringValue(String fieldName) {
        return requestLine.getQueryStringValue(fieldName);
    }

    public void setRequestBody(String requestBody) {
        if (requestBody.isBlank()) {
            return;
        }
        this.requestBody = RequestBody.from(requestBody);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HttpRequest that = (HttpRequest) o;
        return Objects.equals(requestLine, that.requestLine) && Objects.equals(requestHeader,
                that.requestHeader) && Objects.equals(requestBody, that.requestBody);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestLine, requestHeader, requestBody);
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "requestLine=" + requestLine +
                ", requestHeader=" + requestHeader +
                ", requestBody=" + requestBody +
                '}';
    }
}
