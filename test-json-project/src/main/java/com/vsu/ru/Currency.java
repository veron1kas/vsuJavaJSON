package com.vsu.ru;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Currency implements DataBaseItem<Long> {
    private Long id;
    private Long resourceId;
    private String name;
    private Integer count;
}
