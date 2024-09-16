package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpHeader;

public class HttpResponse {

    private static final String DEFAULT_RESPONSE_BODY = "";
    public static final String FILE_EXTENSION_DELIMITER = ".";

    private Map<HttpHeader, String> headers = new LinkedHashMap<>();
    private HttpStatus status = HttpStatus.OK;
    private String body = DEFAULT_RESPONSE_BODY;

    public void addHeader(HttpHeader key, String value) {
        headers.put(key, value);
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void setBody(String body) {
        this.body = body;
        this.headers.put(HttpHeader.CONTENT_TYPE, "text/plain;charset=utf-8");
        this.headers.put(HttpHeader.CONTENT_LENGTH, String.valueOf(body.getBytes().length));
    }

    public void setBodyWithStaticResource(String filePath) {
        try {
            URL resource = getClass().getClassLoader().getResource("static" + filePath);
            if (resource == null) {
                throw new IOException("존재하지 않는 파일입니다. : " + filePath);
            }

            byte[] fileContents = Files.readAllBytes(new File(resource.getFile()).toPath());
            String fileExtension = getFileExtension(filePath);

            this.body = new String(fileContents);
            this.headers.put(HttpHeader.CONTENT_TYPE, "text/" + fileExtension + ";charset=utf-8");
            this.headers.put(HttpHeader.CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        } catch (IOException e) {
            this.status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    private String getFileExtension(String filePath) {
        String fileExtension = "html";
        int fileExtensionIndex = filePath.lastIndexOf(FILE_EXTENSION_DELIMITER);
        if (fileExtensionIndex >= 0) {
            fileExtension = filePath.substring(fileExtensionIndex)
                    .replace(FILE_EXTENSION_DELIMITER, "");
        }
        return fileExtension;
    }

    @Override
    public String toString() {
        String statusLine = "HTTP/1.1 " + this.status + " ";
        StringBuilder headerBuilder = new StringBuilder();
        headers.entrySet()
                .stream()
                .forEach(entry -> headerBuilder.append(String.format("%s: %s \r\n", entry.getKey(), entry.getValue())));

        return String.join("\r\n", statusLine, headerBuilder, body);
    }
}
