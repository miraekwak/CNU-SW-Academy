package org.prgrms.kdt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.Result;
import java.sql.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JdbcCustomerRepository {
    private static final Logger logger = LoggerFactory.getLogger(JdbcCustomerRepository.class);

    public List<String> findNames(String name) {
        var SELECT_SQL = "select * from customers where name = ?";
        List<String> names = new ArrayList<>();

        try (
                var connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt", "root", "0000");
                var statement = connection.prepareStatement(SELECT_SQL);

        )
        {
            statement.setString(1, name);
            logger.info("statement -> {}", statement);
            try(
                    var resultSet = statement.executeQuery();
            ){
                while (resultSet.next()) {
                    var customerName = resultSet.getString("name");
                    var customerId = UUID.nameUUIDFromBytes(resultSet.getBytes("customer_id"));
                    var createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
                    logger.info("customer id -> {}, customer name -> {}, create_at -> {}", customerId, customerName, createdAt);
                    names.add(customerName);
                }
            }
        } catch (SQLException throwable) {
            logger.error("got error while closing connection", throwable);
        }
        return names;
    }

    public static void main(String[] args) {
        var names = new JdbcCustomerRepository().findNames("'tester01' OR 'a'= 'a");
        names.forEach(v -> logger.info("Found name -> {}", v));
    }
}
