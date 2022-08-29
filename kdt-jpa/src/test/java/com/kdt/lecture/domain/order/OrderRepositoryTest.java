package com.kdt.lecture.domain.order;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class OrderRepositoryTest {

    @Autowired
    OrderRepository orderRepository;

    @Test
    void test() {
        Order order = new Order();
        String uuid = UUID.randomUUID().toString();
        order.setUuid(uuid);
        order.setOrderDatetime(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.OPENED);
        order.setMemo("-----");
        order.setCreatedBy("mirae.kwak");
        order.setCratedAt(LocalDateTime.now());

        orderRepository.save(order);
        // 준영속 상태의 order 객체를 넘기면
        // 엔티티매니저 만들고 트랜잭션 열고 닫고 flush 까지 일어나면서 save 일어남

        Order order1 = orderRepository.findById(uuid).get();
        List<Order> all = orderRepository.findAll();

        orderRepository.findAllByOrderStatus(OrderStatus.OPENED);
        orderRepository.findAllByOrderStatusOrderByOrderDatetime(OrderStatus.OPENED);

        orderRepository.findByMemo("-----");
    }
}