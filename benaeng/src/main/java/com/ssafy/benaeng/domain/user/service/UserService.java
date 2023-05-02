package com.ssafy.benaeng.domain.user.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ssafy.benaeng.domain.user.entity.User;
import com.ssafy.benaeng.domain.user.repository.UserRepository;
import com.ssafy.benaeng.global.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;


    public User getUser(Long id){
        return userRepository.findById(id).orElseThrow(()-> new EntityNotFoundException(id + "에 해당하는 User"));
    }

    public String getKakaoToken(String code){
        log.info("--------------------getKakaoToken of User Service--------------------");
        String accessToken = "";
        String refreshToken = "";
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        try{
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
            sb.append("&redirect_uri=http://localhost:3000/login");  // 인가 코드 받은 uri 입력. 여기로 redirect를 해준다는 건지 아니면 그냥 식별을 위해 필요한건지 알아봐야 함
            sb.append("&code=" + code);
            sb.append("&client_secret=t74WWAPHe7HAJQ1kJX6jiAp472D7VopO");

            bw.write(sb.toString());
            bw.flush();

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            log.info("responseCode : " + responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            log.info("response body : " + result);

            //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            accessToken = element.getAsJsonObject().get("access_token").getAsString();
            refreshToken = element.getAsJsonObject().get("refresh_token").getAsString();

            log.info("access_token : " + accessToken);
            log.info("refresh_token : " + refreshToken);

            br.close();
            bw.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return accessToken;
    }
    public HashMap<String, Object> getUserInfo (String accessToken) {
        log.info("--------------------getUserInfo of User Service--------------------");
        //    요청하는 클라이언트마다 가진 정보가 다를 수 있기에 HashMap타입으로 선언
        HashMap<String, Object> userInfo = new HashMap<>();
        String reqURL = "https://kapi.kakao.com/v2/user/me";
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            //    요청에 필요한 Header에 포함될 내용
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);

            int responseCode = conn.getResponseCode();
            log.info("responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            log.info("response body : " + result);

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);
            log.info("get id : " + element.getAsJsonObject().get("id"));
            Long kakaoId = element.getAsJsonObject().get("id").getAsBigInteger().longValue();
            JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();

            String nickname = properties.getAsJsonObject().get("nickname").getAsString();
            String imageUrl = properties.getAsJsonObject().get("profile_image").getAsString();
            log.info("kakaoId : " + kakaoId);
            log.info("nickname : " + nickname);
            log.info("imageUrl : " + imageUrl);

            userInfo.put("kakaoId", kakaoId);
            userInfo.put("nickname", nickname);
            userInfo.put("imageUrl", imageUrl);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return userInfo;
    }


}
