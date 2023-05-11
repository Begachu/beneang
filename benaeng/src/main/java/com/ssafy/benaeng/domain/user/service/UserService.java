package com.ssafy.benaeng.domain.user.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ssafy.benaeng.domain.user.entity.JwtToken;
import com.ssafy.benaeng.domain.user.entity.User;
import com.ssafy.benaeng.domain.user.jwt.JwtTokenProvider;
import com.ssafy.benaeng.domain.user.repository.UserRepository;
import com.ssafy.benaeng.domain.user.responseDto.UpdateUserDto;
import com.ssafy.benaeng.domain.user.responseDto.UserDto;
import com.ssafy.benaeng.global.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    public UpdateUserDto updateUser(Long id, com.ssafy.benaeng.domain.user.requestDto.UpdateUserDto userDto) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id + "에 해당하는 User"));
        user.updateUser(userDto.getIsDark(), userDto.getIsAlarm(), userDto.getIsPurchase(), userDto.getIsCycle());
        userRepository.save(user);
        return UpdateUserDto.builder()
                .isAlarm(user.getIsAlarm())
                .isCycle(user.getIsCycle())
                .isDark(user.getIsDark())
                .isPurchase(user.getIsPurchase())
                .build();
    }

    public UserDto getUser(HttpServletRequest request, Long id) {
        String token = jwtTokenProvider.resolveToken(request);
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id + "에 해당하는 User"));

        if(jwtTokenProvider.isNewJwt(token)){
            SecurityContextHolder.clearContext();
            JwtToken jwt = getJwt(user.getId(), user.getName());
            token = jwt.getAccessToken();
        }

        return UserDto.builder()
                .isPurchase(user.getIsPurchase())
                .isAlarm(user.getIsAlarm())
                .isCycle(user.getIsCycle())
                .isDark(user.getIsDark())
                .accessToken(token)
                .build();
    }
    public void logout(){
        SecurityContextHolder.clearContext();
    }
    public UserDto login(String code, HttpServletResponse response) throws Exception {
        // 1. get kakao token
        String kakaoToken = getKakaoToken(code);
        if (kakaoToken.isEmpty()) throw new EntityNotFoundException("kakao token 없음");

        // 2. get user info
        HashMap<String, Object> userInfo = getUserInfo(kakaoToken);
        Long id = (Long) userInfo.get("kakaoId");
        String name = (String) userInfo.get("nickname");

        // 3. select user
        User user = getOrRegisterUser(id, name);

        // 4. authentication & generate JWT
        JwtToken jwt = getJwt(id, name);

        return user2loginDto(user, jwt);
    }
    public UserDto user2loginDto(User user, JwtToken jwt) {
        return UserDto.builder()
                .isCycle(user.getIsCycle())
                .isPurchase(user.getIsPurchase())
                .isDark(user.getIsDark())
                .isAlarm(user.getIsAlarm())
                .accessToken(jwt.getAccessToken())
                .build();
    }

    public JwtToken getJwt(Long id, String name) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(id, name);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        return jwtTokenProvider.generateToken(authentication);
    }
    public User getOrRegisterUser(Long id, String name) {
        Optional<User> byId = userRepository.findById(id);
        if (byId.isEmpty()) {
            User user = User.builder()
                    .name(name)
                    .id(id)
                    .isAlarm(true)
                    .isDark(true)
                    .isPurchase(true)
                    .isCycle(true)
                    .build();
            userRepository.save(user);
            return user;
        }
        return byId.get();
    }

    public JsonElement getElement(HttpURLConnection conn) throws IOException {
        //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line = "";
        String result = "";

        while ((line = br.readLine()) != null) {
            result += line;
        }

        //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
        JsonParser parser = new JsonParser();
        br.close();
        return parser.parse(result);
    }

    public String getKakaoToken(String code) throws Exception {
        String accessToken = "";
        String refreshToken = "";
        String tokenUrl = "https://kauth.kakao.com/oauth/token";
        String redirectUrl = "https://k8b205.p.ssafy.io";
//        String redirectUrl = "http://localhost:3000";
        URL url = new URL(tokenUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
        StringBuilder sb = new StringBuilder();
        sb.append("grant_type=authorization_code");
        sb.append("&client_id=515ed9f41272edc7e02a5e79d58334f6");
        sb.append("&redirect_uri=" + redirectUrl);  // 인가 코드 받은 uri 입력. 여기로 redirect를 해준다는 건지 아니면 그냥 식별을 위해 필요한건지 알아봐야 함
        sb.append("&code=" + code);
        sb.append("&client_secret=t74WWAPHe7HAJQ1kJX6jiAp472D7VopO");

        bw.write(sb.toString());
        bw.flush();
        bw.close();
        //결과 코드가 200이라면 성공
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("code 값");
        }

        JsonElement element = getElement(conn);
        accessToken = element.getAsJsonObject().get("access_token").getAsString();
        return accessToken;
    }

    public HashMap<String, Object> getUserInfo(String accessToken) throws Exception {
        //    요청하는 클라이언트마다 가진 정보가 다를 수 있기에 HashMap타입으로 선언
        HashMap<String, Object> userInfo = new HashMap<>();
        String reqURL = "https://kapi.kakao.com/v2/user/me";
        URL url = new URL(reqURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");

        //    요청에 필요한 Header에 포함될 내용
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("token 값을 확인해주세요");
        }

        JsonElement element = getElement(conn);
        Long kakaoId = element.getAsJsonObject().get("id").getAsBigInteger().longValue();
        JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();

        String nickname = properties.getAsJsonObject().get("nickname").getAsString();
        String imageUrl = properties.getAsJsonObject().get("profile_image").getAsString();

        userInfo.put("kakaoId", kakaoId);
        userInfo.put("nickname", nickname);
        userInfo.put("imageUrl", imageUrl);

        return userInfo;
    }
}