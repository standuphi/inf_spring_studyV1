package com.jpabook.jpa.shop.Controller;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class MemberForm {
    // get으로 선언한 membeerform
    @NotEmpty(message = "여백 없이 이름을 보여준다")
    private String name;
    private String city;
    private String street;
    private String zipcode;
}
