package dataproviders;

import enums.UserRole;
import org.testng.annotations.DataProvider;

public class TestDataProviders {
    @DataProvider(name = "userRolesProvider")
    public static Object[][] provideUserRoles() {
        return new Object[][]{
                {UserRole.USER.getRole()},
                {UserRole.ADMIN.getRole()}
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

    @DataProvider(name = "unsupportedRoles")
    public Object[][] provideUnsupportedRoles() {
        return new Object[][]{
                {"guest"},
                {"supervisor"},
                {"manager"}
        };
    }

    @DataProvider(name = "validGenders")
    public Object[][] provideValidGenders() {
        return new Object[][]{
                {"male"},
                {"female"}
        };
    }
}
