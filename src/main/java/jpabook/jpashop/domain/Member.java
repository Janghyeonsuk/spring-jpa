package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
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
    @NotEmpty
    private String name;

    @Embedded
    private Address address;

//    @JsonIgnore //멤버를 조회할때 JSON에서 orders를 빼줌
    @OneToMany(mappedBy = "member") //양방향 관계에서 주인이 order에 member
    private List<Order> orders = new ArrayList<>();

}
