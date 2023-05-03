package com.ssafy.benaeng.domain.user.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private Long id;

    private String name;

    private Boolean isDark;

    private Boolean isAlarm;

    private  Boolean isPurchase;

    private  Boolean isCycle;
}
