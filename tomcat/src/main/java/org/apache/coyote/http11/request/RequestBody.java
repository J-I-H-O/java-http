package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.StringTokenizer;

public class RequestBody {
    public static final RequestBody EMPTY = new RequestBody(Collections.emptyMap());

    private final Map<String, String> content;

    private RequestBody(final Map<String, String> content) {
        this.content = content;
    }

    public static Optional<RequestBody> of(final RequestHeaders requestHeaders, final BufferedReader bufferedReader) throws IOException {
        if (!requestHeaders.containsKey("Content-Length")) {
            return Optional.empty();
        }
        final Map<String, String> result = new HashMap<>();
        final int contentLength = Integer.parseInt(requestHeaders.getHeaderValue("Content-Length"));
        if (contentLength > 0) {
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            String body = new String(buffer);

            final StringTokenizer token = new StringTokenizer(body, "&");
            while (token.hasMoreTokens()) {
                final String param = token.nextToken();
                final String[] split = param.split("=");
                result.put(split[0], split[1]);
            }
            return Optional.of(new RequestBody(result));
        }

        return Optional.empty();
    }

    public String getParamValue(final String key) {
        return content.get(key);
    }
}
