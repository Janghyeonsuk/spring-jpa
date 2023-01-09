package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.*;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("B")
@Getter
@Setter
public class Book extends Item{

    private String author;
    private String isbn;

    //==생성 메서드==//
    public static Book createBook(String name, int price, int stockQuantity, String author, String isbn) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        book.setAuthor(author);
        book.setIsbn(isbn);

        return book;
    }

    public static Book updateBook(Long id, String name, int price, int stockQuantity, String author, String isbn) {
        Book book = new Book();
        book.setId(id);
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        book.setAuthor(author);
        book.setIsbn(isbn);

        return book;
    }
}
