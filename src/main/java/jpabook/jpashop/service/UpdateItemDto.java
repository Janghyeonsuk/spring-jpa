package jpabook.jpashop.service;

import lombok.Getter;

@Getter
public class UpdateItemDto {
    private Long id;
    private String name;
    private int price;
    private int stockQuantity;

    public UpdateItemDto(Long id, String name, int price, int stockQuantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }
}
