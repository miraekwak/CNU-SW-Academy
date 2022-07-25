package org.prgrms.kdt;

import org.prgrms.kdt.configuration.YamlPropertiesFactory;
import org.prgrms.kdt.order.Order;
import org.prgrms.kdt.voucher.Voucher;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.yaml.snakeyaml.Yaml;


@Configuration
@ComponentScan(basePackages = {"org.prgrms.kdt.order", "org.prgrms.kdt.voucher", "org.prgrms.kdt.configuration"})
//@ComponentScan(basePackageClasses = {Order.class, Voucher.class})
@PropertySource(value = "application.yaml", factory = YamlPropertiesFactory.class)
@EnableConfigurationProperties //ConfigurationProperties를 사용할 수 있음
public class AppConfiguration {

//    @Bean(initMethod = "init")
//    public BeanOne beanOne(){
//        return new BeanOne();
//    }
}

//class BeanOne implements InitializingBean {
//    public void init() {
//        System.out.println("[BeanOne] init called!");
//    }
//
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        System.out.println("[BeanOne] afterPropertiesSet called!");
//    }
//}