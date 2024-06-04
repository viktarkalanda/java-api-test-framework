
import api.PlayerApiClient;
import config.ConfigManager;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.PlayerDto;
import org.apache.http.HttpStatus;
import utils.ApiSpecs;
import utils.DataGenerator;

public abstract class BaseTest {
    protected static final String SUPERVISOR_LOGIN = ConfigManager.getInstance().getSupervisorLogin();
    protected static final String ADMIN_LOGIN = ConfigManager.getInstance().getAdminLogin();
    @Step("Create test player with role: {role}")
    public PlayerDto createTestPlayer(String role) {
        PlayerDto playerData = DataGenerator.generatePlayer(role);
        Response response = new PlayerApiClient(ApiSpecs.getDefaultSpec()).createPlayer(SUPERVISOR_LOGIN, playerData);
        long playerId = response.then()
                .spec(ApiSpecs.responseSpec(HttpStatus.SC_OK))
                .extract()
                .jsonPath()
                .getLong("id");

        playerData.setId(playerId);
        return playerData;
    }
}
