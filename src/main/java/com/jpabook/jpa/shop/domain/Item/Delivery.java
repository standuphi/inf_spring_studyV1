package com.jpabook.jpa.shop.domain.Item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class Delivery {
    // 배송기능
    @Id @GeneratedValue
    @Column(name="delivery_id")
    private Long id;

    @OneToOne(mappedBy="delivery", fetch=FetchType.LAZY)
    private Order order;

    @Embedded
    private Address address;

    // odinal type과 string type이 있는 enum은 생각해야 할 것
    // 샇이는 순서가 FILO 이기 떄문에 조회에서 영향이 크다
    @Enumerated(EnumType.STRING)
    private DeliveryStatus Status; // 준비중, 도착중
}
