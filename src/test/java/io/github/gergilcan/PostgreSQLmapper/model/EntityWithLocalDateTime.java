package io.github.gergilcan.PostgreSQLmapper.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityWithLocalDateTime {
  private String name;
  private LocalDateTime seenAt;
}
