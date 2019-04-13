package com.example.hibernatesqlservertestcase;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.TimeZone;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class EntityWithOffsetTimestampTests {
    @ClassRule
    public static final GenericContainer sqlServer;

    private static final TimeZone defaultTimeZone = TimeZone.getDefault();

    private static final Logger log = LoggerFactory.getLogger(EntityWithOffsetTimestampTests.class);

    static {
        sqlServer = new GenericContainer<>("mcr.microsoft.com/mssql/server:2017-latest-ubuntu")
                .withEnv("ACCEPT_EULA", "y")
                .withEnv("SA_PASSWORD", "secr3t?!");
        sqlServer.setPortBindings(singletonList("1433:1433"));
    }

    @Autowired
    private TestEntityManager entityManager;

    @Before
    public void setUp() {
        // force JVM time zone to UTC for predictable results regardless of system
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("UTC")));
    }

    @After
    public void cleanUp() {
        TimeZone.setDefault(defaultTimeZone);
    }

    @Test
    public void contextLoads() {
        assertThat(entityManager).isNotNull();
    }

    // succeeds
    @Test
    public void persistsUtcDateTimeCorrectly() {
        OffsetDateTime now = OffsetDateTime.now(Clock.fixed(Instant.EPOCH, ZoneId.of("UTC")));
        EntityWithOffsetTimestamp entity = new EntityWithOffsetTimestamp(now);
        entityManager.persist(entity);
        entityManager.refresh(entity);
        assertThat(entity.getCreatedAt()).isEqualTo(now);
    }

    // fails
    @Test
    public void persistsNonUtcDateTimeCorrectly() {
        OffsetDateTime now = OffsetDateTime.now(Clock.fixed(Instant.EPOCH, ZoneId.of("CET")));
        EntityWithOffsetTimestamp entity = new EntityWithOffsetTimestamp(now);
        entityManager.persist(entity);
        entityManager.refresh(entity);
        assertThat(entity.getCreatedAt()).isEqualTo(now);
    }
}
