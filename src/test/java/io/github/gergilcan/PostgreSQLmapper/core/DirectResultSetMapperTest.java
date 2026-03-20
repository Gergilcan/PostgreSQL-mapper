package io.github.gergilcan.PostgreSQLmapper.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.gergilcan.PostgreSQLmapper.helpers.ComplexEntityHelper;
import io.github.gergilcan.PostgreSQLmapper.model.ComplexEntity;

/**
 * Micro-benchmark style tests: JVM warmup plus several timed runs so console numbers are
 * comparable across commits (median / min / max ms per configuration).
 */
@DisplayName("Direct ResultSet mapping performance")
public class DirectResultSetMapperTest {

  /** Runs discarded so JIT and caches stabilize before measured runs. */
  private static final int WARMUP_ITERATIONS = 2;

  private PostgresEntityMapper mapper;

  @BeforeAll
  static void printEnvironment() {
    System.out.println("Perf JVM: " + System.getProperty("java.version") + " ("
        + System.getProperty("java.vm.name") + ")");
  }

  @BeforeEach
  void setUp() {
    mapper = new PostgresEntityMapper();
  }

  /** Large row counts cost more wall time; fewer timed samples still give a stable median. */
  private static int timedRunCount(int rowCount) {
    return rowCount >= 50_000 ? 3 : 5;
  }

  private static long median(long[] sortedMs) {
    int n = sortedMs.length;
    return n % 2 == 1 ? sortedMs[n / 2] : (sortedMs[n / 2 - 1] + sortedMs[n / 2]) / 2;
  }

  private static void printLine(String target, int rowCount, int runs, long[] samplesMs) {
    Arrays.sort(samplesMs);
    long med = median(samplesMs);
    System.out.printf(
        "rows=%7d target=%-5s runs=%d median=%4d ms  min=%4d  max=%4d%n",
        rowCount, target, runs, med, samplesMs[0], samplesMs[samplesMs.length - 1]);
  }

  @DisplayName("Array target: warmup + repeated timed runs")
  @ParameterizedTest(name = "{0} rows")
  @ValueSource(ints = { 10, 100, 1000, 5000, 100000 })
  void performanceArrayTarget(int rowCount) throws SQLException {
    for (int w = 0; w < WARMUP_ITERATIONS; w++) {
      ResultSet rs = ComplexEntityHelper.createComplexEntityResultSet(rowCount);
      ComplexEntity[] r = mapper.map(rs, ComplexEntity[].class);
      assertEquals(rowCount, r.length);
    }

    int runs = timedRunCount(rowCount);
    long[] samplesMs = new long[runs];
    for (int i = 0; i < runs; i++) {
      ResultSet rs = ComplexEntityHelper.createComplexEntityResultSet(rowCount);
      long t0 = System.nanoTime();
      ComplexEntity[] r = mapper.map(rs, ComplexEntity[].class);
      samplesMs[i] = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - t0);
      assertNotNull(r);
      assertEquals(rowCount, r.length);
    }
    printLine("array", rowCount, runs, samplesMs);
  }

  @SuppressWarnings("unchecked")
  @DisplayName("List target: warmup + repeated timed runs")
  @ParameterizedTest(name = "{0} rows")
  @ValueSource(ints = { 10, 100, 1000, 5000, 100000 })
  void performanceListTarget(int rowCount) throws SQLException {
    for (int w = 0; w < WARMUP_ITERATIONS; w++) {
      ResultSet rs = ComplexEntityHelper.createComplexEntityResultSet(rowCount);
      List<ComplexEntity> r = mapper.map(rs, List.class);
      assertEquals(rowCount, r.size());
    }

    int runs = timedRunCount(rowCount);
    long[] samplesMs = new long[runs];
    for (int i = 0; i < runs; i++) {
      ResultSet rs = ComplexEntityHelper.createComplexEntityResultSet(rowCount);
      long t0 = System.nanoTime();
      List<ComplexEntity> r = mapper.map(rs, List.class);
      samplesMs[i] = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - t0);
      assertNotNull(r);
      assertEquals(rowCount, r.size());
    }
    printLine("list", rowCount, runs, samplesMs);
  }
}
