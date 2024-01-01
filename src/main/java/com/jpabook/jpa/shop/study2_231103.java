package com.jpabook.jpa.shop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class study2_231103 {
    @GetMapping("qwe")
    public String hello(Model model) {
        // qwe 라는 이름으로 server에 get방식으로 요청
        // qwe의 매개변수로 Model 라이브러리로 껍데기를 만듬
        // view로 보내진 데이터이다
        model.addAttribute("data", "qweqwe");
        return "qweqwe";
        // 템플릿 엔진에서 view가 되는 web이 qweqwe인 것
        // 해당 파일을 실행하면 qweqwe.html로 이동
    }
}
