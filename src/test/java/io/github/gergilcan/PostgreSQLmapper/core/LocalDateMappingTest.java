package io.github.gergilcan.PostgreSQLmapper.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PGobject;

import io.github.gergilcan.PostgreSQLmapper.model.EntityWithLocalDate;
import io.github.gergilcan.PostgreSQLmapper.model.EntityWithLocalDateTime;

@DisplayName("LocalDate and LocalDateTime mapping from ResultSet")
class LocalDateMappingTest {

  private PostgresEntityMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new PostgresEntityMapper();
  }

  @Test
  @DisplayName("Maps java.sql.Date column to LocalDate")
  void mapsSqlDateToLocalDate() throws SQLException {
    LocalDate expected = LocalDate.of(2024, 6, 15);
    ResultSet rs = mock(ResultSet.class);
    ResultSetMetaData meta = mock(ResultSetMetaData.class);
    when(meta.getColumnCount()).thenReturn(2);
    when(meta.getColumnName(1)).thenReturn("name");
    when(meta.getColumnName(2)).thenReturn("birthDate");
    when(rs.getMetaData()).thenReturn(meta);
    AtomicBoolean done = new AtomicBoolean(false);
    when(rs.next()).thenAnswer(inv -> !done.getAndSet(true));
    when(rs.getObject(1)).thenReturn("Ada");
    when(rs.getObject(2)).thenReturn(Date.valueOf(expected));

    EntityWithLocalDate entity = mapper.map(rs, EntityWithLocalDate.class);

    assertNotNull(entity);
    assertEquals("Ada", entity.getName());
    assertEquals(expected, entity.getBirthDate());
  }

  @Test
  @DisplayName("Maps PostgreSQL date PGobject to LocalDate")
  void mapsPgObjectDateToLocalDate() throws SQLException {
    LocalDate expected = LocalDate.of(2020, 1, 2);
    PGobject pgDate = new PGobject();
    pgDate.setType("date");
    pgDate.setValue(expected.toString());

    ResultSet rs = mock(ResultSet.class);
    ResultSetMetaData meta = mock(ResultSetMetaData.class);
    when(meta.getColumnCount()).thenReturn(2);
    when(meta.getColumnName(1)).thenReturn("name");
    when(meta.getColumnName(2)).thenReturn("birthDate");
    when(rs.getMetaData()).thenReturn(meta);
    AtomicBoolean done = new AtomicBoolean(false);
    when(rs.next()).thenAnswer(inv -> !done.getAndSet(true));
    when(rs.getObject(1)).thenReturn("Bob");
    when(rs.getObject(2)).thenReturn(pgDate);

    EntityWithLocalDate entity = mapper.map(rs, EntityWithLocalDate.class);

    assertNotNull(entity);
    assertEquals("Bob", entity.getName());
    assertEquals(expected, entity.getBirthDate());
  }

  @Test
  @DisplayName("Maps java.sql.Timestamp column to LocalDateTime")
  void mapsSqlTimestampToLocalDateTime() throws SQLException {
    LocalDateTime expected = LocalDateTime.of(2024, 3, 20, 14, 30, 0, 123_000_000);
    ResultSet rs = mock(ResultSet.class);
    ResultSetMetaData meta = mock(ResultSetMetaData.class);
    when(meta.getColumnCount()).thenReturn(2);
    when(meta.getColumnName(1)).thenReturn("name");
    when(meta.getColumnName(2)).thenReturn("seenAt");
    when(rs.getMetaData()).thenReturn(meta);
    AtomicBoolean done = new AtomicBoolean(false);
    when(rs.next()).thenAnswer(inv -> !done.getAndSet(true));
    when(rs.getObject(1)).thenReturn("Test");
    when(rs.getObject(2)).thenReturn(Timestamp.valueOf(expected));

    EntityWithLocalDateTime entity = mapper.map(rs, EntityWithLocalDateTime.class);

    assertNotNull(entity);
    assertEquals("Test", entity.getName());
    assertEquals(expected, entity.getSeenAt());
  }

  @Test
  @DisplayName("Maps PostgreSQL timestamp PGobject to LocalDateTime")
  void mapsPgObjectTimestampToLocalDateTime() throws SQLException {
    LocalDateTime expected = LocalDateTime.of(2021, 7, 8, 9, 1, 2);
    PGobject pgTs = new PGobject();
    pgTs.setType("timestamp");
    pgTs.setValue("2021-07-08 09:01:02");

    ResultSet rs = mock(ResultSet.class);
    ResultSetMetaData meta = mock(ResultSetMetaData.class);
    when(meta.getColumnCount()).thenReturn(2);
    when(meta.getColumnName(1)).thenReturn("name");
    when(meta.getColumnName(2)).thenReturn("seenAt");
    when(rs.getMetaData()).thenReturn(meta);
    AtomicBoolean done = new AtomicBoolean(false);
    when(rs.next()).thenAnswer(inv -> !done.getAndSet(true));
    when(rs.getObject(1)).thenReturn("Pg");
    when(rs.getObject(2)).thenReturn(pgTs);

    EntityWithLocalDateTime entity = mapper.map(rs, EntityWithLocalDateTime.class);

    assertNotNull(entity);
    assertEquals(expected, entity.getSeenAt());
  }

  @Test
  @DisplayName("Maps java.time.OffsetDateTime column to LocalDateTime")
  void mapsOffsetDateTimeToLocalDateTime() throws SQLException {
    LocalDateTime expected = LocalDateTime.of(2022, 1, 1, 12, 0);
    OffsetDateTime fromDriver = OffsetDateTime.of(expected, ZoneOffset.ofHours(3));

    ResultSet rs = mock(ResultSet.class);
    ResultSetMetaData meta = mock(ResultSetMetaData.class);
    when(meta.getColumnCount()).thenReturn(2);
    when(meta.getColumnName(1)).thenReturn("name");
    when(meta.getColumnName(2)).thenReturn("seenAt");
    when(rs.getMetaData()).thenReturn(meta);
    AtomicBoolean done = new AtomicBoolean(false);
    when(rs.next()).thenAnswer(inv -> !done.getAndSet(true));
    when(rs.getObject(1)).thenReturn("Tz");
    when(rs.getObject(2)).thenReturn(fromDriver);

    EntityWithLocalDateTime entity = mapper.map(rs, EntityWithLocalDateTime.class);

    assertNotNull(entity);
    assertEquals(expected, entity.getSeenAt());
  }
}
