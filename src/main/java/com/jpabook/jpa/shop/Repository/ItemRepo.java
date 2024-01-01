package com.jpabook.jpa.shop.Repository;

import com.jpabook.jpa.shop.domain.Item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepo {
    // jps에 저장된 데이터로 비교할 것이고 없으면 신규id가 객체로 적용
    private final EntityManager em;

    public void save(Item item) {
        if (item.getId() == null) {
            em.persist(item); // 영구성에 존속시킴
        } else {
            em.merge(item);
        }
    }
    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }
    public List<Item> findAll() {
        return em.createQuery("select m from Item m", Item.class).getResultList();
    }
}
