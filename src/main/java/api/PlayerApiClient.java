package api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;

public class PlayerApiClient {
    private static final String CREATE_PLAYER_ENDPOINT = "/player/create/{editor}";
    private static final String DELETE_PLAYER_ENDPOINT = "/player/delete/{editor}";
    private static final String GET_PLAYER_ENDPOINT = "/player/get";
    private static final String GET_ALL_PLAYERS_ENDPOINT = "/player/get/all";
    private static final String UPDATE_PLAYER_ENDPOINT = "/player/update/{editor}/{id}";
    private static final String EDITOR_PATH_PARAM = "editor";
    private static final String ID_PATH_PARAM = "id";
    private final RequestSpecification requestSpecification;
    private static final Logger logger = LoggerFactory.getLogger(PlayerApiClient.class);

    public PlayerApiClient(RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification;
    }

    @Step("Create player with editor {editor} and data {playerDto}")
    public Response createPlayer(String editor, PlayerDto playerDto) {
        logger.info("Creating player with editor: {}", editor);
        return given()
                .spec(requestSpecification)
                .pathParam(EDITOR_PATH_PARAM, editor)
                .queryParam("age", playerDto.getAge())
                .queryParam("gender", playerDto.getGender())
                .queryParam("login", playerDto.getLogin())
                .queryParam("password", playerDto.getPassword())
                .queryParam("role", playerDto.getRole())
                .queryParam("screenName", playerDto.getScreenName())
                .get(CREATE_PLAYER_ENDPOINT)
                .then()
                .extract()
                .response();
    }

    @Step("Delete player with ID {playerId} by editor {editor}")
    public Response deletePlayer(String editor, long playerId) {
        return given()
                .spec(requestSpecification)
                .pathParam(EDITOR_PATH_PARAM, editor)
                .body(new PlayerDeleteRequestDto(playerId))
                .when()
                .delete(DELETE_PLAYER_ENDPOINT)
                .then()
                .extract()
                .response();
    }

    @Step("Get player information by ID {playerId}")
    public Response getPlayerByPlayerId(long playerId) {
        PlayerGetByPlayerIdRequestDto requestDto = new PlayerGetByPlayerIdRequestDto(playerId);
        return given()
                .spec(requestSpecification)
                .body(requestDto)
                .when()
                .post(GET_PLAYER_ENDPOINT) // should be get
                .then()
                .extract()
                .response();
    }

    @Step("Get all players")
    public Response getAllPlayers() {
        logger.info("Retrieving all players");
        return given()
                .spec(requestSpecification)
                .when()
                .get(GET_ALL_PLAYERS_ENDPOINT)
                .then()
                .extract()
                .response();
    }

    @Step("Update player with editor {editor}, ID {id}, and new data")
    public Response updatePlayer(String editor, long id, PlayerUpdateResponseDto playerData) {
        logger.info("Updating player with ID: {} by editor: {}", id, editor);
        return given()
                .spec(requestSpecification)
                .pathParam(EDITOR_PATH_PARAM, editor)
                .pathParam(ID_PATH_PARAM, id)
                .body(playerData)
                .when()
                .patch(UPDATE_PLAYER_ENDPOINT)
                .then()
                .extract()
                .response();
    }
}
