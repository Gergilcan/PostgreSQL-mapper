package io.github.gergilcan.PostgreSQLmapper.core;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Serializes a ResultSet object into a JSON array.
 * Optimized for performance by streaming directly to JSON output.
 */
public class ResultSetSerializer extends JsonSerializer<ResultSet> {
  /**
   * Serializes the ResultSet object into a JSON array.
   *
   * @param rs          The ResultSet object to be serialized.
   * @param gen         The JsonGenerator object to write the JSON output.
   * @param serializers The SerializerProvider object for accessing serializers.
   * @throws IOException If an I/O error occurs during serialization.
   */
  @Override
  public void serialize(ResultSet rs, JsonGenerator gen, SerializerProvider serializers) throws IOException {
    try {
      ResultSetMetaData metaData = rs.getMetaData();
      int columnCount = metaData.getColumnCount();

      // Pre-fetch column names to avoid repeated metadata access
      String[] columnNames = new String[columnCount + 1]; // +1 because JDBC columns are 1-based
      for (int i = 1; i <= columnCount; i++) {
        columnNames[i] = metaData.getColumnName(i);
      }

      gen.writeStartArray();

      // Stream rows directly to JSON output without creating intermediate collections
      while (rs.next()) {
        gen.writeStartObject();
        for (int i = 1; i <= columnCount; i++) {
          Object value = rs.getObject(i);
          gen.writeObjectField(columnNames[i], value);
        }
        gen.writeEndObject();
      }

      gen.writeEndArray();
    } catch (SQLException e) {
      // Wrap SQL exceptions in a more specific exception without stack trace overhead
      throw new IOException("Error serializing ResultSet: " + e.getMessage(), e);
    }
  }
}
