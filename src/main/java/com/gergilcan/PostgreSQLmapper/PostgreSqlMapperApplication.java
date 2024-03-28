package com.gergilcan.PostgreSQLmapper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//Exclude jdbc autoconfiguration
/**
 * This is the main class for the PostgreSQL Mapper application.
 * It is responsible for starting the application and running the Spring Boot
 * framework.
 *
 * @param args The command line arguments.
 */
@SpringBootApplication(exclude = org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class)
public class PostgreSqlMapperApplication {

  /**
   * @param args The command line arguments.
   */
  public static void main(String[] args) {
    SpringApplication.run(PostgreSqlMapperApplication.class, args);
  }

}
