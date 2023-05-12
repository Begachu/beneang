package com.ssafy.benaeng.domain.food.responseDto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class ReportDetailDto {
    private String subCategory;
    private Long purchase;
    private int percent;
    private String msg;
    private Long cycle;
    List<String> preferProducts = new ArrayList<>();
}
