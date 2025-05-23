package io.github.gergilcan.PostgreSQLmapper.core;

import java.io.IOException;

import org.postgresql.util.PGobject;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Serializes PostgreSQL objects to JSON format.
 * This class extends the JsonSerializer class and provides custom serialization
 * logic for PostgreSQL objects.
 */
public class PgObjectSerializer extends JsonSerializer<Object> {
  /**
   * Serializes the given object value into JSON using the provided JsonGenerator.
   * If the value is an instance of PGobject, it checks the type of the PGobject
   * and writes the value accordingly. If the type is "json" or "jsonb", it writes the
   * raw value. Otherwise, it writes the value as a string. If the value is not an
   * instance of PGobject, it uses the provided SerializerProvider to serialize the value.
   *
   * @param value       The object value to be serialized.
   * @param gen         The JsonGenerator used to write the JSON.
   * @param serializers The SerializerProvider used for serialization.
   * @throws IOException If an I/O error occurs during serialization.
   */
  @Override
  public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
    if (value == null) {
      gen.writeNull();
      return;
    }
    
    if (value instanceof PGobject) {
      PGobject pgObject = (PGobject) value;
      String pgValue = pgObject.getValue();
      
      // Handle null values in PGobject
      if (pgValue == null) {
        gen.writeNull();
        return;
      }
      
      switch (pgObject.getType()) {
        case "json":
        case "jsonb":
          // Safely handle empty JSON strings
          if (pgValue.trim().isEmpty()) {
            gen.writeRawValue("{}");
          } else {
            gen.writeRawValue(pgValue);
          }
          break;
        default:
          gen.writeString(pgValue);
      }
    } else {
      // Use the provided SerializerProvider instead of creating a new ObjectMapper
      serializers.defaultSerializeValue(value, gen);
    }
  }
}
