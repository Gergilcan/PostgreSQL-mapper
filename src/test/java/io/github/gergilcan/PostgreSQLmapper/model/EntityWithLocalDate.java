package io.github.gergilcan.PostgreSQLmapper.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityWithLocalDate {
  private String name;
  private LocalDate birthDate;
}
