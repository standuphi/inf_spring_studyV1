package com.jpabook.jpa.shop.domain.Item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.persistence.CascadeType;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {
    @Id @GeneratedValue
    @Column(name="member_id")
    private Long Id;
    
    // id처럼 필요한 key인 경우 @NotEmpty 선언
    private String name;

    @Embedded
    private Address address;

    @JsonIgnore
    @OneToMany(mappedBy="member") // order의 member에 의해 정의된다
    private List<Order> orders = new ArrayList<>();
    // json이 조회할 수 있도록 한쪽 json을 부여 <order와 member 간 호출>

    // member(회원)의 인스턴스들을 private 선언
    // 그 안에는 memberId로 어떤 column으로 객체를 가지는지 임베디드 타입인지 파악
    // 따라서 member table은 @Columns를 선언

    // 혹은 이런 방법으로도 선언해보자
//    private List<Order> orders;
//    public Member() {
//        orders = new ArrayList<>();
//    }
}
