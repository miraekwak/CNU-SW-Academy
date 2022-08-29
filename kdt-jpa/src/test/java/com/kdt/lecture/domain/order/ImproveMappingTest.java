package com.kdt.lecture.domain.order;

import com.kdt.lecture.domain.parent.Parent;
import com.kdt.lecture.domain.parent.ParentId;
import lombok.extern.slf4j.Slf4j;
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
public class ImproveMappingTest {

    @Autowired
    private EntityManagerFactory emf;

    @Test
    void inheritance_test() {
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        Food food = new Food();
        food.setPrice(1000);
        food.setStockQuantity(100);
        food.setChef("백종원");

        entityManager.persist(food);

        transaction.commit();
    }

    @Test
    void mapped_super_class_test() {
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        Order order = new Order();
        order.setUuid(UUID.randomUUID().toString());
        order.setOrderStatus(OrderStatus.OPENED);
        order.setOrderDatetime(LocalDateTime.now());
        order.setMemo("부재시 연락주세요.");

        //
        order.setCreatedBy("mirae.kwak");
        order.setCratedAt(LocalDateTime.now());

        entityManager.persist(order);

        transaction.commit();
    }

    @Test
    void id_test() {
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        Parent parent = new Parent();
        // IdClass
//        parent.setId1("id1");
//        parent.setId2("id2");

        parent.setId(new ParentId("id1", "id2"));

        entityManager.persist(parent);

        transaction.commit();

        entityManager.clear();
        Parent parent1 = entityManager.find(Parent.class, new ParentId("id1", "id2"));
//        log.info("{} {}", parent1.getId1(), parent1.getId2());
        log.info("{} {}", parent1.getId().getId1(), parent1.getId().getId2());
    }
}
