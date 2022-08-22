package org.prgrms.kdt.customer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CustomerJdbcRepository implements CustomerRepository{

    private static final Logger logger = LoggerFactory.getLogger(CustomerJdbcRepository.class);

    private DataSource dataSource;

    private final JdbcTemplate jdbcTemplate;
    private static RowMapper<Customer> customerRowMapper = (resultSet, i) -> {
        var customerName = resultSet.getString("name");
        var customerEmail = resultSet.getString("email");
        var customerId = toUUID(resultSet.getBytes("customer_id"));
        var lastLoginAt = resultSet.getTimestamp("last_login_at") != null ?
                resultSet.getTimestamp("last_login_at").toLocalDateTime() : null;
        var createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
        return new Customer(customerId, customerName, customerEmail, lastLoginAt, createdAt);
    };


    public CustomerJdbcRepository(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Customer insert(Customer customer) {
        var update = jdbcTemplate.update("INSERT INTO customers(customer_id, name, email, created_at) VALUES (UNHEX(REPLACE(?, '-','')), ?, ?, ?)",
                customer.getCustomerId().toString(),
                customer.getName(),
                customer.getEmail(),
                Timestamp.valueOf(customer.getCreatedAt()));
        if(update != 1) {
            throw new RuntimeException("Nothing was inserted");
        }
        return customer;
//        try (
//                var connection = dataSource.getConnection();
//                var statement = connection.prepareStatement(
//                        "INSERT INTO customers(customer_id, name, email, created_at) VALUES (UUID_TO_BIN(?), ?, ?, ?)");
//
//        )
//        {
//            statement.setBytes(1, customer.getCustomerId().toString().getBytes());
//            statement.setString(2, customer.getName());
//            statement.setString(3, customer.getEmail());
//            statement.setTimestamp(4, Timestamp.valueOf(customer.getCreatedAt()));
//            var executeUpdate = statement.executeUpdate();
//            if (executeUpdate != 1) {
//                throw new RuntimeException("Nothing was inserted");
//            }
//            return customer;
//        } catch (SQLException throwable) {
//            logger.error("got error while closing connection", throwable);
//            throw new RuntimeException(throwable);
//        }
    }

    @Override
    public Customer update(Customer customer) {
        var update = jdbcTemplate.update("UPDATE customers SET name = ?, email = ?, last_login_at = ? WHERE customer_id = UNHEX(REPLACE(?, '-',''))",
                customer.getName(),
                customer.getEmail(),
                customer.getLastLoginAt() != null ?
                        Timestamp.valueOf(customer.getLastLoginAt()) : null,
                customer.getCustomerId().toString()
                );
        if(update != 1) {
            throw new RuntimeException("Nothing was updated");
        }
        return customer;
//        try (
//                var connection = dataSource.getConnection();
//                var statement = connection.prepareStatement(
//                        "UPDATE customers SET name = ?, email = ?, last_login_at = ? WHERE customer_id = UUID_TO_BIN(?)");
//        )
//        {
//            statement.setString(1, customer.getName());
//            statement.setString(2, customer.getEmail());
//            statement.setTimestamp(3, customer.getLastLoginAt() != null ?
//                    Timestamp.valueOf(customer.getLastLoginAt()) : null);
//            statement.setBytes(4, customer.getCustomerId().toString().getBytes());
//            var executeUpdate = statement.executeUpdate();
//            if (executeUpdate != 1) {
//                throw new RuntimeException("Nothing was updated");
//            }
//            return customer;
//        } catch (SQLException throwable) {
//            logger.error("got error while closing connection", throwable);
//            throw new RuntimeException(throwable);
//        }
    }

    @Override
    public int count() {
        return jdbcTemplate.queryForObject("select count(*) from customers", Integer.class);
    }

    @Override
    public List<Customer> findAll() {
        return jdbcTemplate.query("select * from customers", customerRowMapper);
//        List<Customer> allCustomers = new ArrayList<>();
//
//        try (
//                var connection = dataSource.getConnection();
//                var statement = connection.prepareStatement("select * from customers");
//                var resultSet = statement.executeQuery();
//        )
//        {
//            while (resultSet.next()) {
//                mapToCustomer(allCustomers, resultSet);
//            }
//        } catch (SQLException throwable) {
//            logger.error("Got error while closing connection", throwable);
//            throw new RuntimeException(throwable);
//        }
//        return allCustomers;
    }

    private void mapToCustomer(List<Customer> allCustomers, java.sql.ResultSet resultSet) throws SQLException {
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
                            "select * from customers where customer_id = UNHEX(REPLACE(?, '-',''))",
                            customerRowMapper,
                            customerId.toString())
            );
        } catch (EmptyResultDataAccessException e){
            logger.error("Got empty result ", e);
            return Optional.empty();
        }

