package models;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerDto {
    private Integer age;
    private String gender;
    private String login;
    private String password;
    private String role;
    private String screenName;
    private Long id;
}
