package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    /**
     * 영속성 컨텍스트가 자동 변경
     */
    @Transactional
    public void updateItem(UpdateItemDto itemDto) {
        //트랜잭션안에서 엔티티를 조회해야 영속상태가 된다.
        Item findItem = itemRepository.findOne(itemDto.getId());

        //(변경감지되면서 DB에 Update 쿼리) 의미있는 메서드를 통해서 역추적 -> 바뀌는 위치를 파악하기 쉽다. -> Setter를 쓰지 말자
        findItem.change(findItem, itemDto.getName(), itemDto.getPrice(), itemDto.getStockQuantity());

//        findItem.setName(name);
//        findItem.setPrice(price);
//        findItem.setStockQuantity(stockQuantity);
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }

}
