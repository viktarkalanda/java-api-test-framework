package models;

import lombok.Data;

import java.util.List;

@Data
public class PlayerGetAllResponseDto {
    private List<PlayerItem> players;
}
