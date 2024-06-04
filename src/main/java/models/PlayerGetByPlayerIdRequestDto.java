package models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlayerGetByPlayerIdRequestDto {
    private long playerId;
}
