package com.jpabook.jpa.shop.Service;

import com.jpabook.jpa.shop.Repository.ItemRepo;
import com.jpabook.jpa.shop.domain.Item.Bookobj;
import com.jpabook.jpa.shop.domain.Item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly=true)
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepo itemRepo;

    @Transactional // 상위 transaction을 오버라이드 한 개념
    public void saveItem(Item item) {
        itemRepo.save(item);
    }
    public Item findOne(Long itemId) {
        return itemRepo.findOne(itemId);
    }
    public List<Item> findItem() {
        return itemRepo.findAll();
    }
    @Transactional
    public void updateItem(Long id, String name, int price, int stockQuantity) {
        // 추적할수 있도록 설계되는 구조 <bookobj param>
        /*
        Item findItem = itemRepo.findOne(itemId);
        findItem.setPrice(param.getPrice()); // get으로 인자를 받으니 setter 방법
        findItem.setName(param.getName()); // 왠만하면 실무에서는 setter를 활용 x
        findItem.setStockQuantity(param.getStockQuantity());
        return findItem;
        // em으로 보내지 않는 점!!
        */
        Item item = itemRepo.findOne(id);
        item.setName(name);
        item.setPrice(price);
        item.setStockQuantity(stockQuantity);
    }
}
