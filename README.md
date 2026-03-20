# PostgreSQL-mapper

## Table of Contents

- [Introduction](#introduction)
- [Usage](#usage)
- [Integration](#integration)
- [Performance](#performance)
- [Contributing](#contributing)
- [License](#license)

## Introduction

This project maps rows from a PostgreSQL `ResultSet` into Java entity types **without** a full ORM.

## Usage

Create an instance of `PostgresEntityMapper` (it is safe to use as a singleton). Call `map(resultSet, YourClass.class)` (or an array / `List` type where supported). Nested types and PostgreSQL `json` / `jsonb` fields are handled via Jackson.

`java.time` types such as `LocalDate` and `LocalDateTime` are supported when reading JDBC / PostgreSQL date and timestamp values.

## Integration

Add the dependency to your `pom.xml` (use the latest released version):

```xml
<dependency>
  <groupId>io.github.gergilcan</groupId>
  <artifactId>PostgreSQL-mapper</artifactId>
  <version>0.1.1</version>
</dependency>
```

## Performance

### Direct mapping

For `ResultSet` inputs, `PostgresEntityMapper` uses `DirectResultSetMapper`: values are put into a per-row `Map` and converted with Jackson’s `convertValue`, **without** round-tripping through JSON strings. That path is much faster than serializing the whole `ResultSet` to JSON and parsing it again.

Row extraction also **caches column names once per scan** (one metadata pass for all rows in that mapping) and builds row maps with an initial capacity suited to the column count, which reduces overhead on large result sets without changing behaviour.

### Benchmarks in this repo

Performance is exercised by JVM tests (timings are indicative and vary by CPU, JDK, and load):

| Test | What it measures |
|------|------------------|
| [`DirectResultSetMapperTest`](src/test/java/io/github/gergilcan/PostgreSQLmapper/core/DirectResultSetMapperTest.java) | `PostgresEntityMapper.map` on a mock `ResultSet` from [`ComplexEntityHelper`](src/test/java/io/github/gergilcan/PostgreSQLmapper/helpers/ComplexEntityHelper.java): nested entity with many columns and JSONB fields. Row counts: `10`, `100`, `1000`, `5000`, `100000`. After **2 warmup** iterations, prints **median / min / max** ms over **5** timed runs (**3** for `100000` rows) for **`List`** vs **`ComplexEntity[]`**. |
| [`ResultSetSerializerPerformanceTest`](src/test/java/io/github/gergilcan/PostgreSQLmapper/core/ResultSetSerializerPerformanceTest.java) | Jackson serialization of a `ResultSet` through [`ResultSetSerializer`](src/main/java/io/github/gergilcan/PostgreSQLmapper/core/ResultSetSerializer.java) (warm-up + varied row/column counts). |

**Reference run** (`DirectResultSetMapperTest` console output): **Java 21.0.10** (OpenJDK 64-Bit Server VM). Figures are **milliseconds**; min–max spans the timed samples for that row count.

| Rows | `List` median | List min–max | `ComplexEntity[]` median | Array min–max |
|-----:|-------------:|-------------:|-------------------------:|--------------:|
| 10 | 1 | 1–1 | 5 | 4–7 |
| 100 | 7 | 7–30 | 10 | 9–27 |
| 1,000 | 69 | 46–92 | 112 | 89–814 |
| 5,000 | 263 | 258–491 | 301 | 274–434 |
| 100,000 | 6,819 | 6,197–8,652 | 8,579 | 7,499–10,666 |

Run the performance-related tests and copy timings from the console:

```bash
mvn test -Dtest=DirectResultSetMapperTest,ResultSetSerializerPerformanceTest
```

### List vs array targets

Mapping to a **Java array** still collects entities in an `ArrayList` first (the JDBC API does not provide the final row count up front), then copies into an array. Prefer **`List<YourEntity>`** (or `List.class` where applicable) when you want the lowest overhead for many rows.

## Contributing

Contributions are welcome: issues, fixes, and extensions to supported types or mapping modes.

## License

This library is free to use in personal or commercial projects; please include a mention.
