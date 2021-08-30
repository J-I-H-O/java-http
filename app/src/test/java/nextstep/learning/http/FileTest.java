package nextstep.learning.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 웹서버는 사용자가 요청한 html 파일을 제공 할 수 있어야 한다.<br>
 * File 클래스를 사용해서 파일을 읽어오고, 사용자에게 전달한다.
 */
@DisplayName("File 클래스 학습 테스트")
class FileTest {

    /**
     * File 객체를 생성하려면 파일의 경로를 알아야 한다.<br>
     * 자바 애플리케이션은 resource 디렉터리에 정적 파일을 저장한다.<br>
     * resource 디렉터리의 경로는 어떻게 알아낼 수 있을까?
     */
    @Test
    void resource_디렉터리에_있는_파일의_경로를_찾는다() {
        final String fileName = "nextstep.txt";

        // todo
        final ClassLoader classLoader = getClass().getClassLoader();
        final File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());

        final String actual = file.getName();

        assertThat(actual).endsWith(fileName);
    }

    /**
     * 읽어온 파일의 내용을 I/O Stream을 사용해서 사용자에게 전달 해야 한다.
     * File, Files 클래스를 사용하여 파일의 내용을 읽어보자.
     */
    @Test
    void 파일의_내용을_읽는다() throws IOException {
        final String fileName = "nextstep.txt";

        // todo
        final ClassLoader classLoader = getClass().getClassLoader();
        final File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());
        final String path = URLDecoder.decode(file.getAbsolutePath(), StandardCharsets.UTF_8);
        final List<String> actual = Files.readAllLines(Path.of(path));

        // todo
        assertThat(actual).containsOnly("nextstep");
    }

    @DisplayName("resources/static 디렉토리의 모든 파일을 읽어올 수 있다")
    @Test
    void readResourcesStatic() throws IOException, URISyntaxException {
        final ClassLoader classLoader = getClass().getClassLoader();
        final URL url = classLoader.getResource("static");
        final String path = URLDecoder.decode(url.toString(), StandardCharsets.UTF_8);

        final List<File> collect = Files.walk(Paths.get(new URI(path)))
                .filter(Files::isRegularFile)
                .map(filePath -> filePath.toFile())
                .collect(Collectors.toList());

        List<String> staticFiles = new ArrayList<>();
        for (File file : collect) {
            final String absolutePath = file.getAbsolutePath();
            staticFiles.add(file.getName());
        }

        assertThat(staticFiles).contains("test.css", "test.html");
    }
}
