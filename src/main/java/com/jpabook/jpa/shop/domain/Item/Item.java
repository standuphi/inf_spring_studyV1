package com.jpabook.jpa.shop.domain.Item;

import com.jpabook.jpa.shop.Exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="dtype")
@Getter @Setter
public abstract class Item {
    @Id
    @GeneratedValue
    @Column(name="item_id")
    private Long id;
    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy="items")
    private List<Category> categories = new ArrayList<>();

    // 비즈니스 로직 : 재고량 증가/감소
    // class 내부에서 정의하고 응집력을 높임
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("more stock plz");
        } // test에서 정의할 새로운 객체가 요구
        this.stockQuantity = restStock;
    }
}
