package com.yosep.restful.events;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.yosep.restful.events.Event;

public class EventTest {
	@Test
	public void builder() {
		Event event = Event.builder().name("Inflearn Spring REST API").description("REST API development with Spring")
				.build();
		assertThat(event).isNotNull();
	}

	@Test
	public void javaBean() {
		// Given
		String name = "Event";
		String description = "Spring";

		// When
		Event event = new Event();
		event.setName(name);
		event.setDescription(description);

		// Then
		assertThat(event.getName()).isEqualTo(name);
		assertThat(event.getDescription()).isEqualTo(description);
	}

	@Test
	public void testFree() {
		// Given
		Event event = Event.builder().basePrice(0).maxPrice(0).build();

		// When
		event.update();

		// Then
		assertThat(event.isFree()).isTrue();

		// Given
		event = Event.builder().basePrice(100).maxPrice(0).build();

		// When
		event.update();

		// Then
		assertThat(event.isFree()).isFalse();
	}

	@Test
	public void testOffline() {
		// Given
		Event event = Event.builder().location("강남역 네이버 D2 스타트업 팩토리").build();

		// When
		event.update();

		// Then
		assertThat(event.isOffline()).isTrue();

		// Given
		event = Event.builder().build();

		// When
		event.update();

		// Then
		assertThat(event.isOffline()).isFalse();
	}
}
