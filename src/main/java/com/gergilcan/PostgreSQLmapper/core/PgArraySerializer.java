package com.gergilcan.PostgreSQLmapper.core;

import java.io.IOException;
import java.sql.SQLException;

import org.postgresql.jdbc.PgArray;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * Serializes a PgArray object into JSON format.
 * This class extends the StdSerializer class and provides custom serialization
 * logic for PgArray objects.
 */
public class PgArraySerializer extends StdSerializer<PgArray> {

  public PgArraySerializer() {
    this(null);
  }

  public PgArraySerializer(Class<PgArray> t) {
    super(t);
  }

  @Override
  public void serialize(
      PgArray value, JsonGenerator jgen, SerializerProvider provider)
      throws IOException {
    try {
      Object[] array = (Object[]) value.getArray();
      jgen.writeStartArray();
      for (int i = 0; i < array.length; i++) {
        jgen.writeObject(array[i]);
      }
      jgen.writeEndArray();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}