import api.PlayerApiClient;
import enums.UserRole;
import io.qameta.allure.Description;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import models.PlayerDto;
import models.PlayerGetAllResponseDto;
import models.PlayerItem;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;
import api.ApiSpecs;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class GetAllPlayersEndpointTest extends BaseTest {

    @Test
    @Description("Ensure the JSON schema of the Get All Players response is correct")
    public void testGetAllPlayersSchemaValidation() {
        Response response = new PlayerApiClient(ApiSpecs.getDefaultSpec()).getAllPlayers();
        response.then()
                .spec(ApiSpecs.responseSpec(HttpStatus.SC_OK))
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("AllPlayersSchema.json"));
    }

    @Test
    @Description("Ensure the system correctly retrieves all players and includes predefined roles")
    public void testGetAllPlayersWithPredefinedRoles() {
        PlayerDto user = createTestPlayer(UserRole.USER.getRole());
        Response getAllPlayersResponse = new PlayerApiClient(ApiSpecs.getDefaultSpec()).getAllPlayers();
        getAllPlayersResponse.then()
                .spec(ApiSpecs.responseSpec(HttpStatus.SC_OK))
                .body("players", hasSize(greaterThan(1)))
                .body("players.id", hasItem(user.getId().intValue()));

        // Альтернативный способ проверки через десериализацию и assertThat
        PlayerGetAllResponseDto playersResponse = getAllPlayersResponse.as(PlayerGetAllResponseDto.class);
        List<Long> playerIds = playersResponse.getPlayers().stream()
                .map(PlayerItem::getId)
                .collect(Collectors.toList());
        assertThat("Player ID is not found in the list", playerIds, hasItem(user.getId()));
    }

}
