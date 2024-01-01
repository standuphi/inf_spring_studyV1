package com.jpabook.jpa.shop.domain.Item;

import lombok.Setter;
import lombok.Getter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter @Setter
@DiscriminatorValue("M")
public class Movieobj extends Item {
    private String director;
    private String actor;
}
