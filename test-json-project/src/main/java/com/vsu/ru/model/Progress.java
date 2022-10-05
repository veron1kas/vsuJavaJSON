package com.vsu.ru.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Progress implements DataBaseItem<Long>{
    private Long id;
    private Long playerId;
    private Long resourceId;
    private Integer score;
    private Integer maxScore;
}
