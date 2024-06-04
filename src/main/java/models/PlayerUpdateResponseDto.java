package models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerUpdateResponseDto {
    private Integer age;
    private String gender;
    private String login;
    private String password;
    private String role;
    private String screenName;
    private Long id;
}
