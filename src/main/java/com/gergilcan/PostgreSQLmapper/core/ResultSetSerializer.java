package com.gergilcan.PostgreSQLmapper.core;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Serializes a ResultSet object into a JSON array.
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
    var list = new ArrayList<Object>();
    try {
      while (rs.next()) {
        list.add(parseObject(rs));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    gen.writeStartArray();
    for (Object object : list) {
      gen.writeObject(object);
    }
    gen.writeEndArray();
  }

  /**
   * Parses a single row of the ResultSet object into a HashMap.
   *
   * @param rs The ResultSet object to be parsed.
   * @return A HashMap representing a single row of the ResultSet.
   */
  private Object parseObject(ResultSet rs) {
    var map = new HashMap<String, Object>();
    try {
      var metaData = rs.getMetaData();
      for (var i = 1; i <= metaData.getColumnCount(); i++) {
        var columnName = metaData.getColumnName(i);
        var columnValue = rs.getObject(i);
        map.put(columnName, columnValue);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return map;
  }
}
