import api.PlayerApiClient;
import enums.UserRole;
import io.qameta.allure.Description;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import models.PlayerDto;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;
import api.ApiSpecs;

import static org.assertj.core.api.Assertions.assertThat;

public class GetPlayerEndpointTest extends BaseTest {

    @Test
    @Description("Ensure the JSON schema of the Get Player response is correct")
    public void testGetExistingPlayerSchemaValidation() {
        PlayerDto createdPlayer = createTestPlayer(UserRole.USER.getRole());
        Response response = new PlayerApiClient(ApiSpecs.getDefaultSpec()).getPlayerByPlayerId(createdPlayer.getId());
        response.then()
                .spec(ApiSpecs.responseSpec(HttpStatus.SC_OK))
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("GetPlayerResponseSchema.json"));
    }

    @Test
    @Description("Ensure the system correctly retrieves a player by valid player ID")
    public void testGetExistingPlayer() {
        PlayerDto createdPlayer = createTestPlayer(UserRole.USER.getRole());
        Response response = new PlayerApiClient(ApiSpecs.getDefaultSpec()).getPlayerByPlayerId(createdPlayer.getId());
        PlayerDto retrievedPlayer = response.then()
                .spec(ApiSpecs.responseSpec(HttpStatus.SC_OK))
                .extract()
                .as(PlayerDto.class);

        assertThat(createdPlayer)
                .usingRecursiveComparison()
                .ignoringFields("password")
                .isEqualTo(retrievedPlayer);
    }

    @Test
    @Description("Ensure that the API correctly handles requests for a non-existing player")
    public void testGetNonExistingPlayer() {
        long nonExistingPlayerId = Long.MAX_VALUE;
        Response response = new PlayerApiClient(ApiSpecs.getDefaultSpec()).getPlayerByPlayerId(nonExistingPlayerId);
        response.then().spec(ApiSpecs.responseSpec(HttpStatus.SC_NOT_FOUND));
    }
}
