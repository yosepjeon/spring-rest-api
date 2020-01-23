package com.yosep.restful.events;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.yosep.restful.events.Event;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

// JUnitParams 파라미터를 사용한 테스트 코드를 만들기 쉽게해주는 라이브러리
@RunWith(JUnitParamsRunner.class)
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
	
	private Object[] paramsForTestFree() {
		return new Object[] { new Object[] { 0, 0, true }, new Object[] { 100, 0, false },
				new Object[] { 0, 100, false }, new Object[] { 100, 200, false } };
	}

	@Test
	// 전혀 Type Safe하지 않음...
//	@Parameters({
//		"0, 0, true",
//		"100, 0, false",
//		"0, 100, false"
//	}) 
	@Parameters(method = "paramsForTestFree")
	public void testFree(int basePrice, int maxPrice, boolean isFree) {
		// Given
		Event event = Event.builder().basePrice(basePrice).maxPrice(maxPrice).build();

		// When
		event.update();

		// Then
		assertThat(event.isFree()).isEqualTo(isFree);

//      아래의 중복된 코드는 필요가 없어짐.. parameters의 값들을 자동으로 반복해서 대입해줌
//		// Given
//		event = Event.builder().basePrice(100).maxPrice(0).build();
//
//		// When
//		event.update();
//
//		// Then
//		assertThat(event.isFree()).isFalse();
	}

	// parameterFor를 prefix로 시작하는 이름으로 지정하면 @parameter에 method를 지정하지 않아도 알아서 찾아준다.
	private Object[] parametersForTestOffline() {
		return new Object[] {
			new Object[] {"강남", true},
			new Object[] {null, false},
			new Object[] {"       ", false}
		};
	}
	
	@Test
	@Parameters
	public void testOffline(String location, boolean isOffline) {
		// Given
		Event event = Event.builder().location(location).build();

		// When
		event.update();

		// Then
		assertThat(event.isOffline()).isEqualTo(isOffline);

		// Given
		event = Event.builder().build();

		// When
		event.update();

		// Then
		assertThat(event.isOffline()).isFalse();
	}
}
