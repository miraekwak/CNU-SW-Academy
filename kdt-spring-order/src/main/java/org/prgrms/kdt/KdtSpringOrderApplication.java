package org.prgrms.kdt;

import org.prgrms.kdt.order.OrderProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.text.MessageFormat;

@SpringBootApplication
@ComponentScan(
        basePackages = {"org.prgrms.kdt.order", "org.prgrms.kdt.voucher", "org.prgrms.kdt.configuration"}
)
public class KdtSpringOrderApplication {

    private final static Logger logger = LoggerFactory.getLogger(KdtSpringOrderApplication.class);

    public static void main(String[] args) {
//        SpringApplication.run(KdtSpringOrderApplication.class);
        var springApplication = new SpringApplication(KdtSpringOrderApplication.class);
//        springApplication.setAdditionalProfiles("dev");
        var applicationContext = springApplication.run(args);

        var orderProperties = applicationContext.getBean(OrderProperties.class);
        logger.info("logger name => {}", logger.getName());
        logger.info("version -> {0}", orderProperties.getVersion());
        logger.info("minimumOrderAmount -> {0}", orderProperties.getMinimumOrderAmount());
        logger.info("supportVendors -> {0}", orderProperties.getSupportVendors());
        logger.info("description -> {0}", orderProperties.getDescription());

    }

}
