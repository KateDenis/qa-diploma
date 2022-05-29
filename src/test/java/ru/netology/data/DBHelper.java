package ru.netology.data;

import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import lombok.Value;

import java.sql.Connection;
import java.sql.DriverManager;


public class DBHelper {
    private static String dbUrl = System.getProperty("db.url");
    private static String dbUser = System.getProperty("db.user");
    private static String dbPass = System.getProperty("db.pass");

    @SneakyThrows
    private static Connection getConnection() {
        return DriverManager.getConnection(dbUrl, dbUser, dbPass);
    }

    @SneakyThrows
    public static void clearDB() {
        try (
                var conn = getConnection();
        ) {
            var dataStmt = conn.prepareStatement("TRUNCATE credit_request_entity;");
            dataStmt.executeUpdate();
            dataStmt = conn.prepareStatement("TRUNCATE order_entity;");
            dataStmt.executeUpdate();
            dataStmt = conn.prepareStatement("TRUNCATE payment_entity;");
            dataStmt.executeUpdate();
        }
    }

    @SneakyThrows
    public static Integer getCountTableRows(String tableName) {
        var codeSQL = "SELECT count(*) as cnt FROM "+tableName+";";
        var runner = new QueryRunner();
        try (
                var conn = getConnection();
        ) {
            var dataStmt = conn.prepareStatement(codeSQL);

            Integer cnt = 0;
            try (var rs = dataStmt.executeQuery()) {
                if (rs.next()) {
                    cnt = rs.getInt("cnt");

                }
            }
            return cnt;
        }
    }

    @SneakyThrows
    public static Boolean tablePaymentHasRows() {
        Integer cnt = getCountTableRows("payment_entity");
        return cnt > 0;
    }

    @SneakyThrows
    public static Boolean tableCreditRequestHasRows() {
        Integer cnt = getCountTableRows("credit_request_entity");
        return cnt > 0;
    }

    @SneakyThrows
    public static Boolean tableOrderHasRows() {
        Integer cnt = getCountTableRows("order_entity");
        return cnt > 0;
    }

    @SneakyThrows
    public static String getTransactionStatus(String tableName)  {
        var codeSQL = "SELECT status FROM "+tableName+" LIMIT 1;";
        var runner = new QueryRunner();
        try (
                var conn = getConnection();
        ) {
            var dataStmt = conn.prepareStatement(codeSQL);

            String status = "";
            try (var rs = dataStmt.executeQuery()) {
                if (rs.next()) {
                    status = rs.getString("status");

                }
            }
            return status;
        }

    }

    public static String getTransactionPaymentStatus()  {
        String status = getTransactionStatus("payment_entity");
        return status;
    }

    public static String getTransactionCreditRequestStatus()  {
        String status = getTransactionStatus("credit_request_entity");
        return status;
    }
}
