package com.gossamercms.outbox.services;

import com.gossamercms.outbox.data.OutboxDbService;
import com.gossamercms.outbox.dtos.OutboxEventDto;
import com.gossamercms.outbox.eventbus.DomainEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class OutboxServiceTest {

    @Test
    void publishesUsingBootAutoConfiguredJackson3Mapper() {
        OutboxDbService dbService = mock(OutboxDbService.class);
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(JacksonAutoConfiguration.class))
                .withBean(OutboxDbService.class, () -> dbService)
                .withUserConfiguration(OutboxService.class)
                .run(context -> {
                    assertThat(context).hasNotFailed().hasSingleBean(ObjectMapper.class);

                    UUID aggregateId = UUID.randomUUID();
                    TestEvent event = new TestEvent("created", Instant.parse("2026-11-03T19:00:00Z"));
                    context.getBean(OutboxService.class).publish("test", aggregateId, event);

                    ArgumentCaptor<OutboxEventDto> captor = ArgumentCaptor.forClass(OutboxEventDto.class);
                    verify(dbService).create(isNull(), captor.capture());
                    OutboxEventDto saved = captor.getValue();
                    assertThat(saved.getAggregateId()).isEqualTo(aggregateId);
                    assertThat(saved.getEventType()).isEqualTo(TestEvent.class.getName());
                    assertThat(saved.getStatus()).isEqualTo("PENDING");
                    ObjectMapper mapper = context.getBean(ObjectMapper.class);
                    assertThat(mapper.readTree(saved.getPayload()).get("name").asString()).isEqualTo("created");
                    assertThat(mapper.readValue(saved.getPayload(), TestEvent.class)).isEqualTo(event);
                });
    }

    public record TestEvent(String name, Instant occurredAt) implements DomainEvent {}
}
