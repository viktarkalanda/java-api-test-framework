package dataproviders;

import enums.UserRole;
import org.testng.annotations.DataProvider;

public class UpdatePlayerTestDataProviders {

    @DataProvider(name = "supervisorUpdateRoles")
    public static Object[][] provideSupervisorUpdateRoles() {
        return new Object[][]{
                {UserRole.USER.getRole()},
                {UserRole.ADMIN.getRole()}
        };
    }

    @DataProvider(name = "selfUpdateRoles")
    public static Object[][] provideSelfUpdateRoles() {
        return new Object[][]{
                {UserRole.ADMIN.getRole()},
                {UserRole.USER.getRole()}
        };
    }

    @DataProvider(name = "invalidAges")
    public Object[][] provideInvalidAges() {
        return new Object[][]{
                {15, "Age below the minimum allowed (16)"},
                {61, "Age above the maximum allowed (60)"}
        };
    }

    @DataProvider(name = "invalidPasswords")
    public static Object[][] provideInvalidPasswords() {
        return new Object[][]{
                {"Short1", "Password too short"},
                {"ThisPasswordIsWayTooLong1234567890", "Password too long"},
                {"passwordwithoutnumbers", "Missing numbers"},
                {"1234567890", "Missing letters"}
        };
    }

    @DataProvider(name = "invalidGenders")
    public static Object[][] provideInvalidGenders() {
        return new Object[][]{
                {"nonbinary", "Gender should be either 'male' or 'female'"},
                {"undefined", "Gender should be either 'male' or 'female'"},
                {"", "Gender cannot be empty"},
                {" ", "Gender cannot be empty"}
        };
    }

    @DataProvider(name = "userUpdateScenarios")
    public static Object[][] provideUserUpdateScenarios() {
        return new Object[][]{
                {UserRole.USER.getRole()},
                {UserRole.ADMIN.getRole()}
        };
    }
}
