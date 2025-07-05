package org.example.templates;

import java.sql.*;

/**
 * Class - example from the notes for connecting to Postgres
 * using JDBC
 */
public class PostgresJDBCExample {

    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/testdb";
        String user = "db";
        String password = "mysecretpassword";
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connection established successfully!");
            Statement statement = connection.createStatement();

            String insertQuery = "INSERT INTO students (name, age) VALUES ('Alice', 22), ('Bob', 25)";
            statement.executeUpdate(insertQuery);
            System.out.println("Data added successfully.");
            String selectQuery = "SELECT * FROM students";
            ResultSet resultSet = statement.executeQuery(selectQuery);

            System.out.println("Student list:");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                System.out.println("ID: " + id + ", Name: " + name + ", Age: " + age);
            }

            String deleteQuery = "DELETE FROM students;";
            statement.execute(deleteQuery);
        } catch (SQLException e) {
            System.err.println("Error while connecting to DB: " +
                    e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                    System.out.println("Connection closed.");
                }
            } catch (SQLException e) {
                System.err.println("Error while closing connection: " +
                        e.getMessage());
            }
        }
    }
}