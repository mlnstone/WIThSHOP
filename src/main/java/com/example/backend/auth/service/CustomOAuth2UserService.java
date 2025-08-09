package com.example.backend.auth.service;

import com.example.backend.common.enums.Role;
import com.example.backend.common.enums.UserProvider;
import com.example.backend.point.entity.Point;
import com.example.backend.point.repository.PointRepository;
import com.example.backend.user.entity.PrincipalDetails;
import com.example.backend.user.entity.User;
import com.example.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final PointRepository pointRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        // OAuth2 사용자 정보 얻어옴 (sub, name, email, picture 등)
        OAuth2User oAuth2User = super.loadUser(request);

        // OAuth2 사용자 정보(이메일, 이름 등)를 Map 형태로 가져옴
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // ex) google, kakao
        String registrationId = request.getClientRegistration().getRegistrationId();
        UserProvider provider = convertToUserProvider(registrationId);

        validateAttributes(attributes);
        User user = registerIfNewUser(attributes, provider);

        return new PrincipalDetails(user, attributes);
    }

    private void validateAttributes(Map<String, Object> userInfoAttributes) {
        if (!userInfoAttributes.containsKey("email")) {
            throw new IllegalArgumentException("서드파티의 응답에 email이 존재하지 않습니다!");
        }
    }

    private User registerIfNewUser(Map<String, Object> attributes, UserProvider provider) {
        String email = (String) attributes.get("email");
        String providerId = (String) attributes.get("sub");

        userRepository.findByUserEmail(email).ifPresent(existing -> {
            if (existing.getUserProvider() != provider) {
                throw new IllegalArgumentException("이미 해당 이메일로 가입된 계정이 있습니다.");
            }
        });

        Optional<User> optionalUser = userRepository.findByUserEmailAndUserProvider(email, provider);

        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }

        User newUser = User.builder()
                .userEmail(email)
                .userPwd("oauth") // OAuth 로그인은 패스워드 없이 사용하니 더미값
                .userType(Role.CUSTOMER) // 기본 권한
                .userProvider(provider)
                .userProviderId((String) attributes.get("sub"))
                .build();

        User saved = userRepository.save(newUser);

        // ★ 신규 가입자 포인트 0 생성
        pointRepository.save(Point.builder()
                .user(saved)
                .balance(0L)
                .build());

        return saved;
    }

    // 공급자를 UserProvider enum으로 변환
    private UserProvider convertToUserProvider(String registrationId) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> UserProvider.GOOGLE;
            default -> UserProvider.LOCAL;
        };
    }
}