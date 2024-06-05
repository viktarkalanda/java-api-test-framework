import api.PlayerApiClient;
import dataproviders.TestDataProviders;
import enums.UserRole;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import models.PlayerDto;
import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import api.ApiSpecs;

public class DeletePlayerEndpointTest extends BaseTest {

    private PlayerApiClient playerApiClient;
    private PlayerDto userEditor;
    private PlayerDto adminEditor;

    @BeforeClass
    public void setup() {
        playerApiClient = new PlayerApiClient(ApiSpecs.getDefaultSpec());
        userEditor = createTestPlayer(UserRole.USER.getRole());
        adminEditor = createTestPlayer(UserRole.ADMIN.getRole());
    }

    @Test(dataProvider = "userRolesProvider", dataProviderClass = TestDataProviders.class)
    @Description("Ensure that the system correctly deletes a player when requested by an authorized supervisor")
    public void testSupervisorDeletePlayerSuccessfully(String targetRole) {
        long playerId = createTestPlayer(targetRole).getId();
        Response response = playerApiClient.deletePlayer(SUPERVISOR_LOGIN, playerId);
        response.then().spec(ApiSpecs.responseSpec(HttpStatus.SC_NO_CONTENT));
        Response getResponse = playerApiClient.getPlayerByPlayerId(playerId);
        getResponse.then().spec(ApiSpecs.responseSpec(HttpStatus.SC_NOT_FOUND));
    }

    @Test(dataProvider = "userRolesProvider", dataProviderClass = TestDataProviders.class)
    @Description("Ensure that a user with 'user' role cannot delete other users")
    public void testUserCannotDeleteOtherRoles(String targetRole) {
        PlayerDto targetPlayer = createTestPlayer(targetRole);
        Response response = playerApiClient.deletePlayer(userEditor.getLogin(), targetPlayer.getId());
        response.then().spec(ApiSpecs.responseSpec(HttpStatus.SC_FORBIDDEN));
    }

    @Test
    @Description("Ensure that a user with 'Admin' role cannot delete other users with 'Admin' role")
    public void testAdminCannotDeleteOtherAdmin() {
        PlayerDto targetPlayer = createTestPlayer(UserRole.ADMIN.getRole());
        Response response = playerApiClient.deletePlayer(adminEditor.getLogin(), targetPlayer.getId());
        response.then().spec(ApiSpecs.responseSpec(HttpStatus.SC_FORBIDDEN));
    }

    @Test
    @Description("Ensure that an admin can delete a user")
    public void testAdminCanDeleteUser() {
        PlayerDto userToBeDeleted = createTestPlayer(UserRole.USER.getRole());
        Response response = playerApiClient.deletePlayer(adminEditor.getLogin(), userToBeDeleted.getId());
        response.then().spec(ApiSpecs.responseSpec(HttpStatus.SC_NO_CONTENT));
        Response getResponse = playerApiClient.getPlayerByPlayerId(userToBeDeleted.getId());
        getResponse.then().spec(ApiSpecs.responseSpec(HttpStatus.SC_NOT_FOUND));
    }

    @Test
    @Description("Ensure that a user cannot delete themselves")
    public void testUserCannotDeleteSelf() {
        PlayerDto userEditor = createTestPlayer(UserRole.USER.getRole());
        Response response = playerApiClient.deletePlayer(userEditor.getLogin(), userEditor.getId());
        response.then().spec(ApiSpecs.responseSpec(HttpStatus.SC_FORBIDDEN));
    }
}
