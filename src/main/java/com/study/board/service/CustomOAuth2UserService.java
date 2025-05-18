package com.study.board.service;

import com.study.board.dto.MemberDTO;
import com.study.board.entity.MemberEntity;
import com.study.board.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(request);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String registrationId = request.getClientRegistration().getRegistrationId(); // google, kakao
        String userNameAttributeName = request.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName(); // "sub", "id"

        String email = null;
        String name = null;

        if ("kakao".equals(registrationId)) {
            log.info("카카오 attributes: {}", attributes);

            Object kakaoAccountObj = attributes.get("kakao_account");
            if (!(kakaoAccountObj instanceof Map<?, ?> kakaoAccount)) {
                throw new OAuth2AuthenticationException("카카오 계정 정보가 없습니다.");
            }

            Object profileObj = kakaoAccount.get("profile");
            if (!(profileObj instanceof Map<?, ?> profile)) {
                throw new OAuth2AuthenticationException("카카오 프로필 정보가 없습니다.");
            }

            name = (String) profile.get("nickname");

            if (name == null) {
                throw new OAuth2AuthenticationException("카카오 닉네임이 없습니다.");
            }

            // 이메일 없으면 임시 대체
            email = (String) kakaoAccount.get("email");
            if (email == null) {
                String providerId = String.valueOf(attributes.get("id")); // 카카오 고유 ID
                email = "kakao_" + providerId + "@socialuser.com";
            }

        } else if ("google".equals(registrationId)) {
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");

            if (email == null || name == null) {
                throw new OAuth2AuthenticationException("구글 사용자 정보가 없습니다.");
            }
        }

        Optional<MemberEntity> optionalMember = memberRepository.findByMemberEmail(email);
        if (optionalMember.isEmpty()) {
            MemberDTO dto = new MemberDTO();
            dto.setMemberEmail(email);
            dto.setMemberName(name);
            dto.setMemberPassword(UUID.randomUUID().toString()); // 의미 없는 임시 비번

            MemberEntity newMember = MemberEntity.toMemberEntity(dto);
            memberRepository.save(newMember);
            log.info("신규 소셜 회원 저장: {}", email);
        }

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                userNameAttributeName
        );
    }
}
