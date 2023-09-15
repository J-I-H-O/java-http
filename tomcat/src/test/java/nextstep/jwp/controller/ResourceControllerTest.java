package nextstep.jwp.controller;

import static org.apache.coyote.http11.HttpHeaderName.CONTENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

import java.io.IOException;
import java.util.stream.Stream;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestURI;
import org.apache.coyote.util.FileFinder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class ResourceControllerTest {

    private static final String INDEX_FILE = "<!DOCTYPE html>\n"
        + "<html lang=\"en\">\n"
        + "    <head>\n"
        + "        <meta charset=\"utf-8\" />\n"
        + "        <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\n"
        + "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\" />\n"
        + "        <meta name=\"description\" content=\"\" />\n"
        + "        <meta name=\"author\" content=\"\" />\n"
        + "        <title>대시보드</title>\n"
        + "        <link href=\"css/styles.css\" rel=\"stylesheet\" />\n"
        + "        <script src=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/js/all.min.js\" crossorigin=\"anonymous\"></script>\n"
        + "    </head>\n"
        + "    <body class=\"sb-nav-fixed\">\n"
        + "        <nav class=\"sb-topnav navbar navbar-expand navbar-dark bg-dark\">\n"
        + "            <!-- Navbar Brand-->\n"
        + "            <a class=\"navbar-brand ps-3\" href=\"index.html\">대시보드</a>\n"
        + "            <!-- Sidebar Toggle-->\n"
        + "            <button class=\"btn btn-link btn-sm order-1 order-lg-0 me-4 me-lg-0\" id=\"sidebarToggle\" href=\"#!\"><i class=\"fas fa-bars\"></i></button>\n"
        + "\n"
        + "            <!-- before login -->\n"
        + "            <div class=\"navbar-nav d-none d-md-inline-block ms-auto me-0 me-md-3 my-2 my-md-0\">\n"
        + "                <a class=\"nav-link\" href=\"login\" role=\"button\"><i class=\"fas fa-user fa-fw\"></i>&nbsp;로그인</a>\n"
        + "            </div>\n"
        + "            <!-- before login -->\n"
        + "\n"
        + "            <!-- after login -->\n"
        + "<!--            <ul class=\"navbar-nav ms-auto ms-md-0 me-3 me-lg-4\">-->\n"
        + "<!--                <li class=\"nav-item dropdown\">-->\n"
        + "<!--                    <a class=\"nav-link dropdown-toggle\" id=\"navbarDropdown\" href=\"#\" role=\"button\" data-bs-toggle=\"dropdown\" aria-expanded=\"false\"><i class=\"fas fa-user fa-fw\"></i></a>-->\n"
        + "<!--                    <ul class=\"dropdown-menu dropdown-menu-end\" aria-labelledby=\"navbarDropdown\">-->\n"
        + "<!--                        <li><a class=\"dropdown-item\" href=\"#!\">Settings</a></li>-->\n"
        + "<!--                        <li><hr class=\"dropdown-divider\" /></li>-->\n"
        + "<!--                        <li><a class=\"dropdown-item\" href=\"#!\">Logout</a></li>-->\n"
        + "<!--                    </ul>-->\n"
        + "<!--                </li>-->\n"
        + "<!--            </ul>-->\n"
        + "            <!-- after login -->\n"
        + "\n"
        + "        </nav>\n"
        + "        <div id=\"layoutSidenav\">\n"
        + "            <div id=\"layoutSidenav_nav\">\n"
        + "                <nav class=\"sb-sidenav accordion sb-sidenav-dark\" id=\"sidenavAccordion\">\n"
        + "                    <div class=\"sb-sidenav-menu\">\n"
        + "                        <div class=\"nav\">\n"
        + "                            <div class=\"sb-sidenav-menu-heading\">Core</div>\n"
        + "                            <a class=\"nav-link\" href=\"index.html\">\n"
        + "                                <div class=\"sb-nav-link-icon\"><i class=\"fas fa-tachometer-alt\"></i></div>\n"
        + "                                대시보드\n"
        + "                            </a>\n"
        + "                        </div>\n"
        + "                    </div>\n"
        + "                </nav>\n"
        + "            </div>\n"
        + "            <div id=\"layoutSidenav_content\">\n"
        + "                <main>\n"
        + "                    <div class=\"container-fluid px-4\">\n"
        + "                        <h1 class=\"mt-4\">대시보드</h1>\n"
        + "                        <ol class=\"breadcrumb mb-4\">\n"
        + "                            <li class=\"breadcrumb-item active\">첫 페이지</li>\n"
        + "                        </ol>\n"
        + "                        <div class=\"row\">\n"
        + "                            <div class=\"col-lg-6\">\n"
        + "                                <div class=\"card mb-4\">\n"
        + "                                    <div class=\"card-header\">\n"
        + "                                        <i class=\"fas fa-chart-bar me-1\"></i>\n"
        + "                                        Bar Chart\n"
        + "                                    </div>\n"
        + "                                    <div class=\"card-body\"><canvas id=\"myBarChart\" width=\"100%\" height=\"50\"></canvas></div>\n"
        + "                                </div>\n"
        + "                            </div>\n"
        + "                            <div class=\"col-lg-6\">\n"
        + "                                <div class=\"card mb-4\">\n"
        + "                                    <div class=\"card-header\">\n"
        + "                                        <i class=\"fas fa-chart-pie me-1\"></i>\n"
        + "                                        Pie Chart\n"
        + "                                    </div>\n"
        + "                                    <div class=\"card-body\"><canvas id=\"myPieChart\" width=\"100%\" height=\"50\"></canvas></div>\n"
        + "                                </div>\n"
        + "                            </div>\n"
        + "                        </div>\n"
        + "                    </div>\n"
        + "                </main>\n"
        + "                <footer class=\"py-4 bg-light mt-auto\">\n"
        + "                    <div class=\"container-fluid px-4\">\n"
        + "                        <div class=\"d-flex align-items-center justify-content-between small\">\n"
        + "                            <div class=\"text-muted\">Copyright &copy; Your Website 2021</div>\n"
        + "                            <div>\n"
        + "                                <a href=\"index.html\">Home</a>\n"
        + "                                &middot;\n"
        + "                                <a href=\"#\">Privacy Policy</a>\n"
        + "                                &middot;\n"
        + "                                <a href=\"#\">Terms &amp; Conditions</a>\n"
        + "                            </div>\n"
        + "                        </div>\n"
        + "                    </div>\n"
        + "                </footer>\n"
        + "            </div>\n"
        + "        </div>\n"
        + "        <script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js\" crossorigin=\"anonymous\"></script>\n"
        + "        <script src=\"https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.8.0/Chart.min.js\" crossorigin=\"anonymous\"></script>\n"
        + "        <script src=\"js/scripts.js\"></script>\n"
        + "        <script src=\"assets/chart-area.js\"></script>\n"
        + "        <script src=\"assets/chart-bar.js\"></script>\n"
        + "        <script src=\"assets/chart-pie.js\"></script>\n"
        + "    </body>\n"
        + "</html>\n";

    @DisplayName("파일 경로가 존재하거나 정적 자원 요청이면 요청을 처리한다.")
    @Test
    void canHandle() {
        // given
        // when
        // then
        final ResourceController resourceController = new ResourceController();
        Stream.of("/index.html", "/css/styles.css", "/js/scripts.js", "/401.html", "/favicon.ico", "/login",
                "/register")
            .map(
                path -> new HttpRequest(HttpHeaders.empty(), HttpMethod.GET, HttpRequestURI.from(path), "HTTP/1.1",
                    null)
            ).forEach(path -> assertTrue(resourceController.canHandle(path)));
    }

    @DisplayName("정적 파일을 찾아 요청을 처리한다.")
    @Test
    void service() throws IOException {
        // given
        try (final MockedStatic<FileFinder> fileFinderMockedStatic = mockStatic(FileFinder.class)) {
            fileFinderMockedStatic.when(() -> FileFinder.readFile("/index.html"))
                .thenReturn(INDEX_FILE);
        }

        // when
        final ResourceController resourceController = new ResourceController();
        final ResponseEntity responseEntity = resourceController.service(
            new HttpRequest(HttpHeaders.empty(), HttpMethod.GET, HttpRequestURI.from("/index.html"),
                "HTTP/1.1", null));

        // then
        assertAll(
            () -> assertThat(responseEntity.getHeaders()
                .containsHeaderNameAndValue(CONTENT_TYPE, "text/html;charset=utf-8")).isTrue(),
            () -> assertThat(responseEntity.getBody()).isEqualTo(INDEX_FILE)
        );
    }
}
