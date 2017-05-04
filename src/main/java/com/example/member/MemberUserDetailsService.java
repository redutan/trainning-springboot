package com.example.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberUserDetailsService implements UserDetailsService {
    @Autowired
    private MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = Optional.ofNullable(memberRepository.findOne(username))
                .orElseThrow(() -> new UsernameNotFoundException("Not founds member : " + username));
        String memberAuthority = Optional.ofNullable(member.getAuthority())
                .orElseThrow(() -> new UsernameNotFoundException("Not founds authority : " + username));
        return User.withUsername(username).password(member.getPassword()).roles(memberAuthority).build();
    }
}
