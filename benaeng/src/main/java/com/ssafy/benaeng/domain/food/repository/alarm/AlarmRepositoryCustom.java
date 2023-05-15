package com.ssafy.benaeng.domain.food.repository.alarm;

import com.ssafy.benaeng.domain.food.responseDto.AlarmDto;
import com.ssafy.benaeng.domain.food.responseDto.FcmAlamDto;

import java.util.List;

public interface AlarmRepositoryCustom {
    List<AlarmDto> getAlarmList(Long userId);
    List<FcmAlamDto> getFcmAlarmList();
}
