package com.lee.domain;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class UserKeyDatabase {
    // 数据库文件名
    private static final String DB_NAME = "user_key.db";
    // 创建表的 SQL 语句
    private static final String CREATE_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS user_key (user TEXT PRIMARY KEY, key TEXT)";
    // 插入数据的 SQL 语句
    private static final String INSERT_SQL = "INSERT INTO user_key (user, key) VALUES (?, ?)";
    // 清空表的 SQL 语句
    private static final String DELETE_ALL_SQL = "DELETE FROM user_key";
    // 数据库连接对象
    private final Connection connection;

    /**
     * 构造函数，初始化数据库连接对象和创建表
     * @throws SQLException
     */
    public UserKeyDatabase() throws SQLException {
        // 使用 DriverManager 获取 SQLite 数据库连接
        connection = DriverManager.getConnection("jdbc:sqlite:" + DB_NAME + "?mode=rw");

        connection.createStatement().executeUpdate(CREATE_TABLE_SQL); // 执行创建表的 SQL 语句
    }

    /**
     * 插入单个用户的公钥到数据库
     * @param user 用户名
     * @param key 公钥
     * @throws SQLException
     */
    public void insertUserKey(String user, String key) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) { // 使用 PreparedStatement 插入数据
            statement.setString(1, user);
            statement.setString(2, key);
            statement.executeUpdate(); // 执行插入数据的 SQL 语句
        }
    }

    /**
     * 清空数据库中的所有数据
     * @throws SQLException
     */
    public void clearDatabase() throws SQLException {
        connection.createStatement().executeUpdate(DELETE_ALL_SQL); // 执行清空表的 SQL 语句
    }

    /**
     * 插入多个用户的公钥到数据库
     * @param userKeys 用户名和公钥的键值对
     * @throws SQLException
     */
    public void insertUserKeys(Map<String, String> userKeys) throws SQLException {
        clearDatabase(); // 先清空数据库
        for (Map.Entry<String, String> entry : userKeys.entrySet()) { // 遍历所有键值对
            insertUserKey(entry.getKey(), entry.getValue()); // 插入单个用户的公钥到数据库
        }
    }

    /**
     * 获取某个用户的公钥
     * @param user 用户名
     * @return 用户的公钥，如果不存在则返回 null
     * @throws SQLException
     */
    public String getUserKey(String user) throws SQLException {
        String query = "SELECT key FROM user_key WHERE user=?"; // 查询指定用户名的公钥的 SQL 语句
        try (PreparedStatement statement = connection.prepareStatement(query)) { // 使用 PreparedStatement 执行查询操作
            statement.setString(1, user); // 设置查询参数
            try (ResultSet result = statement.executeQuery()) { // 执行查询操作并获取查询结果
                if (result.next()) { // 如果查询结果非空
                    return result.getString("key"); // 返回查询结果中的公钥
                } else { // 如果查询结果为空
                    return null; // 返回 null
                }
            }
        }
    }

}
