package io.github.gergilcan.PostgreSQLmapper.core;

import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A specialized mapper that converts ResultSet objects directly to Java objects
 * without the intermediate string representation.
 * This improves performance by eliminating the unnecessary
 * serialization/deserialization steps.
 */
public class DirectResultSetMapper {

  private final ObjectMapper mapper;

  /**
   * Creates a new DirectResultSetMapper using the provided ObjectMapper
   * 
   * @param mapper The ObjectMapper to use for complex object conversion
   */
  public DirectResultSetMapper(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  /**
   * Maps a ResultSet directly to the target type without intermediate JSON
   * serialization
   * 
   * @param <T>        The target type
   * @param resultSet  The ResultSet to map
   * @param targetType The class of the target type
   * @return An object of the target type
   * @throws SQLException If there is an error accessing the ResultSet
   */
  @SuppressWarnings("unchecked")
  public <T> T mapResultSet(ResultSet resultSet, Class<T> targetType) throws SQLException {
    if (resultSet == null) {
      return null;
    }

    // For array type, we're expecting multiple rows
    if (targetType.isArray()) {
      return (T) convertToArray(resultSet, targetType.getComponentType());
    }

    // For List type, we're expecting multiple rows
    if (List.class.isAssignableFrom(targetType)) {
      return (T) convertToList(resultSet);
    }

    // For Map or a single entity, we're expecting a single row
    if (Map.class.isAssignableFrom(targetType)) {
      return (T) convertToMap(resultSet);
    }

    // For a specific entity type, we're expecting a single row
    if (resultSet.next()) {
      String[] columnNames = columnNamesFrom(resultSet.getMetaData());
      Map<String, Object> rowMap = extractRowValues(resultSet, columnNames);
      try {
        // Convert the map to the target entity type
        return mapper.convertValue(rowMap, targetType);
      } catch (Exception e) {
        throw new SQLException("Failed to convert ResultSet to " + targetType.getName(), e);
      }
    }

    return null;
  }

  private <T> T[] convertToArray(ResultSet resultSet, Class<T> targetType) throws SQLException {
    List<T> list = new ArrayList<>();
    try {
      String[] columnNames = columnNamesFrom(resultSet.getMetaData());
      while (resultSet.next()) {
        Map<String, Object> row = extractRowValues(resultSet, columnNames);
        T item = mapper.convertValue(row, targetType);
        list.add(item);
      }

      // Convert list to array
      @SuppressWarnings("unchecked")
      T[] array = (T[]) java.lang.reflect.Array.newInstance(targetType, list.size());
      return list.toArray(array);
    } catch (Exception e) {
      throw new SQLException("Failed to convert ResultSet to " + targetType.getName() + " array", e);
    }
  }

  /**
   * Converts a ResultSet to a List of Maps, where each Map represents a row
   * 
   * @param resultSet The ResultSet to convert
   * @return A List of Maps, where each Map contains column name to value mappings
   * @throws SQLException If there is an error accessing the ResultSet
   */
  private List<Map<String, Object>> convertToList(ResultSet resultSet) throws SQLException {
    List<Map<String, Object>> results = new ArrayList<>();

    String[] columnNames = columnNamesFrom(resultSet.getMetaData());
    while (resultSet.next()) {
      Map<String, Object> row = extractRowValues(resultSet, columnNames);
      results.add(row);
    }

    return results;
  }

  /**
   * Converts a ResultSet to a Map, assuming the ResultSet has a single row
   * 
   * @param resultSet The ResultSet to convert
   * @return A Map containing column name to value mappings
   * @throws SQLException If there is an error accessing the ResultSet
   */
  private Map<String, Object> convertToMap(ResultSet resultSet) throws SQLException {
    if (resultSet.next()) {
      String[] columnNames = columnNamesFrom(resultSet.getMetaData());
      return extractRowValues(resultSet, columnNames);
    }
    return new HashMap<>();
  }

  private static String[] columnNamesFrom(ResultSetMetaData metaData) throws SQLException {
    int columnCount = metaData.getColumnCount();
    String[] names = new String[columnCount];
    for (int i = 1; i <= columnCount; i++) {
      names[i - 1] = metaData.getColumnName(i);
    }
    return names;
  }

  /**
   * Extracts values from the current row of a ResultSet into a Map
   *
   * @param resultSet   The ResultSet to extract values from
   * @param columnNames Column names from {@link #columnNamesFrom(ResultSetMetaData)}; reused across
   *                    rows for the same ResultSet
   * @return A Map containing column name to value mappings
   * @throws SQLException If there is an error accessing the ResultSet
   */
  private Map<String, Object> extractRowValues(ResultSet resultSet, String[] columnNames) throws SQLException {
    int columnCount = columnNames.length;
    Map<String, Object> row = new HashMap<>((int) (columnCount / 0.75f) + 1);

    for (int i = 0; i < columnCount; i++) {
      String columnName = columnNames[i];
      int jdbcColumn = i + 1;
      Object value = resultSet.getObject(jdbcColumn);

      // Handle PostgreSQL specific types if needed
      if (value instanceof org.postgresql.jdbc.PgArray) {
        try {
          value = ((org.postgresql.jdbc.PgArray) value).getArray();
        } catch (SQLException e) {
          // If we can't get the array, use the original value
        }
      } else if (value instanceof org.postgresql.util.PGobject) {
        org.postgresql.util.PGobject pgObject = (org.postgresql.util.PGobject) value;
        String pgValue = pgObject.getValue();

        if (pgValue != null) {
          if ("json".equals(pgObject.getType()) || "jsonb".equals(pgObject.getType())) {
            try {
              // Parse JSON string to Java object
              value = mapper.readValue(pgValue, Object.class);
            } catch (IOException e) {
              // If we can't parse the JSON, use the original string value
              value = pgValue;
            }
          } else if ("date".equals(pgObject.getType())) {
            value = LocalDate.parse(pgValue);
          } else if ("timestamp".equals(pgObject.getType()) || "timestamptz".equals(pgObject.getType())) {
            value = parsePgTimestampText(pgValue);
          } else {
            value = pgValue;
          }
        }
      } else if (value instanceof Timestamp) {
        value = ((Timestamp) value).toLocalDateTime();
      } else if (value instanceof OffsetDateTime) {
        value = ((OffsetDateTime) value).toLocalDateTime();
      } else if (value instanceof Date) {
        value = ((Date) value).toLocalDate();
      }

      row.put(columnName, value);
    }

    return row;
  }

  /**
   * Parses PostgreSQL {@code timestamp} / {@code timestamptz} text as {@link LocalDateTime}
   * (instant-based values use the offset then drop the zone).
   */
  private static LocalDateTime parsePgTimestampText(String pgValue) {
    String s = pgValue.trim();
    String normalized = s.length() > 10 && s.charAt(10) == ' '
        ? s.substring(0, 10) + 'T' + s.substring(11)
        : s;
    try {
      return OffsetDateTime.parse(normalized).toLocalDateTime();
    } catch (DateTimeParseException ignored) {
      // not an offset datetime
    }
    try {
      return LocalDateTime.parse(normalized);
    } catch (DateTimeParseException ignored) {
      // not ISO local
    }
    return Timestamp.valueOf(s).toLocalDateTime();
  }
}
