package io.github.gergilcan.PostgreSQLmapper.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@DisplayName("ResultSetSerializer Performance Tests")
public class ResultSetSerializerPerformanceTest {

  private ObjectMapper objectMapper;
  private ResultSetSerializer serializer;

  @BeforeEach
  void setUp() {
    serializer = new ResultSetSerializer();
    objectMapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();
    module.addSerializer(ResultSet.class, serializer);
    objectMapper.registerModule(module);
  }

  @DisplayName("Performance test with different row counts")
  @ParameterizedTest(name = "Serializing {0} rows")
  @ValueSource(ints = { 10, 100, 1000, 10000 })
  void testSerializationPerformance(int rowCount) throws IOException, SQLException {
    // Create a mock ResultSet with specified number of rows
    ResultSet mockResultSet = createMockResultSet(rowCount, 10);

    // Warm-up
    for (int i = 0; i < 5; i++) {
      objectMapper.writeValueAsString(mockResultSet);
      // Need to recreate the mock for each iteration since ResultSet state is
      // consumed
      mockResultSet = createMockResultSet(rowCount, 10);
    }

    // Actual performance test
    long startTime = System.nanoTime();
    String json = objectMapper.writeValueAsString(mockResultSet);
    long endTime = System.nanoTime();

    long durationMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);

    assertNotNull(json);
    System.out.printf("Serialized %d rows in %d ms%n", rowCount, durationMs);
  }

  @DisplayName("Performance test with different column counts")
  @Test
  void testSerializationWithDifferentColumnCounts() throws IOException, SQLException {
    // Test with different column counts but fixed row count
    int rowCount = 1000;
    int[] columnCounts = { 5, 10, 20, 50 };

    for (int columnCount : columnCounts) {
      ResultSet mockResultSet = createMockResultSet(rowCount, columnCount);

      long startTime = System.nanoTime();
      String json = objectMapper.writeValueAsString(mockResultSet);
      long endTime = System.nanoTime();

      long durationMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);

      assertNotNull(json);
      System.out.printf("Serialized %d rows with %d columns in %d ms%n",
          rowCount, columnCount, durationMs);
    }
  }

  private ResultSet createMockResultSet(int rowCount, int columnCount) throws SQLException {
    ResultSet mockResultSet = mock(ResultSet.class);
    ResultSetMetaData metaData = mock(ResultSetMetaData.class);

    when(mockResultSet.getMetaData()).thenReturn(metaData);
    when(metaData.getColumnCount()).thenReturn(columnCount);

    // Set up column names
    for (int i = 1; i <= columnCount; i++) {
      when(metaData.getColumnName(i)).thenReturn("column" + i);
    }

    // Use AtomicInteger to track state between invocations
    AtomicInteger rowCounter = new AtomicInteger(0);

    // Set up row iteration behavior
    when(mockResultSet.next()).thenAnswer(invocation -> {
      int currentRow = rowCounter.getAndIncrement();
      return currentRow < rowCount;
    });

    // Set up mock data for each cell
    for (int col = 1; col <= columnCount; col++) {
      final int currentCol = col;
      when(mockResultSet.getObject(eq(currentCol))).thenAnswer(invocation -> {
        // The row counter is already incremented by next(), so we need current-1
        int currentRow = rowCounter.get() - 1;
        return "value-" + currentRow + "-" + currentCol;
      });
    }

    return mockResultSet;
  }
}
