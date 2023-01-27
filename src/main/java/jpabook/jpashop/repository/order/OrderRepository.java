package jpabook.jpashop.repository.order;

import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    public List<Order> findAllByString(OrderSearch orderSearch) {
        //language=JPAQL
        String jpql = "select o From Order o join o.member m";
        boolean isFirstCondition = true;
        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }
        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000); //최대 1000건
        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();
    }

    /**
     * JPA Criteria
     */

    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Object, Object> m = o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"),
                    orderSearch.getOrderStatus());
            criteria.add(status);
        }

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name =
                    cb.like(m.<String>get("name"), "%" +
                            orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //최대1000건
        return query.getResultList();
    }

    //장점 : Api에서 재활용성이 높음 / 엔티티를 조회한 것으로 데이터 변경이 가능
    //단점 : select 절에서 불필요한 쿼리가 나감
    public List<Order> findAllWithMemberDelivery() {
        //fetch 조인 -> member와 delivery의 Lazy를 다 무시하고 프록시가 아닌 진짜 객체를 다 가져옴
        return em.createQuery(
                "select o from Order o " +
                        " join fetch o. member m" +
                        " join fetch o.delivery d", Order.class
        ).getResultList();
    }

    //JPA distinct
    //1. DB에 distinct를 추가, 컬럼의 모든 속성 값이 같아야 중복을 제거해준다
    //2. 루트(Order)같은 엔티티의 (id 값이 같으면) 중복 제거하고 컬렉션에 한개만 담는다
    //단점 -> 페이징 쿼리가 안나감 1대다 페치조인시 하이버네이트에서 애플리케이션 내부에 올리고 처리를 해주어 값이 큰경우 오류 발생
    public List<Order> findAllWithItem() {
        return em.createQuery(
                        "select distinct o from Order o" +
                                " join fetch o.member m" +
                                " join fetch o.delivery d" +
                                " join fetch o.orderItems oi" +
                                " join fetch oi.item i", Order.class)
                .getResultList();
    }

    //ToOne에서는 뻥튀기 되는일이 없으므로 fetch 조인을 쓰는게 효율적, 페이징에 영향을 주지 않음
    //따라서 ToOne 관계는 페치조인으로 쿼리수를 줄이고 나머지는 batch_fetch_size를 사용
    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery(
                        "select o from Order o " +
                                " join fetch o. member m" +
                                " join fetch o.delivery d", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }
}
