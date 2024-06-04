package dataproviders;

import enums.UserRole;
import org.testng.annotations.DataProvider;

public class CreatePlayerTestDataProviders {

    @DataProvider(name = "unsupportedRoles")
    public Object[][] provideUnsupportedRoles() {
        return new Object[][]{
                {"guest"},
                {"supervisor"},
                {"manager"}
        };
    }

    @DataProvider(name = "invalidPasswords")
    public Object[][] provideInvalidPasswords() {
        return new Object[][]{
                {"Short1", "Password too short"},
                {"ThisPasswordIsWayTooLong123", "Password too long"},
                {"abcdefghi", "Password without numbers"},
                {"1234567890", "Password without letters"}
        };
    }

    @DataProvider(name = "validGenders")
    public Object[][] provideValidGenders() {
        return new Object[][]{
                {"male"},
                {"female"}
        };
    }

    @DataProvider(name = "invalidGenders")
    public Object[][] provideInvalidGenders() {
        return new Object[][]{
                {"nonbinary"},
                {"undefined"},
                {""},
                {" "}
        };
    }

    @DataProvider(name = "adminRoleEditorProvider")
    public static Object[][] adminRoleEditorProvider() {
        return new Object[][]{
                {UserRole.ADMIN.getRole()},
                {UserRole.USER.getRole()}
        };
    }

    @DataProvider(name = "ageValidationScenarios")
    public Object[][] provideAgeValidationScenarios() {
        return new Object[][]{
                {15, "age below minimum"},
                {61, "age above maximum"}
        };
    }

    @DataProvider(name = "userRoleCreationScenarios")
    public static Object[][] provideUserRoleCreationScenarios() {
        return new Object[][]{
                {UserRole.USER.getRole()},
                {UserRole.ADMIN.getRole()}
        };
    }


}
