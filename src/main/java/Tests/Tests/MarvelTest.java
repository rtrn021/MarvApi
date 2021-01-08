package Tests.Tests;

import Pojo.Marvel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;

public class MarvelTest {

    public static String publicKey = "939e9928e3bc18b8de8550e849a4d3ff";
    public static String privateKey = "243817e682528ec143bca0b1fdac5d0da9ff68c0";

    public static String generate_MD5() {
        /**First I used Instant.now() but It only accepts 1 as a parameter**/
        //String timestamp = Instant.now().toString();
        return DigestUtils.md5Hex(1 + privateKey + publicKey);
    }

    @BeforeAll
    public static void setup(){
        RestAssured.baseURI="https://gateway.marvel.com:443";
        RestAssured.basePath="/v1/public";
//        RestAssured.port = 8000 ;
    }


    @Test
    public void getCharacters(){
        Response response =
                given().log().all()
                        .queryParam("ts", 1)
                        .queryParam("apikey", publicKey)
                        .queryParam("hash", generate_MD5())
                        .queryParam("limit", 100)
                        .queryParam("offset", 2)
                        .get("/characters");

        response.then().statusCode(200); // -> that is an assertion
        List<String> names = response.jsonPath().getList("data.results.name");
        System.out.println("names = " + names);
        System.out.println( response.jsonPath().prettify() );

    }

    @Test
    public void test_SchemaVal() {

                given().log().all()
                        .queryParam("ts", 1)
                        .queryParam("apikey", publicKey)
                        .queryParam("hash", generate_MD5())
                        .queryParam("limit", 100)
                        .queryParam("offset", 0)
                        .get("/characters/1011334")
                .then()
                        .log().all()
                        .assertThat()
                        .body(matchesJsonSchemaInClasspath("MarvelSchema.json"));
    }

    @Test
    public void test1(){
        given().log().uri().accept(ContentType.JSON)
                .queryParam("ts", 1)
                .queryParam("apikey", publicKey)
                .queryParam("hash", generate_MD5())
                .get("/characters/1011334")
        .then()
                .statusCode(200)
                .contentType(ContentType.JSON);

    }

    @Test
    public void test_GetTheFirstChar(){
        Response response = given().log().uri().accept(ContentType.JSON)
                .queryParam("ts", 1)
                .queryParam("apikey", publicKey)
                .queryParam("hash", generate_MD5())
                .get("/characters/1011334");

        System.out.println( response.jsonPath().prettify() );
        System.out.println("response.statusCode() = " + response.statusCode());
        System.out.println("response.then().statusCode(200) = " + response.then().statusCode(200));
        String name = response.jsonPath().getString("data.results.name");
        System.out.println("name = " + name);
        System.out.println( response.then().body("data.results.name",contains("3-D Man")));


    }
    
    @Test
    public void test_Pojo(){
        Response response = given().log().uri().accept(ContentType.JSON)
                .queryParam("ts", 1)
                .queryParam("apikey", publicKey)
                .queryParam("hash", generate_MD5())
                .get("/characters/1011334");
        System.out.println(response.jsonPath().getString("data.results[0]"));

        Marvel marvel = response.jsonPath().getObject("data.results[0]",Marvel.class);

        System.out.println("marvel = " + marvel);
    }

    @Test
    public void test_AllCharNames_100(){
        JsonPath jPath = given()
                .queryParam("ts", 1)
                .queryParam("apikey", publicKey)
                .queryParam("hash", generate_MD5())
                .queryParam("limit", 100)
                .queryParam("offset", 0)
                .get("/characters").jsonPath();
        System.out.println("jPath.getList(\"data.results.name\") = " + jPath.getList("data.results.name"));
        List<Marvel> marvels = jPath.getList("data.results",Marvel.class);
        System.out.println(marvels.size());
        System.out.println(marvels);
    }

    @Test
    public void test_ObjectMapper() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Marvel marvel = mapper.readValue(new File("data.json"),Marvel.class);
        System.out.println("marvel = " + marvel);

    }

    @Test
    public void test_ObjectMapper_List() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<Marvel> marvels = mapper.readValue(new File("all_data.json"),new TypeReference<List<Marvel>>(){});
        System.out.println("marvel = " + marvels);
        System.out.println();
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(marvels));
        mapper.writeValue(new File("newFile.json"),marvels);


    }


    @Test
    public void test_url_and_Count(){
        Response response =
                given()
                        .queryParam("ts", 1)
                        .queryParam("apikey", publicKey)
                        .queryParam("hash", generate_MD5())
                        .log().all().
                        when()
                        .get("/characters/1010699");
        System.out.println(response.jsonPath().prettify());

        System.out.println("url = " + response.jsonPath().get("data.results.urls[0].url[1]"));
        System.out.println("comics.available = " + response.jsonPath().get("data.results.comics.available"));
    }
}
