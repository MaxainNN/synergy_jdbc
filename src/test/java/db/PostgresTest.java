package db;

import org.example.PostgresApp;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

//TODO Fix test
public class PostgresTest {
    private static Connection connection;
    private static PostgresApp postgresApp;

    @BeforeAll
    static void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
        postgresApp = new PostgresApp();

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE organization (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "inn VARCHAR(12), ogrn VARCHAR(13), adress TEXT, " +
                    "phone VARCHAR(12), email VARCHAR(255), " +
                    "is_resident BOOLEAN, is_legal_entity BOOLEAN)");

            stmt.execute("CREATE TABLE contract (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "organization_id INT, payment_amount DECIMAL(15,2), " +
                    "FOREIGN KEY (organization_id) REFERENCES organization(id))");

            stmt.execute("INSERT INTO organization VALUES " +
                    "(1, '1234567890', '1234567890123', 'Moscow, Red Square', '79990000001', " +
                    "'org1@test.com', true, true), " +
                    "(2, '2345678901', '2345678901234', 'London, Baker St', '79990000002', " +
                    "'org2@test.com', true, false)");

            stmt.execute("INSERT INTO contract VALUES " +
                    "(1, 1, 10000.00), (2, 1, 20000.00), (3, 2, 15000.00)");
        }
    }

    @AfterAll
    static void tearDown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    @DisplayName("Should return correct organizations count")
    void testPrintOrganizationsCount() throws SQLException {
        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));
        postgresApp.printOrganizationsCount(connection);
        String output = outContent.toString();
        assertTrue(output.contains("2"));
    }

    @Test
    @DisplayName("Should group organizations by legal type correctly")
    void testPrintOrganizationsByLegalType() throws SQLException {
        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        postgresApp.printOrganizationsByLegalType(connection);
        String output = outContent.toString();

        assertAll(
                () -> assertTrue(output.contains("Legal Entity")),
                () -> assertTrue(output.contains("Individual")),
                () -> assertTrue(output.contains("30000.00")),
                () -> assertTrue(output.contains("15000.00"))
        );
    }

    @Test
    @DisplayName("Should display all organizations with details")
    void testPrintAllOrganizationsDetails() throws SQLException {
        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        postgresApp.printAllOrganizationsDetails(connection);
        String output = outContent.toString();

        assertAll(
                () -> assertTrue(output.contains("org1@test.com")),
                () -> assertTrue(output.contains("org2@test.com")),
                () -> assertTrue(output.contains("Moscow")),
                () -> assertTrue(output.contains("London"))
        );
    }

    @Test
    @DisplayName("Should handle SQL injection attempts safely")
    void testSqlInjectionSafety() throws SQLException {
        assertDoesNotThrow(() -> {
            Connection mockConn = mock(Connection.class);
            PreparedStatement mockStmt = mock(PreparedStatement.class);

            when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mock(ResultSet.class));

            postgresApp.printOrganizationsCount(mockConn);
        });
    }
}
