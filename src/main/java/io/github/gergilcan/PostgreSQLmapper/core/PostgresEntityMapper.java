package io.github.gergilcan.PostgreSQLmapper.core;

import java.sql.ResultSet;

import org.postgresql.jdbc.PgArray;
import org.postgresql.util.PGobject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * A custom JSON mapper that provides serialization and deserialization
 * functionality for JSON objects.
 */
public class PostgresEntityMapper {
  ObjectWriter writer;
  ObjectMapper mapper;

  public PostgresEntityMapper() {
    this.mapper = new JsonMapper();
    SimpleModule module = new SimpleModule();
    module.addSerializer(PgArray.class, new PgArraySerializer());
    module.addSerializer(PGobject.class, new PgObjectSerializer());
    module.addSerializer(ResultSet.class, new ResultSetSerializer());
    mapper.registerModule(module);
  }

  /**
   * @param <T>
   * @param fromValue   The object to map from(this can be a result set, a string,
   *                    etc.)
   * @param toValueType The class to map the result
   * @return The mapped object
   */
  public <T> T map(Object fromValue, Class<T> toValueType) {
    var stringValue = this.writeValueAsString(fromValue);
    return this.readValue(stringValue, toValueType);
  }

  /**
   * @param value The object to serialize
   * @return The serialized object as a string
   */
  public String writeValueAsString(Object value) {
    try {
      return mapper.writeValueAsString(value);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * @param <T>       The type of the object to deserialize
   * @param content   The content to deserialize
   * @param valueType The class to deserialize the content to
   * @return The deserialized object
   */
  public <T> T readValue(String content, Class<T> valueType) {
    try {
      return mapper.readValue(content, valueType);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
