package com.kdt.lecture.domain.order;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@SpringBootTest
public class ProxyTest {

    @Autowired
    private EntityManagerFactory emf;

    private String uuid = UUID.randomUUID().toString();

    @BeforeAll
    void setup() {
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        // 주문 엔티티
        Order order = new Order();
        order.setUuid(uuid);
        order.setOrderStatus(OrderStatus.OPENED);
        order.setOrderDatetime(LocalDateTime.now());
        order.setMemo("부재시 연락주세요.");

        entityManager.persist(order);

        // 회원 엔티티
        Member member = new Member();
        member.setName("mirae.kwak");
        member.setNickName("mara");
        member.setAge(23);
        member.setAddress("대전광역시");
        member.setDescription("백엔드 개발");

        member.addOrder(order);
        entityManager.persist(member);

        transaction.commit();
    }

    @Test
    void proxy() {
        EntityManager entityManager = emf.createEntityManager();
        Order order = entityManager.find(Order.class, uuid);

        Member member = order.getMember(); // 프록시 객체
        log.info("USE BEFORE isLoaded : {}", emf.getPersistenceUnitUtil().isLoaded(member));
        log.info("USE MEMBER : nick-name : {}", member.getNickName()); // member.getNickName : 사용
        log.info("USE AFTER isLoaded : {}", emf.getPersistenceUnitUtil().isLoaded(member));
    }

    @Test
    void move_persist() {
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        Order order = entityManager.find(Order.class, uuid); // 영속상태

        transaction.begin();

        OrderItem item = new OrderItem(); // 준영속 상태
        item.setQuantity(10);
        item.setPrice(1000);

        order.addOrderItem(item); // 영속성전이를 통해서, 영속상태로 바뀐다.

        transaction.commit(); //flush()
        entityManager.clear();

        Order order2 = entityManager.find(Order.class, uuid); // 영속상태

        transaction.begin();

        order2.getOrderItems().remove(0); // 고아상태

        transaction.commit(); // flush

    }

}
