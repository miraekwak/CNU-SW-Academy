package com.kdt.lecture.domain.order;

import com.kdt.lecture.domain.Customer;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@SpringBootTest
public class OrderPersistenceTest {

    @Autowired
    EntityManagerFactory emf;

    @Test
    void member_insert() {
        Member member = new Member();
        member.setName("kwakmirae");
        member.setAddress("대전 광역시");
        member.setAge(23);
        member.setNickname("mara");
        member.setDescription("백엔드 개발자");

        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        entityManager.persist(member);

        transaction.commit();
    }

    @Test
    void 연관관계_테스트() {
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        Member member = new Member();
        member.setName("kwakmirae");
        member.setAddress("대전 광역시");
        member.setAge(23);
        member.setNickname("mara");
        member.setDescription("백엔드 개발자");

        entityManager.persist(member);


        Order order = new Order();
        order.setUuid(UUID.randomUUID().toString());
        order.setOrderStatus(OrderStatus.OPENED);
        order.setOrderDateTime(LocalDateTime.now());
        order.setMemo("부재시 연락주세요.");
        order.setMember(member);
//        member.setOrders(Lists.newArrayList(order));

        entityManager.persist(order);

        transaction.commit();

        entityManager.clear();
        Order entity = entityManager.find(Order.class, order.getUuid());

        log.info("{}", entity.getMember().getName());
        log.info("{}", entity.getMember().getOrders().size());
        log.info("{}", order.getMember().getOrders().size());
    }
}
