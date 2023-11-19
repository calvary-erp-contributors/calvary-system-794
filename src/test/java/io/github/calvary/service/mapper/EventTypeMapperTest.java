package io.github.calvary.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EventTypeMapperTest {

    private EventTypeMapper eventTypeMapper;

    @BeforeEach
    public void setUp() {
        eventTypeMapper = new EventTypeMapperImpl();
    }
}
