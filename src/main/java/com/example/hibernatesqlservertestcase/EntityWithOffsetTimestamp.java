package com.example.hibernatesqlservertestcase;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.OffsetDateTime;

@Entity
public class EntityWithOffsetTimestamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private OffsetDateTime createdAt;

    private EntityWithOffsetTimestamp() {
    }

    public EntityWithOffsetTimestamp(OffsetDateTime currentTime) {
        this.createdAt = currentTime;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
