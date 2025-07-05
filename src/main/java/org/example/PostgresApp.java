package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

/**
 * Example of app that connect to Postgres DB and
 * execute some request according to task
 */
public class PostgresApp {

    private static final String PG_URL = "jdbc:postgresql://localhost:5432/testdb";
    private static final String PG_USER = "db";
    private static final String PG_PASSWORD = "12345";

    public static void main(String[] args) {

        try (Connection connection = DriverManager.getConnection(PG_URL, PG_USER, PG_PASSWORD)) {
            System.out.println("Connection successfully established!");

            // After first execution could be commented (also IOException block)
            executeScript(connection,"src/main/java/org/example/scripts/init_tables.sql");
            System.out.println("Tables in database successfully created!");
            executeScript(connection, "src/main/java/org/example/scripts/seed_data.sql");
            System.out.println("Test data in tables created!");

            // 1. Output orgs count
            printOrganizationsCount(connection);
            // 2. Output orgs grouped by type of legal entity
            printOrganizationsByLegalType(connection);
            // 3. Output all table data (Organization)
            printAllOrganizationsDetails(connection);

        } catch (SQLException e){
            System.out.println("SQL Exception : " + e.getMessage());
        } catch (IOException e){
            System.out.println("IO Exception : " + e.getMessage());
        }
    }

    /**
     * Execute script from sql file
     * @param connection current connection to database
     * @param path path to script
     * @throws IOException if error while reading sql script
     * @throws SQLException if error while execute sql command
     */
    private static void executeScript(Connection connection, String path) throws IOException, SQLException {
        String sql = new String(Files.readAllBytes(Paths.get(path)));
        String[] commands = sql.split(";\\s*\\n");

        try (Statement statement = connection.createStatement()) {
            for (String command : commands){
                if (!command.trim().isEmpty()) {
                    statement.execute(command);
                }
            }
        }
    }

    /**
     * Print count of organizations in the table "Organization"
     * @param connection current connection to database
     * @throws SQLException if error while execute sql command
     */
    public static void printOrganizationsCount(Connection connection) throws SQLException {
        String sql = "SELECT COUNT(*) AS count FROM organization";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                System.out.println("\n1) Total number of organizations: " + count);
            }
        }
    }

    /**
     * Print count of organizations in the table "Organization"
     * grouped by type of legal entity
     * @param connection current connection to database
     * @throws SQLException if error while execute sql command
     */
    public static void printOrganizationsByLegalType(Connection connection) throws SQLException {
        String sql = "SELECT " +
                "CASE WHEN is_legal_entity THEN 'Legal Entity' ELSE 'Individual' END AS legal_type, " +
                "COUNT(*) AS count, " +
                "COALESCE(SUM(c.payment_amount), 0) AS total_payments " +
                "FROM organization o " +
                "LEFT JOIN contract c ON o.id = c.organization_id " +
                "GROUP BY is_legal_entity " +
                "ORDER BY legal_type";

        System.out.println("\n2) Organizations grouped by legal type:");
        System.out.println("+----------------+-------+----------------+");
        System.out.println("| Legal Type     | Count | Total Payments |");
        System.out.println("+----------------+-------+----------------+");

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String legalType = resultSet.getString("legal_type");
                int count = resultSet.getInt("count");
                double totalPayments = resultSet.getDouble("total_payments");

                System.out.printf("| %-14s | %5d | %14.2f |\n",
                        legalType, count, totalPayments);
            }
        }
        System.out.println("+----------------+-------+----------------+");
    }

    /**
     * Print all details of organizations in the table "Organization"
     * @param connection current connection to database
     * @throws SQLException if error while execute sql command
     */
    public static void printAllOrganizationsDetails(Connection connection) throws SQLException {
        String sql = "SELECT " +
                "CASE WHEN is_legal_entity THEN 'Legal Entity' ELSE 'Individual' END AS org_type, " +
                "inn AS tax_number, " +
                "COALESCE(SUBSTRING(adress FROM '([^,]+),') , 'Unknown') AS city, " +
                "COALESCE(SUBSTRING(adress FROM '([^,]+)$'), adress) AS address, " +
                "phone, " +
                "email " +
                "FROM organization " +
                "ORDER BY org_type, city";

        System.out.println("\n3. All organizations details:");
        System.out.println("+----------------+-------------+------------------" +
                "+-----------------------------+--------------+---------------------------+");
        System.out.println("| Organization   | Tax Number  | City             |" +
                " Address                     | Phone        | Email                     |");
        System.out.println("| Type           |             |                  |" +
                "                             |              |                           |");
        System.out.println("+----------------+-------------+------------------+" +
                "-----------------------------+--------------+---------------------------+");

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String orgType = resultSet.getString("org_type");
                String taxNumber = resultSet.getString("tax_number");
                String city = resultSet.getString("city");
                String address = resultSet.getString("address");
                String phone = resultSet.getString("phone");
                String email = resultSet.getString("email");

                city = city != null && city.length() > 15 ? city.substring(0, 12) + "..." : city;
                address = address != null && address.length() > 25 ? address.substring(0, 22) + "..." : address;
                email = email != null && email.length() > 25 ? email.substring(0, 22) + "..." : email;

                System.out.printf("| %-14s | %-11s | %-16s | %-27s | %-12s | %-25s |\n",
                        orgType, taxNumber, city, address, phone, email);
            }
        }
        System.out.println("+----------------+-------------+------------------+" +
                "-----------------------------+--------------+---------------------------+");
    }
}
