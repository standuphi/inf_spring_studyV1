package com.jpabook.jpa.shop.Controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
    // 메인 페이지는 아니다!
    Logger log = LoggerFactory.getLogger(getClass());
    // 혹은 상위에 @Slf4j 를 선언
    @RequestMapping("/")
    public String home() {
        log.info("h-c");
        return "home";
    }
    // 구현된 코드가 html에서 작동할수 있도록 반드시 retunr을 받는 이름으로 정의
}
