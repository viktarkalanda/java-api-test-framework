package enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    ADMIN("admin"),
    USER("user"),
    SUPERVISOR("supervisor");

    private final String role;
}
