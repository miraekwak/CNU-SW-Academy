package org.prgrms.kdt;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.prgrms.kdt.order.OrderProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.swing.*;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SpringBootApplication
@ComponentScan(
//        basePackages = {"org.prgrms.kdt.order", "org.prgrms.kdt.voucher", "org.prgrms.kdt.configuration"}
          basePackages = {"org.prgrms.kdt.customer", "org.prgrms.kdt.config"}
)
public class KdtSpringOrderApplication {

    private final static Logger logger = LoggerFactory.getLogger(KdtSpringOrderApplication.class);

    public static void main(String[] args) {
//        SpringApplication.run(KdtSpringOrderApplication.class);
//        var springApplication = new SpringApplication(KdtSpringOrderApplication.class);
//        springApplication.setAdditionalProfiles("dev");
//        var applicationContext = springApplication.run(args);

//        var orderProperties = applicationContext.getBean(OrderProperties.class);
//        logger.info("logger name => {}", logger.getName());
//        logger.info("version -> {0}", orderProperties.getVersion());
//        logger.info("minimumOrderAmount -> {0}", orderProperties.getMinimumOrderAmount());
//        logger.info("supportVendors -> {0}", orderProperties.getSupportVendors());
//        logger.info("description -> {0}", orderProperties.getDescription());

        SpringApplication.run(KdtSpringOrderApplication.class);
    }

}
