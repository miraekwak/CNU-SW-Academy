package org.prgrms.kdt;

import org.prgrms.kdt.order.OrderItem;
import org.prgrms.kdt.order.OrderProperties;
import org.prgrms.kdt.order.OrderService;
import org.prgrms.kdt.voucher.FixedAmountVoucher;
import org.prgrms.kdt.voucher.JdbcVoucherRepository;
import org.prgrms.kdt.voucher.VoucherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderTester {

    // 로거이름 : org.prgrms.kdt.OrderTestser
    private static final Logger logger = LoggerFactory.getLogger(OrderTester.class);

    public static void main(String[] args) throws IOException {
        // color conversion 사용
        AnsiOutput.setEnabled(AnsiOutput.Enabled.ALWAYS);

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
        logger.info("logger name => {}", logger.getName());
        logger.info("version -> {0}", orderProperties.getVersion());
        logger.info("minimumOrderAmount -> {0}", orderProperties.getMinimumOrderAmount());
        logger.info("supportVendors -> {0}", orderProperties.getSupportVendors());
        logger.info("description -> {0}", orderProperties.getDescription());

        var resource = applicationContext.getResource("classpath:application.yaml");
        var resource2 = applicationContext.getResource("file:test/sample.txt"); // working directory 기준
        var resource3 = applicationContext.getResource("https://stackoverflow.com/");

        System.out.println(MessageFormat.format("Resource -> {0}", resource.getClass().getCanonicalName()));
        var strings = Files.readAllLines(resource2.getFile().toPath());
        System.out.println(strings.stream().reduce("", (a,b) -> a+"\n"+b));

        System.out.println(MessageFormat.format("Resource3 -> {0}", resource3.getClass().getCanonicalName()));
        var readableByteChannel = Channels.newChannel(resource3.getURL().openStream());
        var bufferedReader = new BufferedReader(Channels.newReader(readableByteChannel, StandardCharsets.UTF_8));
        var contents = bufferedReader.lines().collect(Collectors.joining("\n"));
        System.out.println(contents);

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
