# Project Name

PostgreSQL-mapper

## Table of Contents

- [Project Name](#project-name)
  - [Table of Contents](#table-of-contents)
  - [Introduction](#introduction)
  - [Usage](#usage)
  - [Integration](#integration)
  - [Performance](#performance)
  - [Contributing](#contributing)
  - [License](#license)

## Introduction

The purpose of this project is to be able to map to an Entity class from a result set coming from a PostgreSQL database wihtout the hurdle of having an ORM.

## Usage

To use it you just need to create an instance of the PostgresEntityMapper. This class can be a singleton if you want due that its an stateless class.
Then just call the map method passing a result set coming from a query and the second parameter needed is the class of each entity. This will return you the correctly formed
entity.
The entities can contain other entities, so you can have a tree of entities. The mapper will take care of that too.

## Integration

You just need to add the following dependency to the pom.xml file:

```xml
<dependency>
  <groupId>io.github.gergilcan</groupId>
  <artifactId>PostgreSQL-mapper</artifactId>
  <version>0.0.6</version>
</dependency>
```

## Performance

After the 0.0.6 version, the library has been optimized to use a direct mapping approach, which significantly improves performance when mapping ResultSet objects to Java objects. This is achieved by eliminating the intermediate string representation that was previously used in the mapping process.

This direct mapping approach reduces processing time and memory usage, especially for large result sets, making it a more efficient solution for mapping database results to Java entities.

This improvement was of more of a 90% in performance, so it is highly recommended to use the latest version of the library for optimal performance.

The current performance times are for the result set mapping to a list of entities of complex objects with more than 10 fields and 5 lists, maps, and other complex objects:
-- For list ---
Direct mapping 10 rows for list took 2 ms
Direct mapping 100 rows for list took 12 ms
Direct mapping 1000 rows for list took 84 ms
Direct mapping 5000 rows for list took 557 ms
Direct mapping 100000 rows for list took 9259 ms

-- For array ---
Direct mapping 10 rows for array took 50 ms
Direct mapping 100 rows for array took 38 ms
Direct mapping 1000 rows for array took 144 ms
Direct mapping 5000 rows for array took 605 ms
Direct mapping 100000 rows for array took 9589 ms

This performance data shows that the library is capable of handling large datasets efficiently, making it suitable for applications that require high-performance database interactions.

You can see that the array mapping is slower than the list mapping, this is because the array mapping needs to convert the array to a list first, so it is not recommended to use arrays in the database if you want to use this library. This is due we dont know the resultset size, so we need to convert the array to a list first, which adds some overhead.

## Contributing

Anyone is able to contribute, feel free to do it to increase its scope or to fix issues. If applicable, provide guidelines for contributing to the project. Include information on how to report issues, submit feature requests, or contribute code.

## License

This library is free to use in any of your personal or commercial projects, just include a mention.

```

```
