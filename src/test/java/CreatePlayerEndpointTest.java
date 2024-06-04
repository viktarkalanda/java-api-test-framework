import api.PlayerApiClient;
import dataproviders.CreatePlayerTestDataProviders;
import enums.UserRole;
import io.qameta.allure.Description;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import models.PlayerDto;
import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import api.ApiSpecs;
import utils.DataGenerator;


import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class CreatePlayerEndpointTest extends BaseTest {

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
    @Description("Ensure the JSON schema of the player creation response is correct")
    public void testCreatePlayerSchemaValidation() {
        PlayerDto playerData = DataGenerator.generatePlayer(UserRole.USER.getRole());
        Response response = playerApiClient.createPlayer(SUPERVISOR_LOGIN, playerData);
        response.then()
                .spec(ApiSpecs.responseSpec(HttpStatus.SC_OK))
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("CreatePlayerSchema.json"));
    }

    @Test(dataProvider = "ageValidationScenarios", dataProviderClass = CreatePlayerTestDataProviders.class)
    @Description("Ensure the system properly handles age constraints during user creation")
    public void testAgeConstraints(int age, String scenarioDescription) {
        log.info("Testing age constraint: {}", scenarioDescription);
        PlayerDto playerData = DataGenerator.generatePlayer(UserRole.USER.getRole());
        playerData.setAge(age);
        Response response = playerApiClient.createPlayer(SUPERVISOR_LOGIN, playerData);
        response.then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test(dataProvider = "unsupportedRoles", dataProviderClass = CreatePlayerTestDataProviders.class)
    @Description("Ensure the system rejects user creation with roles that are not supported or allowed")
    public void testCreationWithUnsupportedRoles(String role) {
        PlayerDto playerData = DataGenerator.generatePlayer(role);
        Response response = playerApiClient.createPlayer(SUPERVISOR_LOGIN, playerData);
        response.then().spec(ApiSpecs.responseSpec(HttpStatus.SC_BAD_REQUEST));
    }

    @Test
    @Description("Ensure the system enforces unique login constraints")
    public void testUserCreationWithDuplicateLogin() {
        String sharedLogin = "uniqueUser" + System.currentTimeMillis();
        PlayerDto firstPlayer = DataGenerator.generatePlayer(UserRole.USER.getRole());
        firstPlayer.setLogin(sharedLogin);
        Response firstResponse = playerApiClient.createPlayer(SUPERVISOR_LOGIN, firstPlayer);
        firstResponse.then().spec(ApiSpecs.responseSpec(HttpStatus.SC_OK));
        PlayerDto secondPlayer = DataGenerator.generatePlayer(UserRole.USER.getRole());
        secondPlayer.setLogin(sharedLogin);
        Response secondResponse = playerApiClient.createPlayer(SUPERVISOR_LOGIN, secondPlayer);
        secondResponse.then().spec(ApiSpecs.responseSpec(HttpStatus.SC_BAD_REQUEST));
    }

    @Test
    @Description("Ensure the system enforces unique screenName constraints")
    public void testUserCreationWithDuplicateScreenName() {
        String uniqueScreenName = "UniqueScreen" + System.currentTimeMillis();
        PlayerDto firstPlayer = DataGenerator.generatePlayer(UserRole.USER.getRole());
        firstPlayer.setScreenName(uniqueScreenName);
        Response firstResponse = playerApiClient.createPlayer(SUPERVISOR_LOGIN, firstPlayer);
        firstResponse.then().spec(ApiSpecs.responseSpec(HttpStatus.SC_OK));
        PlayerDto secondPlayer = DataGenerator.generatePlayer(UserRole.USER.getRole());
        secondPlayer.setScreenName(uniqueScreenName);
        Response secondResponse = playerApiClient.createPlayer(SUPERVISOR_LOGIN, secondPlayer);
        secondResponse.then().spec(ApiSpecs.responseSpec(HttpStatus.SC_BAD_REQUEST));
    }

    @Test(dataProvider = "invalidPasswords", dataProviderClass = CreatePlayerTestDataProviders.class)
    @Description("Ensure the system rejects user creation with an invalid password")
    public void testPasswordValidation(String password, String errorMessage) {
        PlayerDto playerData = DataGenerator.generatePlayer(UserRole.USER.getRole());
        playerData.setPassword(password);
        log.info("Testing invalid password scenario: {}", errorMessage);
        Response response = playerApiClient.createPlayer(SUPERVISOR_LOGIN, playerData);
        response.then().spec(ApiSpecs.responseSpec(HttpStatus.SC_BAD_REQUEST));
    }

    @Test(dataProvider = "validGenders", dataProviderClass = CreatePlayerTestDataProviders.class)
    @Description("Ensure the system allows user creation with valid genders 'male' and 'female'")
    public void testCreatePlayerWithValidGender(String gender) {
        PlayerDto playerData = DataGenerator.generatePlayer(UserRole.USER.getRole());
        playerData.setGender(gender);
        Response response = playerApiClient.createPlayer(SUPERVISOR_LOGIN, playerData);
        response.then().spec(ApiSpecs.responseSpec(HttpStatus.SC_OK));
    }

    @Test(dataProvider = "invalidGenders", dataProviderClass = CreatePlayerTestDataProviders.class)
    @Description("Ensure the system rejects user creation with an invalid gender")
    public void testCreatePlayerWithInvalidGender(String gender) {
        PlayerDto playerData = DataGenerator.generatePlayer(UserRole.USER.getRole());
        playerData.setGender(gender);
        Response response = playerApiClient.createPlayer(SUPERVISOR_LOGIN, playerData);
        response.then().spec(ApiSpecs.responseSpec(HttpStatus.SC_BAD_REQUEST));
    }

    @Test(dataProvider = "adminRoleEditorProvider", dataProviderClass = CreatePlayerTestDataProviders.class)
    @Description("Test to ensure that the admin can create both admin and user roles correctly")
    public void testAdminCreatePlayerDataValidation(String targetRole) {
        PlayerDto playerData = DataGenerator.generatePlayer(targetRole);
        Response response = playerApiClient.createPlayer(adminEditor.getLogin(), playerData);
        response.then().spec(ApiSpecs.responseSpec(HttpStatus.SC_OK));
        long createdPlayerId = response.jsonPath().getLong("id");
        Response getPlayerResponse = playerApiClient.getPlayerByPlayerId(createdPlayerId);
        getPlayerResponse.then().spec(ApiSpecs.responseSpec(HttpStatus.SC_OK));
        PlayerDto fetchedPlayer = getPlayerResponse.body().as(PlayerDto.class);
        assertThat(fetchedPlayer)
                .usingRecursiveComparison()
                .ignoringFields("id", "password")
                .isEqualTo(playerData);
    }

    @Test(dataProvider = "userRoleCreationScenarios", dataProviderClass = CreatePlayerTestDataProviders.class)
    @Description("Ensure that a user with 'user' role cannot create other users with different roles")
    public void testUserCannotCreateOtherRoles(String targetRole) {
        PlayerDto playerData = DataGenerator.generatePlayer(targetRole);
        Response response = playerApiClient.createPlayer(userEditor.getLogin(), playerData);
        response.then().spec(ApiSpecs.responseSpec(HttpStatus.SC_FORBIDDEN));
    }

}
