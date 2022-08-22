package org.prgrms.kdt.customer.repository;

import org.prgrms.kdt.customer.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.nio.ByteBuffer;
import java.sql.*;
import java.util.*;

@Repository
@Primary
public class CustomerNamedJdbcRepository implements CustomerRepository {

    private static final Logger logger = LoggerFactory.getLogger(CustomerNamedJdbcRepository.class);

    private final NamedParameterJdbcTemplate jdbcTemplate;

//    private final PlatformTransactionManager transactionManager;

//    private final TransactionTemplate transactionTemplate;

    private static RowMapper<Customer> customerRowMapper = (resultSet, i) -> {
        var customerName = resultSet.getString("name");
        var customerEmail = resultSet.getString("email");
        var customerId = toUUID(resultSet.getBytes("customer_id"));
        var lastLoginAt = resultSet.getTimestamp("last_login_at") != null ?
                resultSet.getTimestamp("last_login_at").toLocalDateTime() : null;
        var createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
        return new Customer(customerId, customerName, customerEmail, lastLoginAt, createdAt);
    };


    public CustomerNamedJdbcRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Map<String, Object> toParamMap(Customer customer){
      return new HashMap<String, Object>(){{
            put("customerId", customer.getCustomerId().toString());
            put("name", customer.getName());
            put("email", customer.getEmail());
            put("createdAt",Timestamp.valueOf(customer.getCreatedAt()));

        }};
    }

    @Override
    public Customer insert(Customer customer) {

        var update = jdbcTemplate.update("INSERT INTO customers(customer_id, name, email, created_at) " +
                        "VALUES (UNHEX(REPLACE(:customerId, '-','')), :name, :email, :createdAt)",
                toParamMap(customer));
        if(update != 1) {
            throw new RuntimeException("Nothing was inserted");
        }
        return customer;
    }

    @Override
    public Customer update(Customer customer) {
        var update = jdbcTemplate.update("UPDATE customers SET name = :name, email = :email, created_at = :createdAt WHERE customer_id = UNHEX(REPLACE(:customerId, '-',''))",
                toParamMap(customer));
        if(update != 1) {
            throw new RuntimeException("Nothing was updated");
        }
        return customer;
    }

    @Override
    public int count() {
        return jdbcTemplate.queryForObject("select count(*) from customers", Collections.emptyMap(), Integer.class);
    }

    @Override
    public List<Customer> findAll() {
        return jdbcTemplate.query("select * from customers", customerRowMapper);
    }

    private void mapToCustomer(List<Customer> allCustomers, ResultSet resultSet) throws SQLException {
        var customerName = resultSet.getString("name");
        var customerEmail = resultSet.getString("email");
        var customerId = toUUID(resultSet.getBytes("customer_id"));
        var lastLoginAt = resultSet.getTimestamp("last_login_at") != null ?
                resultSet.getTimestamp("last_login_at").toLocalDateTime() : null;
        var createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
         allCustomers.add(new Customer(customerId, customerName, customerEmail, lastLoginAt, createdAt));
    }

    @Override
    public Optional<Customer> findById(UUID customerId) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            "select * from customers where customer_id = UNHEX(REPLACE(:customerId, '-',''))",
                            Collections.singletonMap("customerId", customerId.toString()),
                            customerRowMapper
                    )
            );
        } catch (EmptyResultDataAccessException e){
            logger.error("Got empty result ", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Customer> findByName(String name) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            "select * from customers where name = :name",
                            Collections.singletonMap("name", name),
                            customerRowMapper
                            )
            );
        } catch (EmptyResultDataAccessException e){
            logger.error("Got empty result ", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            "select * from customers where email = :email",
                            Collections.singletonMap("email", email),
                            customerRowMapper
                            )
            );
        } catch (EmptyResultDataAccessException e){
            logger.error("Got empty result ", e);
            return Optional.empty();
        }
    }

//    public void testTransaction(Customer customer) {
//        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
//            @Override
//            protected void doInTransactionWithoutResult(TransactionStatus status) {
//                jdbcTemplate.update("UPDATE customers SET name = :name WHERE customer_id = UNHEX(REPLACE(:customerId, '-',''))", toParamMap(customer));
//                jdbcTemplate.update("UPDATE customers SET email = :email WHERE customer_id = UNHEX(REPLACE(:customerId, '-',''))", toParamMap(customer));
//            }
//        });
////        var transaction = transactionManager.getTransaction(new DefaultTransactionDefinition());
//
////        try{
////            jdbcTemplate.update("UPDATE customers SET name = :name WHERE customer_id = UNHEX(REPLACE(:customerId, '-',''))", toParamMap(customer));
////            jdbcTemplate.update("UPDATE customers SET email = :email WHERE customer_id = UNHEX(REPLACE(:customerId, '-',''))", toParamMap(customer));
////            transactionManager.commit(transaction);
////       } catch (DataAccessException e) {
////            logger.info("Got error", e);
////            transactionManager.rollback(transaction);
////        }
//    }

    @Override
    public void deleteAll() {
        var update = jdbcTemplate.update("DELETE FROM customers", Collections.emptyMap());
//        var update = jdbcTemplate.getJdbcTemplate().update("DELETE FROM customers");

    }

    static UUID toUUID(byte[] bytes){
        var byteBuffer = ByteBuffer.wrap(bytes);
        return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
    }
}
