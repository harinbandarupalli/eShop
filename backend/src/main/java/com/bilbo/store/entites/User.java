package com.bilbo.store.entites;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "users", schema = "eshop")
public class User {

    @Id
    @Column(length = 255)
    @EqualsAndHashCode.Include
    private String sub;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "created_timestamp", updatable = false)
    private OffsetDateTime createdTimestamp;

    @Column(name = "last_updated_timestamp")
    private OffsetDateTime lastUpdatedTimestamp;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "last_updated_by")
    private String lastUpdatedBy;
}
