package by.obs.portal;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.Matchers.containsString;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LocalizationTest {

    @Value("${local.server.port}")
    int port;

    @BeforeEach
    void init() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    void getEnglishLanguageForLoginPage() {
        final RequestSpecification request = RestAssured.given().param("lang", "en");
        request.when().get("obsportal/login")
                .then().assertThat().statusCode(200)
                .and().body(containsString("<h5 class=\"card-title text-center\">Sing in to BillingPortal</h5>"));
    }

    @Test
    void getRussianLanguageForLoginPage() {
        final RequestSpecification request = RestAssured.given().param("lang", "ru_RU");
        request.when().get("obsportal/login")
                .then().assertThat().statusCode(200)
                .and().body(containsString("<h5 class=\"card-title text-center\">Вход в BillingPortal</h5>"));
    }
}