package com.ssafy.benaeng.domain.food.responseDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@RequiredArgsConstructor
public class AlarmDto {
    private Long userId;
    private Integer type;
    private Integer status;
    private Date createTime;
    private Integer dDay;
    private String foodName;
    private Long foodId;
    private Long foodCategoryId;
    private String msg;
}
