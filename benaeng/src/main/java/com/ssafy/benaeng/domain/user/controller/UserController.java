package com.ssafy.benaeng.domain.user.controller;

import com.ssafy.benaeng.domain.user.entity.User;
import com.ssafy.benaeng.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final HttpSession httpSession;
    private final UserService userService;
    @GetMapping("/check")
    public ResponseEntity<String> check(){
        log.info("------------check of User controller-------------");
        return new ResponseEntity<>("안녕 나는 check야 ", HttpStatus.OK);
    }
    @GetMapping("/social/{code}")
    public ResponseEntity<?> code(@PathVariable("code") String code, HttpServletResponse response){
        log.info("------------code of User controller-------------");
        log.info("인가 코드 : " + code);
        User user = userService.login(code, response);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

}
