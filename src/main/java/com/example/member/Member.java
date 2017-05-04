package com.example.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 회원정보
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    @Id
    private String memberId;
    private String password;
    private String authority;
}
