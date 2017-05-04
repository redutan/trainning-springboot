package com.example.member;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MemberUserDetailsServiceTest {
    @InjectMocks
    MemberUserDetailsService memberUserDetailsService;
    @Mock
    MemberRepository memberRepository;
    // param
    String username;
    Member member;

    @Before
    public void setUp() throws Exception {
        username = random(String.class);
    }

    @Test
    public void testLoadUserByUsername() throws Exception {
        // given
        mockingMemberRepository();
        // when
        UserDetails userDetails = memberUserDetailsService.loadUserByUsername(username);
        // then
        assertUserDetails(userDetails);
    }

    private void mockingMemberRepository() {
        member = random(Member.class, "memberId", "authority");
        member.setMemberId(username);
        member.setAuthority("ROLE_USER");
        when(memberRepository.findOne(eq(username))).thenReturn(member);
    }

    private void assertUserDetails(UserDetails userDetails) {
        assertThat(userDetails, is(notNullValue()));
        assertThat(userDetails.getUsername(), is(username));
        assertThat(userDetails.getPassword(), is(member.getPassword()));
        assertThat(userDetails.getAuthorities(), hasSize(1));
        GrantedAuthority firstAuthority = userDetails.getAuthorities().iterator().next();
        assertThat(firstAuthority.getAuthority(), is(member.getAuthority()));
    }

    @Test(expected = UsernameNotFoundException.class)
    public void testLoadUserByUsername_NotFoundMember() throws Exception {
        // given
        mockingMemberRepository_NotFoundMember();
        // when
        memberUserDetailsService.loadUserByUsername(username);
    }

    private void mockingMemberRepository_NotFoundMember() {
        when(memberRepository.findOne(eq(username))).thenReturn(null);
    }

    @Test(expected = UsernameNotFoundException.class)
    public void testLoadUserByUsername_NoAuthority() throws Exception {
        // given
        mockingMemberRepository_NoAuthority();
        // when
        memberUserDetailsService.loadUserByUsername(username);
    }

    private void mockingMemberRepository_NoAuthority() {
        member = random(Member.class, "memberId", "authority");
        member.setMemberId(username);
        when(memberRepository.findOne(eq(username))).thenReturn(member);
    }
}