//        List<Customer> allCustomers = new ArrayList<>();
//
//        try (
//                var connection = dataSource.getConnection();
//                var statement = connection.prepareStatement("select * from customers where customer_id = UUID_TO_BIN(?)");
//
//        )
//        {
//            statement.setBytes(1, customerId.toString().getBytes());
//            logger.info("statement -> {}", statement);
//            try(
//                    var resultSet = statement.executeQuery();
//            ){
//                while (resultSet.next()) {
//                    mapToCustomer(allCustomers, resultSet);
//                }
//            }
//        } catch (SQLException throwable) {
//            logger.error("got error while closing connection", throwable);
//            throw new RuntimeException(throwable);
//        }
//        return allCustomers.stream().findFirst();
    }

    @Override
    public Optional<Customer> findByName(String name) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            "select * from customers where name = ?",
                            customerRowMapper,
                            name)
            );
        } catch (EmptyResultDataAccessException e){
            logger.error("Got empty result ", e);
            return Optional.empty();
        }
//        List<Customer> allCustomers = new ArrayList<>();
//
//        try (
//                var connection = dataSource.getConnection();
//                var statement = connection.prepareStatement("select * from customers where name = ?");
//
//        )
//        {
//            statement.setString(1, name);
//            logger.info("statement -> {}", statement);
//            try(
//                    var resultSet = statement.executeQuery();
//            ){
//                while (resultSet.next()) {
//                    mapToCustomer(allCustomers, resultSet);
//                }
//            }
//        } catch (SQLException throwable) {
//            logger.error("got error while closing connection", throwable);
//            throw new RuntimeException(throwable);
//        }
//        return allCustomers.stream().findFirst();
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            "select * from customers where email = ?",
                            customerRowMapper,
                            email)
            );
        } catch (EmptyResultDataAccessException e){
            logger.error("Got empty result ", e);
            return Optional.empty();
        }
//        List<Customer> allCustomers = new ArrayList<>();
//
//        try (
//                var connection = dataSource.getConnection();
//                var statement = connection.prepareStatement("select * from customers where email = ?");
//
//        )
//        {
//            statement.setString(1, email);
//            logger.info("statement -> {}", statement);
//            try(
//                    var resultSet = statement.executeQuery();
//            ){
//                while (resultSet.next()) {
//                    mapToCustomer(allCustomers, resultSet);
//                }
//            }
//        } catch (SQLException throwable) {
//            logger.error("got error while closing connection", throwable);
//            throw new RuntimeException(throwable);
//        }
//        return allCustomers.stream().findFirst();
    }

    @Override
    public void deleteAll() {
        var update = jdbcTemplate.update("DELETE FROM customers");
//        try (
//                var connection = dataSource.getConnection();
//                var statement = connection.prepareStatement("DELETE FROM customers");
//       )
//        {
//            statement.executeUpdate();
//        } catch (SQLException throwable) {
//            logger.error("got error while closing connection", throwable);
//            throw new RuntimeException(throwable);
//        }
    }

    static UUID toUUID(byte[] bytes){
        var byteBuffer = ByteBuffer.wrap(bytes);
        return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
    }
}
