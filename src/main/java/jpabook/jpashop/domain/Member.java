package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

//    @Column(unique = true) //유니크 제약 조건
    private String name;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member") //양방향 관계에서 주인이 order에 member
    private List<Order> orders = new ArrayList<>();

}
