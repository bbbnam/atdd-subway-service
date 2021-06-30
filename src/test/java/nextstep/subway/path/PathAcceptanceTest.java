package nextstep.subway.path;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.auth.acceptance.TestToken;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;

import static nextstep.subway.auth.AuthSteps.로그인_요청;
import static nextstep.subway.line.acceptance.LineSetionSteps.*;
import static nextstep.subway.line.acceptance.LineSteps.*;
import static nextstep.subway.member.MemeberSteps.회원_등록_되어_있음;
import static nextstep.subway.path.PathSteps.*;

@ActiveProfiles("test")
@DisplayName("지하철 경로 조회")
public class PathAcceptanceTest extends AcceptanceTest {

    private LineResponse 신분당선;
    private LineResponse 이호선;
    private LineResponse 삼호선;
    private StationResponse 강남역;
    private StationResponse 양재역;
    private StationResponse 교대역;
    private StationResponse 남부터미널역;
    private String EMAIL = "teenager@email.com";
    private String PASSWORD = "12345";
    private int AGE = 15;

    private TestToken 로그인된_청소년;

    /**
     * 교대역   --- *2호선*(10)  ---     강남역
     * |                                 |
     * *3호선*(7)                     *신분당선* (4)
     * |                                |
     * 남부터미널역 --- *3호선*(10) --- 양재
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = StationAcceptanceTest.지하철역_등록되어_있음("강남역").as(StationResponse.class);
        양재역 = StationAcceptanceTest.지하철역_등록되어_있음("양재역").as(StationResponse.class);
        교대역 = StationAcceptanceTest.지하철역_등록되어_있음("교대역").as(StationResponse.class);
        남부터미널역 = StationAcceptanceTest.지하철역_등록되어_있음("남부터미널역").as(StationResponse.class);

        신분당선 = 지하철_노선_등록되어_있음("신분당선", "bg-red-600", 0, 강남역, 양재역, 4);
        이호선 = 지하철_노선_등록되어_있음("이호선", "bg-red-600", 0, 교대역, 강남역, 10);
        삼호선 = 지하철_노선_등록되어_있음("삼호선", "bg-red-600", 0, 교대역, 양재역, 17);

        지하철_노선에_지하철역_등록되어_있음(삼호선, 교대역, 남부터미널역, 7);
        회원_등록_되어_있음(EMAIL, PASSWORD, AGE);

        ExtractableResponse<Response> response = 로그인_요청(EMAIL, PASSWORD);
        로그인된_청소년 = response.as(TestToken.class);
    }

    @DisplayName("최단 경로를 조회한다. - 로그인 안했을때")
    @Test
    void findShortestPath_withNotLogin() {
        int expectedDistance = 14;
        int expectedFare = 1350;
        ExtractableResponse<Response> response = 지하철_노선_최단경로_조회_요청(교대역, 양재역);

        지하철_노선_최단경로_조회됨(response);
        지하철_노선_최단경로_목록_정렬됨(response, Arrays.asList(교대역, 강남역, 양재역));
        지하철_노선_최단경로_거리_응답됨(response, expectedDistance);
        지하철_노선_최단경로_이용_요금_응답됨(response, expectedFare);
    }

    @DisplayName("최단 경로를 조회한다. - 로그인 했을때 (청소년 할인 해당일때)")
    @Test
    void findShortestPath_withLogin() {
        int expectedDistance = 14;
        int expectedFare = 1150;
        ExtractableResponse<Response> response = 지하철_노선_최단경로_조회_요청_with로그인(교대역, 양재역, 로그인된_청소년);

        지하철_노선_최단경로_조회됨(response);
        지하철_노선_최단경로_목록_정렬됨(response, Arrays.asList(교대역, 강남역, 양재역));
        지하철_노선_최단경로_거리_응답됨(response, expectedDistance);
        지하철_노선_최단경로_이용_요금_응답됨(response, expectedFare);
    }
}
