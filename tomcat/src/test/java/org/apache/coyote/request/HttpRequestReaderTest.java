package org.apache.coyote.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpRequestReaderTest {

    private HttpRequestReader httpRequestReader;

    @BeforeEach
    void setUp() {
        httpRequestReader = new HttpRequestReader();
    }

    @Test
    void 줄을_읽는다() {
        // given
        String line = "GET /index.html HTTP/1.1 ";
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        new ByteArrayInputStream(line.getBytes()), StandardCharsets.UTF_8
                ))
        ) {

            // when
            String requestLine = httpRequestReader.readLine(reader);

            // then
            assertThat(requestLine).isEqualTo("GET /index.html HTTP/1.1 ");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void 문단을_읽는다() {
        // given
        String paragraph = String.join(System.lineSeparator(),
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "account=gugu&password=password&email=hkkang@woowahan.com");
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        new ByteArrayInputStream(paragraph.getBytes()), StandardCharsets.UTF_8
                ))
        ) {

            // when
            String requestLineAndHeader = httpRequestReader.readHeader(reader);

            // then
            assertThat(requestLineAndHeader).isEqualTo(String.join(System.lineSeparator(),
                    "Host: localhost:8080 ",
                    "Connection: keep-alive "));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void 요청을_읽는다() {
        // given
        String request = String.join(System.lineSeparator(),
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "account=gugu&password=password&email=hkkang@woowahan.com");
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        new ByteArrayInputStream(request.getBytes()), StandardCharsets.UTF_8
                ))
        ) {

            // when
            String requestLine = httpRequestReader.readLine(reader);
            String requestHeader = httpRequestReader.readHeader(reader);
            String requestBody = httpRequestReader.readHeader(reader);

            // then
            assertSoftly(softly -> {
                softly.assertThat(requestLine).isEqualTo("GET /index.html HTTP/1.1 ");
                softly.assertThat(requestHeader).isEqualTo(String.join(System.lineSeparator(),
                        "Host: localhost:8080 ",
                        "Connection: keep-alive "));
                softly.assertThat(requestBody).isEqualTo(String.join(System.lineSeparator(),
                        "account=gugu&password=password&email=hkkang@woowahan.com"));
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void 읽을_내용이_없을_때는_빈문자열이_반환된다() {
        // given
        String request = "GET /index.html HTTP/1.1 ";
        try (
                InputStream inputStream = new ByteArrayInputStream(request.getBytes());
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        ) {
            httpRequestReader.readLine(reader);

            // when
            String paragraph = httpRequestReader.readHeader(reader);

            // then
            assertThat(paragraph).isEmpty();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
