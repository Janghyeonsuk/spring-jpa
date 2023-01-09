package jpabook.jpashop.controller;

import jpabook.jpashop.controller.Form.BookForm;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import jpabook.jpashop.service.UpdateItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    /**
     * 상품 등록
     */
    @GetMapping("items/new")
    public String createForm(Model model) {
        model.addAttribute("form", new BookForm());

        return "items/createItemForm";
    }

    @PostMapping("items/new")
    public String create(BookForm form, BindingResult result) {

        if (result.hasErrors()) {
            return "items/createItemForm";
        }

        Book book = Book.createBook(form.getName(), form.getPrice(), form.getStockQuantity(), form.getAuthor(), form.getIsbn());

        itemService.saveItem(book);
        return "redirect:/items";
    }

    /**
     * 상품 목록
     */
    @GetMapping(value = "/items")
    public String list(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);

        return "items/itemList";
    }

    /**
     * 상품 수정
     */

    @GetMapping(value = "items/{itemId}/edit")
    public String updateItemForm(@PathVariable Long itemId, Model model) {
        Book item = (Book) itemService.findOne(itemId);

        BookForm form = new BookForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form", form);
        return "items/updateItemForm";
    }

/*
    //merge 사용 방법 -> 변경감지는 원하는 속성만 변경이 가능하지만 merge는 모든 속성이 변경 /merge는 필드에 값이 없으면 null로 업데이트해버림
    @PostMapping(value = "items/{itemId}/edit")
    public String updateItem(@PathVariable String itemId, @ModelAttribute("form") BookForm form) {

        //준영속 상태 -> DB에 한번 저장되어서 식별자가 존재하는데 이렇게 임의로 만들어낸 엔티티도 기존 식별자를 가지면 준영속상태
        Book book = Book.updateBook(form.getId(), form.getName(), form.getPrice(), form.getStockQuantity(), form.getAuthor(), form.getIsbn());

        itemService.saveItem(book);
        return "redirect:/items";
    }
*/

    /**
     * 상품 수정, 권장 코드,영속성 컨텍스트가 자동 변경
     */
    @PostMapping(value = "/items/{itemId}/edit")
    public String updateItem(@PathVariable Long itemId, @ModelAttribute("form") BookForm form) {

        //변경 감지
        UpdateItemDto itemDto = new UpdateItemDto(itemId,form.getName(), form.getPrice(), form.getStockQuantity());

        itemService.updateItem(itemDto);
        return "redirect:/items";
    }
}
