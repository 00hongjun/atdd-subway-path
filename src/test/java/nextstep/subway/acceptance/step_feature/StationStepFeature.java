package nextstep.subway.acceptance.step_feature;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.dto.StationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class StationStepFeature {

    public static final String GANGNAM_STATION_NAME = "강남역";
    public static final String YEOKSAM_STATION_NAME = "역삼역";
    public static final String NONHYEON_STATION_NAME = "논현역";
    public static final String PANGYO_STATION_NAME = "판교역";
    private static final String CREATE_STATION_NAME_PARAM_KEY = "name";
    private static final String STATION_BASE_URI = "stations";

    public static StationResponse callCreateAndFind(String stationName) {
        Map<String, String> params = createStationParams(stationName);

        ExtractableResponse<Response> createResponse = callCreateStation(params);
        String location = createResponse.header("Location");

        ExtractableResponse<Response> response = callFindStationByUri(location);
        return response.as(StationResponse.class);

    }

    public static ExtractableResponse<Response> callCreateStation(Map<String, String> StationParams) {
        return RestAssured.given().log().all()
                .body(StationParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(STATION_BASE_URI)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> callFindAllStation() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get(STATION_BASE_URI)
                .then().log().all()
                .extract();
        return response;
    }

    public static ExtractableResponse<Response> callFindStationByUri(String uri) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get(uri)
                .then().log().all()
                .extract();
        return response;
    }

    public static ExtractableResponse<Response> callDeleteStation(String uri) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
        return response;
    }

    public static Map<String, String> createStationParams(String name) {
        Map<String, String> result = new HashMap();
        result.put(CREATE_STATION_NAME_PARAM_KEY, name);

        return result;
    }

    public static void checkCreateStation(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    public static void checkCreateStationFail(ExtractableResponse<Response> response) {
        checkResponseStatus(response.statusCode(), HttpStatus.BAD_REQUEST);
    }

    public static void checkFindStation(ExtractableResponse<Response> response) {
        checkResponseStatus(response.statusCode(), HttpStatus.OK);
    }

    public static void checkResponseStatus(int statusCode, HttpStatus httpStatus) {
        assertThat(statusCode).isEqualTo(httpStatus.value());
    }

}
