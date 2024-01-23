package com.cos.jwt.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Entity // db 자동 생성을 위해 Entity 어노테이션을 붙여주자
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String roles; // USER,ADMIN 만약 role이 하나면 변수명이 role여야 한다.

    // role list 뽑아내는 메소드
    // role 이 두 개 이상 있을 경우에만 필요한 메소드 하나만 있다면 굳이 필요 없음
    public List<String> getRoleList() {
        if (this.roles.length() > 0) { // roles 가 0개가 아니라면
            return Arrays.asList(this.roles.split(","));
        }
        return new ArrayList<>(); // roles 가 0개 라면
    }
}
