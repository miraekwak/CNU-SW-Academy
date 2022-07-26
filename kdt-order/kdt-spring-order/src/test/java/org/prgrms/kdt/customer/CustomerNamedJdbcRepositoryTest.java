package org.prgrms.kdt.customer;

import com.wix.mysql.EmbeddedMysql;
import com.wix.mysql.distribution.Version;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;
import org.prgrms.kdt.customer.model.Customer;
import org.prgrms.kdt.customer.repository.CustomerNamedJdbcRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static com.wix.mysql.EmbeddedMysql.anEmbeddedMysql;
import static com.wix.mysql.ScriptResolver.classPathScript;
import static com.wix.mysql.config.Charset.UTF8;
import static com.wix.mysql.config.MysqldConfig.aMysqldConfig;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@SpringJUnitConfig
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CustomerNamedJdbcRepositoryTest {

    private static final Logger logger = LoggerFactory.getLogger(CustomerNamedJdbcRepositoryTest.class);

    @Configuration
    @ComponentScan(
        basePackages= {"org.prgrms.kdt.customer"}
    )
    static class Config{
        @Bean
        public DataSource dataSource() {
            var dataSource = DataSourceBuilder.create()
                    .url("jdbc:mysql://localhost:2215/test-order_mgmt")
                    .username("test")
                    .password("0000")
                    .type(HikariDataSource.class)
                    .build();
//            dataSource.setMaximumPoolSize(1000);
//            dataSource.setMinimumIdle(100);
            return dataSource;
        }

        @Bean
        public JdbcTemplate jdbcTemplate(DataSource dataSource) {
            return new JdbcTemplate(dataSource);
        }

        @Bean
        public NamedParameterJdbcTemplate namedParameterJdbcTemplate(JdbcTemplate jdbcTemplate) {
            return new NamedParameterJdbcTemplate(jdbcTemplate);
        }

        @Bean
        public PlatformTransactionManager platformTransactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }

        @Bean
        public TransactionTemplate transactionTemplate(PlatformTransactionManager platformTransactionManager) {
            return new TransactionTemplate(platformTransactionManager);
        }
    }

    @Autowired
    CustomerNamedJdbcRepository customerRepository;

    @Autowired
    DataSource dataSource;

    Customer newCustomer;

    EmbeddedMysql embeddedMysql;

    @BeforeAll
    void setup() {
        newCustomer = new Customer(UUID.randomUUID(), "test-user", "test-user@gmail.com",
                LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
        var mysqlConfig = aMysqldConfig(Version.v5_7_10)
                .withCharset(UTF8)
                .withPort(2215)
                .withUser("test", "0000")
                .withTimeZone("Asia/Seoul")
                .build();
        embeddedMysql = anEmbeddedMysql(mysqlConfig)
                .addSchema("test-order_mgmt", classPathScript("schema.sql"))
                .start();
    }

    @AfterAll
    void cleanup() {
        embeddedMysql.stop();
    }

    @Test
    @Order(1)
    public void testHikariConnectionPool() {
        assertThat(dataSource.getClass().getName(), is("com.zaxxer.hikari.HikariDataSource"));
    }

    @Test
    @Order(2)
    @DisplayName("고객을 추가할 수 있다.")
    public void testInsert() throws InterruptedException {

        try{
            customerRepository.insert(newCustomer);
        } catch (BadSqlGrammarException e) {
            logger.error("Got error code - ", e.getSQLException().getErrorCode());
        }

        var retrievedCustomer = customerRepository.findById(newCustomer.getCustomerId());
        assertThat(retrievedCustomer.isEmpty(), is(false));
        assertThat(retrievedCustomer.get(), samePropertyValuesAs(newCustomer));
    }


    @Test
    @Order(3)
    @DisplayName("전체 고객을 조회할 수 있다.")
    public void testFindAll() throws InterruptedException {
        var customers = customerRepository.findAll();
        assertThat(customers.isEmpty(), is(false));
        Thread.sleep(10000);
    }

    @Test
    @Order(4)
    @DisplayName("이름으로 고객을 조회할 수 있다.")
    public void testFindByName() throws InterruptedException {
        var customer = customerRepository.findByName(newCustomer.getName());
        assertThat(customer.isEmpty(), is(false));
        Thread.sleep(10000);

        var unknown = customerRepository.findByName("unknown-user");
        assertThat(unknown.isEmpty(), is(true));
        Thread.sleep(10000);
    }

    @Test
    @Order(5)
    @DisplayName("이메일로 고객을 조회할 수 있다.")
    public void testFindByEmail() throws InterruptedException {
        var customer = customerRepository.findByEmail(newCustomer.getEmail());
        assertThat(customer.isEmpty(), is(false));
        Thread.sleep(10000);

        var unknown = customerRepository.findByEmail("unknown-user");
        assertThat(unknown.isEmpty(), is(true));
        Thread.sleep(10000);
    }


    @Test
    @Order(6)
    @DisplayName("고객을 수정할 수 있다.")
    public void testUpdate() throws InterruptedException {
        newCustomer.changeName("updated-user");
        customerRepository.update(newCustomer);

        var all = customerRepository.findAll();
        assertThat(all, hasSize(1));
        assertThat(all, everyItem(samePropertyValuesAs(newCustomer)));

        var retrievedCustomer = customerRepository.findById(newCustomer.getCustomerId());
        assertThat(retrievedCustomer.isEmpty(), is(false));
        assertThat(retrievedCustomer.get(), samePropertyValuesAs(newCustomer));
    }

    @Test
    @Order(7)
    @DisplayName("트랜잭션 테스트")
    public void testTransaction() {
//        var prevOne = customerRepository.findById(newCustomer.getCustomerId());
//        assertThat(prevOne.isEmpty(), is(false));
//
//        var newOne = new Customer(UUID.randomUUID(), "a", "a@gmail.com", LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS)));
//        var insertedNewOne = customerRepository.insert(newOne);
//        try {
//            customerRepository.testTransaction(new Customer(insertedNewOne.getCustomerId(), "b", prevOne.get().getEmail(), newOne.getCreatedAt()));
//        } catch (DataAccessException e) {
//            logger.error("Got error when testing transaction", e);
//        }
//        var maybeNewOne = customerRepository.findById(insertedNewOne.getCustomerId());
//        assertThat(maybeNewOne.isEmpty(), is(false));
//        assertThat(maybeNewOne.get(), samePropertyValuesAs(newOne));
    }
}