package com.jpabook.jpa.shop.domain.Item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter @Setter
@DiscriminatorValue("B")
public class Bookobj extends Item {
    private String author;
    private String qwe;
}
