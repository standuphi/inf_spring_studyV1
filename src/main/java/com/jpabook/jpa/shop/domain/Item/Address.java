package com.jpabook.jpa.shop.domain.Item;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class Address {
    // table의 인스턴스가 될수 있지만 새로운 타입으로서 private선언이 필요한 경우
    // private에게 @Embedded와 클래스로서 새로 생성한다
    // 회원 클래스같은 경우 @Getter를 통해 relation을 가져야 할 것
    private String city;
    private String street;
    private String zipcode;

    // reflection
    protected Address() {
    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
