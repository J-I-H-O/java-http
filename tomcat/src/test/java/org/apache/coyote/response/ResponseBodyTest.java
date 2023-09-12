package org.apache.coyote.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.response.body.ResponseBody;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ResponseBodyTest {

    @Test
    void Response_Body의_길이를_잰다() {
        // given
        ResponseBody responseBody = new ResponseBody("Hello World!");

        // when
        int actual = responseBody.measureContentLength();

        // then
        assertThat(actual).isEqualTo(12);
    }
}
