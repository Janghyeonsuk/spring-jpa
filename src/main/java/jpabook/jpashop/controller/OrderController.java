package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.order.OrderSearch;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import jpabook.jpashop.service.MemberService;
import jpabook.jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    @GetMapping("/order")
    private String createOrder(Model model) {

        List<Member> members = memberService.findMembers();
        List<Item> items = itemService.findItems();
        model.addAttribute("members", members);
        model.addAttribute("items", items);

        return "order/orderForm";
    }

    @PostMapping("/order")
    private String create(@RequestParam Long memberId,
                          @RequestParam Long itemId,
                          @RequestParam int count) {

        //핵심 비즈니스 로직은 식별자만 찾아서 넘겨주고 컨트롤러 안에서 엔티티를 생성하면 영속성 컨텍스트가 존재하는 상태에서 존재 가능
        //트랜잭션없이 바깥에서 조회하면 영속상태가 끝나고 order로 넘어가면 영속성 컨택스트와 관리여 없어지므로
        orderService.order(memberId, itemId, count);
        return "redirect:/orders";
    }

    @GetMapping("/orders") //ModelAttribute("orderSearch")처럼 설정을 미리하면 model에 자동으로 orderSearch가 담긴다.
    public String orderList(@ModelAttribute("orderSearch") OrderSearch orderSearch, Model model) {
        List<Order> orders = orderService.findOrders(orderSearch);
        model.addAttribute("orders", orders);
//        model.addAttribute("orderSearch", orderSearch);

        return "order/orderList";
    }

    @PostMapping("orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return "redirect:/orders";
    }
}
