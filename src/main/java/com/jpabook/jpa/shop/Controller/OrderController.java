package com.jpabook.jpa.shop.Controller;

import com.jpabook.jpa.shop.Repository.OrderSearch;
import com.jpabook.jpa.shop.Service.ItemService;
import com.jpabook.jpa.shop.Service.MemberService;
import com.jpabook.jpa.shop.Service.OrderService;
import com.jpabook.jpa.shop.domain.Item.Item;
import com.jpabook.jpa.shop.domain.Item.Member;
import com.jpabook.jpa.shop.domain.Item.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {
    // 주문에 반응해서 view에게 요청보내는 구현
    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    @GetMapping("/order")
    public String createForm(Model model) {
        // 가동 시 조회
        List<Member> members = memberService.findMembers();
        List<Item> items = itemService.findItem();

        // 가동 시 반환
        model.addAttribute("members", members);
        model.addAttribute("items", items);

        return "order/orderForm";
    }

    @GetMapping("/order")
    public String order(@RequestParam("merberId") Long memberId,
                        @RequestParam("itemId") Long itemId,
                        @RequestParam("count") int count) {
        // 어떤 구현으로 소비자에게 서비스를 제송할 것인가 구현
        // 이런 구현같이 Id만 우선 살려서 알리고 각 메서드들이 끌어서 쓰는 구조로 많이 씀
        orderService.order(memberId, itemId, count); // 멀티 셀렉트 구현 가능!!
        return "redirect:/orders";
    }

    @GetMapping("/orders")
    public String orderList(@ModelAttribute("orderSearch") OrderSearch orderSearch, Model model) {
        // 삼품을 조회해서 넘어오고 Model 객체로 지정
        // 조회기능만을 이용하므로 orderRepo에 정의한 것
        List<Order> orders = orderService.findOrders(orderSearch);
        model.addAttribute("orders", orders);

        return "order/orderList";
    }

    @PostMapping("/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") Long orderId) {
        orderService.cancelOrder(orderId);
        return "redirect:/orders";
    }
}
