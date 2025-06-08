package io.github.gergilcan.PostgreSQLmapper.core;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
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
      Map<String, Object> rowMap = extractRowValues(resultSet);
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
      while (resultSet.next()) {
        Map<String, Object> row = extractRowValues(resultSet);
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

    while (resultSet.next()) {
      Map<String, Object> row = extractRowValues(resultSet);
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
      return extractRowValues(resultSet);
    }
    return new HashMap<>();
  }

  /**
   * Extracts values from the current row of a ResultSet into a Map
   * 
   * @param resultSet The ResultSet to extract values from
   * @return A Map containing column name to value mappings
   * @throws SQLException If there is an error accessing the ResultSet
   */
  private Map<String, Object> extractRowValues(ResultSet resultSet) throws SQLException {
    ResultSetMetaData metaData = resultSet.getMetaData();
    int columnCount = metaData.getColumnCount();

    Map<String, Object> row = new HashMap<>();

    for (int i = 1; i <= columnCount; i++) {
      String columnName = metaData.getColumnName(i);
      Object value = resultSet.getObject(i);

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
          } else {
            value = pgValue;
          }
        }
      }

      row.put(columnName, value);
    }

    return row;
  }
}
