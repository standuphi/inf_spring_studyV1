package com.jpabook.jpa.shop.Controller;

import com.jpabook.jpa.shop.Service.ItemService;
import com.jpabook.jpa.shop.domain.Item.Bookobj;
import com.jpabook.jpa.shop.domain.Item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.awt.print.Book;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    // 영구성
    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createForm(Model model) {
        model.addAttribute("form", new BookForm());
        // 이러한 정의는 bean에게 Form 정의를 객체로서 보내고 추적을 할수 있는 것
        return "items/createItemForm";
    }

    // validate 가ㅏㅏㅏㅏ능
    // 안전한 구현은 setter로 불러오는 것보다 가져올 class에서 모두정의하고 getter를 이용하는 것
    // 이는 개발 입장에서 어디서 넘어오는지 정보가 드러나기 떄문에 깔끔하다는 것
    @PostMapping(value="/items/new")
    public String create(BookForm Form) {
        Bookobj book = new Bookobj();
        book.setName(Form.getName());
        book.setPrice(Form.getPrice());
        book.setStockQuantity(Form.getStockQuantity());
        book.setAuthor(Form.getAuthor());
        book.setQwe(Form.getQwe());

        itemService.saveItem(book);
        return "redirect/items";
    }

    // 강품 등록
    @GetMapping("/items")
    public String list(Model model) {
        List<Item> items = itemService.findItem();
        model.addAttribute("items", items);
        return "items/itemList";

    }

    // 상품 갱신(화면에 보여질 곳)
    @GetMapping("/items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {
        Bookobj item = (Bookobj) itemService.findOne(itemId); // 어떤 상품을 반환할지 선택한 것 <bookobj 를 선택>

        BookForm form = new BookForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setQwe(item.getQwe());

        model.addAttribute("form", form);
        return "items/updateItemForm";
    }

    // 갱신한 요청 폼 => 변경 감지에 제일 좋은 예시, 현재는 적용하지 않음
    @PostMapping("items/{itemId}/edit")
    public String updateItem(@PathVariable Long itemId, @ModelAttribute("form") BookForm Form) {
        /*
        Bookobj book = new Bookobj();

        book.setId(Form.getId());
        book.setName(Form.getName());
        book.setPrice(Form.getPrice());
        book.setStockQuantity(Form.getStockQuantity());
        book.setAuthor(Form.getAuthor());
        book.setQwe(Form.getQwe());
        */
        itemService.updateItem(itemId, Form.getName(), Form.getPrice(), Form.getStockQuantity());
        return "redirect:/items";
    }
}
