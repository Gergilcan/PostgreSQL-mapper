package io.github.gergilcan.PostgreSQLmapper.core;

import java.io.IOException;
import java.sql.SQLException;

import org.postgresql.jdbc.PgArray;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * Serializes a PgArray object into JSON format.
 * This class extends the StdSerializer class and provides custom serialization
 * logic for PgArray objects. Optimized for performance and proper exception
 * handling.
 */
public class PgArraySerializer extends StdSerializer<PgArray> {

  public PgArraySerializer() {
    this(null);
  }

  public PgArraySerializer(Class<PgArray> t) {
    super(t);
  }

  /**
   * Serializes a PostgreSQL array into a JSON array.
   *
   * @param value    The PgArray to serialize
   * @param jgen     The JSON generator
   * @param provider The serializer provider
   * @throws IOException If an I/O error occurs or if the array cannot be accessed
   */
  @Override
  public void serialize(
      PgArray value, JsonGenerator jgen, SerializerProvider provider)
      throws IOException {
    try {
      // Get array elements efficiently
      Object[] array = (Object[]) value.getArray();

      // Use non-deprecated approach to write the array
      jgen.writeStartArray();

      // Directly write array elements
      for (Object element : array) {
        jgen.writeObject(element);
      }

      jgen.writeEndArray();
    } catch (SQLException e) {
      // Wrap SQL exceptions in IOException with meaningful message instead of just
      // printing stack trace
      throw new IOException("Failed to serialize PostgreSQL array: " + e.getMessage(), e);
    }
  }
}