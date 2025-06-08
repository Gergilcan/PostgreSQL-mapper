package io.github.gergilcan.PostgreSQLmapper.helpers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.postgresql.util.PGobject;

public class ComplexEntityHelper {

  /**
   * Helper method to create a mock ResultSet with ComplexEntity data
   * Used by performance and correctness tests
   */
  public static ResultSet createComplexEntityResultSet(int rowCount) throws SQLException {
    ResultSet resultSet = mock(ResultSet.class);
    ResultSetMetaData metaData = mock(ResultSetMetaData.class);

    // Set up columns for all fields
    when(metaData.getColumnCount()).thenReturn(13);
    when(metaData.getColumnName(1)).thenReturn("id");
    when(metaData.getColumnName(2)).thenReturn("name");
    when(metaData.getColumnName(3)).thenReturn("age");
    when(metaData.getColumnName(4)).thenReturn("salary");
    when(metaData.getColumnName(5)).thenReturn("account_balance");
    when(metaData.getColumnName(6)).thenReturn("active");
    when(metaData.getColumnName(7)).thenReturn("created_at");
    when(metaData.getColumnName(8)).thenReturn("updated_at");
    when(metaData.getColumnName(9)).thenReturn("tags");
    when(metaData.getColumnName(10)).thenReturn("metadata");
    when(metaData.getColumnName(11)).thenReturn("address");
    when(metaData.getColumnName(12)).thenReturn("contact_info");
    when(metaData.getColumnName(13)).thenReturn("employment_details");

    when(resultSet.getMetaData()).thenReturn(metaData);

    // Set up row counter
    AtomicInteger counter = new AtomicInteger(0);
    when(resultSet.next()).thenAnswer(invocation -> counter.incrementAndGet() <= rowCount);

    // Mock data for each column
    when(resultSet.getObject(1)).thenReturn(UUID.randomUUID());
    when(resultSet.getString(2)).thenReturn("Test Name");
    when(resultSet.getInt(3)).thenReturn(30);
    when(resultSet.getDouble(4)).thenReturn(75000.00);
    when(resultSet.getBigDecimal(5)).thenReturn(new BigDecimal("150000.75"));
    when(resultSet.getBoolean(6)).thenReturn(true);
    when(resultSet.getTimestamp(7)).thenReturn(Timestamp.valueOf(LocalDateTime.now().minusDays(30)));
    when(resultSet.getTimestamp(8)).thenReturn(Timestamp.valueOf(LocalDateTime.now()));

    // JSON arrays and objects as PGobject
    PGobject tagsObject = new PGobject();
    tagsObject.setType("jsonb");
    tagsObject.setValue("[\"java\", \"postgresql\", \"testing\"]");
    when(resultSet.getObject(9)).thenReturn(tagsObject);

    PGobject metadataObject = new PGobject();
    metadataObject.setType("jsonb");
    metadataObject.setValue("{\"createdBy\": \"system\", \"priority\": \"high\"}");
    when(resultSet.getObject(10)).thenReturn(metadataObject);

    PGobject addressObject = new PGobject();
    addressObject.setType("jsonb");
    addressObject.setValue(
        "{\"street\":\"123 Test St\",\"city\":\"Test City\",\"state\":\"TS\",\"zipCode\":\"12345\",\"country\":\"Testland\",\"location\":{\"latitude\":40.7128,\"longitude\":-74.0060}}");
    when(resultSet.getObject(11)).thenReturn(addressObject);

    PGobject contactInfoObject = new PGobject();
    contactInfoObject.setType("jsonb");
    contactInfoObject.setValue(
        "[{\"type\":\"email\",\"value\":\"test@example.com\",\"isPrimary\":true},{\"type\":\"phone\",\"value\":\"+1234567890\",\"isPrimary\":false}]");
    when(resultSet.getObject(12)).thenReturn(contactInfoObject);

    PGobject employmentDetailsObject = new PGobject();
    employmentDetailsObject.setType("jsonb");
    employmentDetailsObject.setValue(
        "{\"department\":\"Engineering\",\"position\":\"Developer\",\"yearsOfService\":5,\"projects\":[{\"name\":\"Project A\",\"description\":\"Test project\",\"startDate\":\"2023-01-01T00:00:00Z\",\"endDate\":\"2023-12-31T00:00:00Z\",\"status\":\"IN_PROGRESS\"}],\"skills\":{\"java\":\"expert\",\"sql\":\"intermediate\"}}");
    when(resultSet.getObject(13)).thenReturn(employmentDetailsObject);
    when(resultSet.getFetchSize()).thenReturn(rowCount);
    return resultSet;
  }
}
