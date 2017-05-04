package com.example.member;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 회원정보
 */
@Data
@Entity
public class Member {
    @Id
    private String memberId;
    private String password;
    private String authority;
}
