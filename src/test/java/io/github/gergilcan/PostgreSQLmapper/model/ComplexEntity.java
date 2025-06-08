package io.github.gergilcan.PostgreSQLmapper.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A complex entity class for testing deserialization performance.
 * This class contains a variety of field types to test the mapper's performance
 * with complex data structures.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplexEntity {
  private UUID id;
  private String name;
  private Integer age;
  private Double salary;
  @JsonAlias("account_balance")
  private BigDecimal accountBalance;
  private Boolean active;
  @JsonAlias("created_at")
  private Timestamp createdAt;
  @JsonAlias("updated_at")
  private Timestamp updatedAt;
  private List<String> tags;
  private Map<String, Object> metadata;
  private Address address;
  @JsonAlias("contact_info")
  private List<ContactInfo> contactInfo;
  @JsonAlias("employment_details")
  private EmploymentDetails employmentDetails;

  /**
   * Nested address class
   */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Address {
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private GeoLocation location;
  }

  /**
   * Nested geolocation class
   */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class GeoLocation {
    private Double latitude;
    private Double longitude;
  }

  /**
   * Nested contact info class
   */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ContactInfo {
    private String type;
    private String value;
    private Boolean isPrimary;
  }

  /**
   * Nested employment details class
   */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class EmploymentDetails {
    private String department;
    private String position;
    private Integer yearsOfService;
    private List<Project> projects;
    private Map<String, String> skills;
  }

  /**
   * Nested project class
   */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Project {
    private String name;
    private String description;
    private Timestamp startDate;
    private Timestamp endDate;
    private ProjectStatus status;
  }

  /**
   * Project status enum
   */
  public enum ProjectStatus {
    ACTIVE,
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED,
    ON_HOLD,
    CANCELLED
  }
}
