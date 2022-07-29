package org.prgrms.kdt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.UUID;

public class JdbcCustomerRepository {
    private static final Logger logger = LoggerFactory.getLogger(JdbcCustomerRepository.class);

    public static void main(String[] args) {
        try (
                var connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt", "root", "0000");
                var statement = connection.createStatement();
                var resultSet = statement.executeQuery("select * from customers");

        )
        {
            while (resultSet.next()) {
                var name = resultSet.getString("name");
                var customerId = UUID.nameUUIDFromBytes(resultSet.getBytes("customer_id"));
                logger.info("customer id -> {}, customer name -> {}", customerId, name);
            }

        } catch (SQLException e) {
            logger.error("got error while closing connection", e);
        }
    }
}
