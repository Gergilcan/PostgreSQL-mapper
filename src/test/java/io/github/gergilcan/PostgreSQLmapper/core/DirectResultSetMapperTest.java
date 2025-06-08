package io.github.gergilcan.PostgreSQLmapper.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.gergilcan.PostgreSQLmapper.helpers.ComplexEntityHelper;
import io.github.gergilcan.PostgreSQLmapper.model.ComplexEntity;

@DisplayName("Direct ResultSet Mapping Performance Tests")
public class DirectResultSetMapperTest {

  private PostgresEntityMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new PostgresEntityMapper();
  }

  @DisplayName("Performance test comparing direct vs. indirect mapping with different row counts")
  @ParameterizedTest(name = "Mapping {0} rows")
  @ValueSource(ints = { 10, 100, 1000, 5000, 100000 })
  void performanceTestDirectMappingWithArray(int rowCount) throws SQLException {
    // Create a mock ResultSet with the specified number of rows
    ResultSet mockResultSet = ComplexEntityHelper.createComplexEntityResultSet(rowCount);

    // Test direct mapping (via intermediate string)
    long startTimeDirect = System.nanoTime();
    var entitiesDirect = mapper.map(mockResultSet, ComplexEntity[].class);
    long endTimeDirect = System.nanoTime();
    long durationDirectMs = TimeUnit.NANOSECONDS.toMillis(endTimeDirect - startTimeDirect);

    // Verify results
    assertNotNull(entitiesDirect);
    assertEquals(rowCount, entitiesDirect.length);

    System.out.printf("Direct mapping %d rows for array took %d ms%n", rowCount, durationDirectMs);
  }

  @SuppressWarnings("unchecked")
  @DisplayName("Performance test comparing direct vs. indirect mapping with different row counts")
  @ParameterizedTest(name = "Mapping {0} rows")
  @ValueSource(ints = { 10, 100, 1000, 5000, 100000 })
  void performanceTestDirectMappingWithList(int rowCount) throws SQLException {
    // Create a mock ResultSet with the specified number of rows
    ResultSet mockResultSet = ComplexEntityHelper.createComplexEntityResultSet(rowCount);

    // Test direct mapping (via intermediate string)
    long startTimeDirect = System.nanoTime();
    List<ComplexEntity> entitiesDirect = mapper.map(mockResultSet, List.class);
    long endTimeDirect = System.nanoTime();
    long durationDirectMs = TimeUnit.NANOSECONDS.toMillis(endTimeDirect - startTimeDirect);

    // Verify results
    assertNotNull(entitiesDirect);
    assertEquals(rowCount, entitiesDirect.size());

    System.out.printf("Direct mapping %d rows for list took %d ms%n", rowCount, durationDirectMs);
  }
}
