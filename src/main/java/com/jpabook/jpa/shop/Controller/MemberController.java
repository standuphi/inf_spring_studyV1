package com.jpabook.jpa.shop.Controller;

import com.jpabook.jpa.shop.Service.MemberService;
import com.jpabook.jpa.shop.domain.Item.Address;
import com.jpabook.jpa.shop.domain.Item.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm"; // return하는 객체가 반드시 요구
    } // member를 찾아서 spring은 get방식으로 요청을 보냄, 동시에 객체로서 하나의 id가 된다

    public String create(@Valid MemberForm Form, BindingResult result) {

        if (result.hasErrors()) {
            // 에러가 일어나서 해당 메서드에 접근하게 된다면
            return "members/createMemberForm";
        }

        Address address = new Address(Form.getCity(), Form.getStreet(), Form.getZipcode());
        // memberform에 대해 데이터가 변수 대상으로 entity table과 relation을 가지는지 명시적인 구현
        // 검증 메서드를 test나 service로 넘기지 않고 controller 안에서 try-catch처럼 움직이는 방법인 것
        // member로 지정하면 해당 class에 모든 어노테이션을 부여하고 깔금하지 않음

        // 영구성에서 요청되는 page는 service로 join
        // 각각 기능을 구동하는 것
        Member member = new Member();
        member.setName(Form.getName());
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/";
    }

    @GetMapping("/members")
    public String list(Model model) {
        List<Member> members = memberService.findMembers();
        model.addAttribute("membbers", members);
        return "members/memberList";
    }
}
