# Project Name

PostgreSQL-mapper

## Table of Contents

- [Project Name](#project-name)
  - [Table of Contents](#table-of-contents)
  - [Introduction](#introduction)
  - [Usage](#usage)
  - [Integration](#integration)
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
  <version>0.0.5</version>
</dependency>
```

## Contributing

Anyone is able to contribute, feel free to do it to increase its scope or to fix issues. If applicable, provide guidelines for contributing to the project. Include information on how to report issues, submit feature requests, or contribute code.

## License

This library is free to use in any of your personal or commercial projects, just include a mention.

```

```
