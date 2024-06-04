import api.PlayerApiClient;
import dataproviders.UpdatePlayerTestDataProviders;
import enums.UserRole;
import io.qameta.allure.Description;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import models.PlayerDto;
import models.PlayerUpdateResponseDto;
import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.ApiSpecs;
import utils.DataGenerator;

import static org.assertj.core.api.Assertions.assertThat;


@Slf4j
public class UpdatePlayerEndpointTest extends BaseTest {

    private PlayerApiClient playerApiClient;
    private PlayerDto userEditor;
    private PlayerDto adminEditor;

    @BeforeClass
    public void setup() {
        playerApiClient = new PlayerApiClient(ApiSpecs.getDefaultSpec());
        userEditor = createTestPlayer(UserRole.USER.getRole());
        adminEditor = createTestPlayer(UserRole.ADMIN.getRole());
    }

    @Test
    @Description("Ensure the JSON schema of the Update Player response is correct")
    public void testUpdatePlayerSchemaValidation() {
        PlayerDto createdPlayer = createTestPlayer(UserRole.USER.getRole());
        PlayerUpdateResponseDto updateData = DataGenerator.generatePlayerUpdateRequestDto();
        Response response = playerApiClient.updatePlayer(createdPlayer.getLogin(), createdPlayer.getId(), updateData);
        response.then()
                .spec(ApiSpecs.responseSpec(HttpStatus.SC_OK))
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("PlayerUpdateResponseSchema.json"));
    }

    @Test(dataProvider = "supervisorUpdateRoles", dataProviderClass = UpdatePlayerTestDataProviders.class)
    @Description("Ensure the system correctly updates multiple fields of a player's data with valid permissions and verifies data integrity for supervisors")
    public void testSupervisorUpdatePlayerValid(String targetRole) {
        PlayerDto createdPlayer = createTestPlayer(targetRole);
        PlayerUpdateResponseDto updateData = DataGenerator.generatePlayerUpdateRequestDto();
        Response response = playerApiClient.updatePlayer(SUPERVISOR_LOGIN, createdPlayer.getId(), updateData);
        response.then().spec(ApiSpecs.responseSpec(HttpStatus.SC_OK));
        PlayerUpdateResponseDto responseData = response.body().as(PlayerUpdateResponseDto.class);
        assertThat(responseData)
                .usingRecursiveComparison()
                .ignoringFields("id", "password", "role")
                .isEqualTo(updateData);
    }

    @Test(dataProvider = "selfUpdateRoles", dataProviderClass = UpdatePlayerTestDataProviders.class)
    @Description("Ensure the system allows users to update their own data")
    public void testUserCanUpdateSelf(String role) {
        PlayerDto createdPlayer = createTestPlayer(role);
        PlayerUpdateResponseDto updateData = DataGenerator.generatePlayerUpdateRequestDto();
        Response response = playerApiClient.updatePlayer(createdPlayer.getLogin(), createdPlayer.getId(), updateData);
        response.then().spec(ApiSpecs.responseSpec(HttpStatus.SC_OK));
        PlayerUpdateResponseDto responseData = response.body().as(PlayerUpdateResponseDto.class);
        assertThat(responseData)
                .usingRecursiveComparison()
                .ignoringFields("id", "password", "role")
                .isEqualTo(updateData);
    }

    @Test(dataProvider = "invalidAges", dataProviderClass = UpdatePlayerTestDataProviders.class)
    @Description("Ensure the system rejects updates when the age is outside the allowed range")
    public void testUpdatePlayerWithInvalidAge(int age, String description) {
        log.info("Testing age constraint: {}", description);
        PlayerDto originalPlayer = createTestPlayer(UserRole.USER.getRole());
        PlayerUpdateResponseDto updateData = DataGenerator.generatePlayerUpdateRequestDto();
        updateData.setAge(age);
        Response response = playerApiClient.updatePlayer(SUPERVISOR_LOGIN, originalPlayer.getId(), updateData);
        response.then().spec(ApiSpecs.responseSpec(HttpStatus.SC_BAD_REQUEST));
        Response getResponse = playerApiClient.getPlayerByPlayerId(originalPlayer.getId());
        getResponse.then().spec(ApiSpecs.responseSpec(HttpStatus.SC_OK));
        PlayerDto fetchedPlayer = getResponse.body().as(PlayerDto.class);
        assertThat(fetchedPlayer.getAge()).isEqualTo(originalPlayer.getAge());
    }

    @Test(dataProvider = "invalidPasswords", dataProviderClass = UpdatePlayerTestDataProviders.class)
    @Description("Ensure the system rejects updates when the password does not meet the requirements")
    public void testUpdatePlayerWithInvalidPassword(String password, String description) {
        log.info("Testing password constraint: {}", description);
        PlayerDto originalPlayer = createTestPlayer(UserRole.USER.getRole());
        PlayerUpdateResponseDto updateData = DataGenerator.generatePlayerUpdateRequestDto();
        updateData.setPassword(password);
        Response response = playerApiClient.updatePlayer(SUPERVISOR_LOGIN, originalPlayer.getId(), updateData);
        response.then().spec(ApiSpecs.responseSpec(HttpStatus.SC_BAD_REQUEST));
    }

    @Test(dataProvider = "invalidGenders", dataProviderClass = UpdatePlayerTestDataProviders.class)
    @Description("Ensure the system rejects updates with invalid gender values")
    public void testUpdatePlayerWithInvalidGender(String gender, String description) {
        log.info("Testing invalid gender update: {}", description);
        PlayerDto originalPlayer = createTestPlayer(UserRole.USER.getRole());
        PlayerUpdateResponseDto updateData = new PlayerUpdateResponseDto();
        updateData.setGender(gender);
        Response response = playerApiClient.updatePlayer(SUPERVISOR_LOGIN, originalPlayer.getId(), updateData);
        response.then().spec(ApiSpecs.responseSpec(HttpStatus.SC_BAD_REQUEST));
    }

    @Test(dataProvider = "userUpdateScenarios", dataProviderClass = UpdatePlayerTestDataProviders.class)
    @Description("Ensure users with 'user' role cannot update other players")
    public void testUserCannotUpdateOthers(String targetRole) {
        PlayerDto targetPlayer = createTestPlayer(targetRole);
        PlayerUpdateResponseDto updateData = DataGenerator.generatePlayerUpdateRequestDto();
        Response response = playerApiClient.updatePlayer(userEditor.getLogin(), targetPlayer.getId(), updateData);
        response.then().spec(ApiSpecs.responseSpec(HttpStatus.SC_FORBIDDEN));
        Response checkResponse = playerApiClient.getPlayerByPlayerId(targetPlayer.getId());
        checkResponse.then().spec(ApiSpecs.responseSpec(HttpStatus.SC_OK));
        PlayerDto updatedPlayer = checkResponse.body().as(PlayerDto.class);
        assertThat(updatedPlayer)
                .usingRecursiveComparison()
                .ignoringFields("id", "password", "role")
                .isEqualTo(targetPlayer);
    }

    @Test
    @Description("Ensure that a user with the role 'admin' can update data of users with the role 'user'")
    public void testAdminCanUpdateUser() {
        PlayerDto user = createTestPlayer(UserRole.USER.getRole());
        PlayerUpdateResponseDto updateData = DataGenerator.generatePlayerUpdateRequestDto();
        Response response = playerApiClient.updatePlayer(adminEditor.getLogin(), user.getId(), updateData);
        response.then().spec(ApiSpecs.responseSpec(HttpStatus.SC_OK));
        PlayerUpdateResponseDto responseData = response.body().as(PlayerUpdateResponseDto.class);
        assertThat(responseData)
                .usingRecursiveComparison()
                .ignoringFields("id", "password", "role")
                .isEqualTo(updateData);
    }

    @Test
    @Description("Ensure that a user with the role 'admin' cannot update data of another admin")
    public void testAdminCannotUpdateOtherAdmin() {
        PlayerDto otherAdmin = createTestPlayer(UserRole.ADMIN.getRole());
        PlayerUpdateResponseDto updateData = DataGenerator.generatePlayerUpdateRequestDto();
        Response response = playerApiClient.updatePlayer(adminEditor.getLogin(), otherAdmin.getId(), updateData);
        response.then().spec(ApiSpecs.responseSpec(HttpStatus.SC_FORBIDDEN));
    }

}
