package org.apache.coyote.http11.request;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class HttpPath {
    private String path;

    public HttpPath(final String path) {
        this.path = path;
    }

    public static HttpPath from(String path) {
        return new HttpPath(path);
    }

    public String getEndPoint() {
        return this.path.substring(this.path.lastIndexOf("/") + 1);
    }

    public Map<String, String> getQueryParameters() {
        if (!hasQueryParameter()) {
            return Map.of();
        }

        final String parameters = this.path.substring(this.path.lastIndexOf("?") + 1);
        return HttpParameterParser.parseParametersIntoMap(parameters);
    }

    public boolean hasQueryParameter() {
        return !Objects.equals(this.path.lastIndexOf("?"), -1);
    }

    public Optional<String> getQueryParameter(String parameter) {
        return Optional.ofNullable(this.getQueryParameters().get(parameter));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final HttpPath httpPath = (HttpPath) o;
        return Objects.equals(path, httpPath.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

    public String getFullUri() {
        return this.path;
    }
}
