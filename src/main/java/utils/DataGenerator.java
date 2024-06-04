package utils;

import com.github.javafaker.Faker;
import lombok.experimental.UtilityClass;
import models.PlayerDto;
import models.PlayerUpdateResponseDto;

import java.util.Arrays;
import java.util.List;
@UtilityClass
public class DataGenerator {
    private static final Faker faker = new Faker();
    private static final int MIN_AGE = 18;
    private static final int MAX_AGE = 60;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 16;
    private static final List<String> GENDERS = Arrays.asList("male", "female");

    public static PlayerDto generatePlayer(String role) {
        return PlayerDto.builder()
                .age(faker.number().numberBetween(MIN_AGE, MAX_AGE))
                .gender(faker.options().nextElement(GENDERS))
                .login(faker.name().username())
                .password(faker.internet().password(MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH, true, true))
                .role(role)
                .screenName(faker.name().fullName())
                .build();
    }

    public static PlayerUpdateResponseDto generatePlayerUpdateRequestDto() {
        return PlayerUpdateResponseDto.builder()
                .age(faker.number().numberBetween(MIN_AGE, MAX_AGE))
                .gender(faker.options().nextElement(GENDERS))
                .login(faker.name().username())
                .password(faker.internet().password(MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH, true, true))
                .screenName(faker.name().fullName())
                .build();
    }
}
