package org.prgrms.kdt;

import org.prgrms.kdt.order.OrderItem;
import org.prgrms.kdt.order.OrderProperties;
import org.prgrms.kdt.order.OrderService;
import org.prgrms.kdt.voucher.FixedAmountVoucher;
import org.prgrms.kdt.voucher.JdbcVoucherRepository;
import org.prgrms.kdt.voucher.VoucherRepository;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.Assert;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderTester {
    public static void main(String[] args) {
        var applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(AppConfiguration.class);
        var environment = applicationContext.getEnvironment();
        environment.setActiveProfiles("local");
        applicationContext.refresh();

//        var environment = applicationContext.getEnvironment();
//        var version = environment.getProperty("kdt.version");
//        var minimumOrderAmount = environment.getProperty("kdt.minimum-order-amount", Integer.class);
//        var supportVendors = environment.getProperty("kdt.support-vendors", List.class);
//        var description = environment.getProperty("kdt.description", List.class);
//        System.out.println(MessageFormat.format("version -> {0}", version));
//        System.out.println(MessageFormat.format("minimumOrderAmount -> {0}", minimumOrderAmount));
//        System.out.println(MessageFormat.format("supportVendors -> {0}", supportVendors));
//        System.out.println(MessageFormat.format("description -> {0}", description));
        var orderProperties = applicationContext.getBean(OrderProperties.class);
        System.out.println(MessageFormat.format("version -> {0}", orderProperties.getVersion()));
        System.out.println(MessageFormat.format("minimumOrderAmount -> {0}", orderProperties.getMinimumOrderAmount()));
        System.out.println(MessageFormat.format("supportVendors -> {0}", orderProperties.getSupportVendors()));
        System.out.println(MessageFormat.format("description -> {0}", orderProperties.getDescription()));

        var customerId = UUID.randomUUID();

        var voucherRepository = applicationContext.getBean(VoucherRepository.class);
//        var voucherRepository2 =
//                BeanFactoryAnnotationUtils.qualifiedBeanOfType(
//                        applicationContext.getBeanFactory(),
//                        VoucherRepository.class,
//                        "memory");
//        System.out.println(MessageFormat.format("voucherRepository{0}", voucherRepository));
//        System.out.println(MessageFormat.format("voucherRepository2{0}", voucherRepository2));
//        System.out.println(MessageFormat.format("voucherRepository == voucherRepository2 == {0}", voucherRepository == voucherRepository2));

        // profile test
        System.out.println(MessageFormat.format("is Jdbc Repository -> {0}", voucherRepository instanceof JdbcVoucherRepository));
        System.out.println(MessageFormat.format("is Jdbc Repository -> {0}", voucherRepository.getClass().getCanonicalName()));

        var voucher = voucherRepository.insert(new FixedAmountVoucher(UUID.randomUUID(), 10L));

        var orderService = applicationContext.getBean(OrderService.class);
        var order = orderService.createOrder(customerId, new ArrayList<OrderItem>(){{
            add(new OrderItem(UUID.randomUUID(), 100L, 1L));
        }}, voucher.getVoucherId());

        Assert.isTrue(order.totalAmount() == 90L, MessageFormat.format("totalAmount{0} is not 90L", order.totalAmount()));

        applicationContext.close();
    }
}
