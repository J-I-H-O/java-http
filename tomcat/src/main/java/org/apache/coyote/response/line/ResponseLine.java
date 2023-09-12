package org.apache.coyote.response.line;

import static common.ResponseStatus.EMPTY_RESPONSE_STATUS;

import common.ResponseStatus;
import java.util.Objects;

public class ResponseLine {

    private static final String DELIMITER = " ";

    private final String httpVersion;
    private ResponseStatus responseStatus;

    public ResponseLine(String httpVersion, ResponseStatus responseStatus) {
        this.httpVersion = httpVersion;
        this.responseStatus = responseStatus;
    }

    public boolean hasEmptyResponseStatus() {
        return responseStatus == EMPTY_RESPONSE_STATUS;
    }

    public String responseLineMessage() {
        return String.join(
                DELIMITER,
                httpVersion,
                responseStatus.codeMessage(),
                responseStatus.responseMessage()
        );
    }

    public String htpVersion() {
        return httpVersion;
    }

    public ResponseStatus responseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ResponseLine that = (ResponseLine) o;
        return Objects.equals(httpVersion, that.httpVersion) && responseStatus == that.responseStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpVersion, responseStatus);
    }

    @Override
    public String toString() {
        return "ResponseLine{" +
                "htpVersion='" + httpVersion + '\'' +
                ", responseStatus=" + responseStatus +
                '}';
    }
}
