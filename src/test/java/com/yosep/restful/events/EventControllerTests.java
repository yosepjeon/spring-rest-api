package com.yosep.restful.events;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

import java.time.LocalDateTime;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yosep.restful.common.RestDocsConfiguration;
import com.yosep.restful.common.TestDescription;

@RunWith(SpringRunner.class)
//@WebMvcTest
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class) // 다른 스프링 빈 설정파일을 읽어와서 사용하는 방법 중 하나.
public class EventControllerTests {
	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

//	@MockBean
//	EventRepository eventRepository;

	@Test
	@TestDescription("정상적으로 이벤트를 생성하는 테스트")
	public void createEvent() throws Exception {
		EventDTO event = EventDTO.builder().name("Spring").description("REST API Development")
				.beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
				.closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
				.beginEventDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
				.endEventDateTime(LocalDateTime.of(2018, 11, 26, 14, 21)).basePrice(100).maxPrice(200)
				.limitOfEnrollment(100).location("강남역 D2 스타트업 팩토리").build();
//		event.setId(10);
//		Mockito.when(eventRepository.save(event)).thenReturn(event);

		mockMvc.perform(post("/api/events/").contentType(MediaType.APPLICATION_JSON_UTF8).accept(MediaTypes.HAL_JSON)
				.content(objectMapper.writeValueAsString(event))).andDo(print()).andExpect(status().isCreated())
				.andExpect(jsonPath("id").exists()).andExpect(header().exists(HttpHeaders.LOCATION))
				.andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_UTF8_VALUE))
				.andExpect(jsonPath("id").value(Matchers.not(100))).andExpect(jsonPath("free").value(false))
				.andExpect(jsonPath("offline").value(true))
				.andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
				// self링크 같은 경우 해당 이벤트 resource마다 매번 설정해줘야 하니까 이런 기능은 EventResource에 추가해주는 것이 좋음
				.andDo(document("create-event",
						links(
								linkWithRel("self").description("link to self"),
								linkWithRel("query-events").description("link to query events"),
								linkWithRel("update-event").description("link to update an existing event"),
								linkWithRel("profile").description("link to profile")
						),
						requestHeaders(
								headerWithName(HttpHeaders.ACCEPT).description("accept header"),
								headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
						),
						requestFields(
								fieldWithPath("name").description("Name of new event"),
								fieldWithPath("description").description("description of new event"),
								fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
								fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
								fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
								fieldWithPath("endEventDateTime").description("date time of end of new event"),
								fieldWithPath("location").description("location of new event"),
								fieldWithPath("basePrice").description("base price of new event"),
								fieldWithPath("maxPrice").description("max price of new event"),
								fieldWithPath("limitOfEnrollment").description("limit of enrollment")
						),
						responseHeaders(
								headerWithName(HttpHeaders.LOCATION).description("Location header"),
								headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
						),
						responseFields(
								fieldWithPath("id").description("identifier of new event"),
								fieldWithPath("name").description("Name of new event"),
								fieldWithPath("description").description("description of new event"),
								fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
								fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
								fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
								fieldWithPath("endEventDateTime").description("date time of end of new event"),
								fieldWithPath("location").description("location of new event"),
								fieldWithPath("basePrice").description("base price of new event"),
								fieldWithPath("maxPrice").description("max price of new event"),
								fieldWithPath("limitOfEnrollment").description("limit of enrollment"),
								fieldWithPath("free").description("it tells is if this event is free or not"),
								fieldWithPath("offline").description("it tells is if this event is offline event or not"),
								fieldWithPath("eventStatus").description("eventStatus"),
								fieldWithPath("_links.self.href").description("link to self"),
								fieldWithPath("_links.query-events.href").description("link to query event list"),
								fieldWithPath("_links.update-event.href").description("link to update exiting event"),
								fieldWithPath("_links.profile.href").description("link to profile")
						)
						// _links에 대한 부분도 response의 일부로 보고 있기 때문에 이를 회피하기 위한 방법의 하나로 relaxedResponseFields() 사용
						// 장점: 문서의 일부분만 테스트 가능
						// 단점: 정확한 문서를 생성하지 못한다.
						// 되도록 전부 기입을 해서 정확하게 문서를 만들자.
//						relaxedResponseFields(
//								fieldWithPath("id").description("identifier of new event"),
//								fieldWithPath("name").description("Name of new event"),
//								fieldWithPath("description").description("description of new event"),
//								fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
//								fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
//								fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
//								fieldWithPath("endEventDateTime").description("date time of end of new event"),
//								fieldWithPath("location").description("location of new event"),
//								fieldWithPath("basePrice").description("base price of new event"),
//								fieldWithPath("maxPrice").description("max price of new event"),
//								fieldWithPath("limitOfEnrollment").description("limit of enrollment"),
//								fieldWithPath("free").description("it tells is if this event is free or not"),
//								fieldWithPath("offline").description("it tells is if this event is offline event or not"),
//								fieldWithPath("eventStatus").description("eventStatus")
//						)
				));

	}

	@Test
	@TestDescription("입력 받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
	public void createEvent_Bad_Request() throws Exception {
		Event event = Event.builder().id(100).name("Spring").description("REST API Development")
				.beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
				.closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
				.beginEventDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
				.endEventDateTime(LocalDateTime.of(2018, 11, 26, 14, 21)).basePrice(100).maxPrice(200)
				.limitOfEnrollment(100).location("강남역 D2 스타트업 팩토리").free(true).offline(false)
				.eventStatus(EventStatus.PUBLISHED).build();
//		event.setId(10);
//		Mockito.when(eventRepository.save(event)).thenReturn(event);

		mockMvc.perform(post("/api/events/").contentType(MediaType.APPLICATION_JSON_UTF8).accept(MediaTypes.HAL_JSON)
				.content(objectMapper.writeValueAsString(event))).andDo(print()).andExpect(status().isBadRequest());

	}

	@Test
	@TestDescription("입력 값이 비어있는 경우에 에러가 발생하는 테스트")
	public void createEvent_Bad_Request_Empty_Input() throws Exception {
		EventDTO eventDTO = EventDTO.builder().build();

		this.mockMvc.perform(post("/api/events").contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(this.objectMapper.writeValueAsString(eventDTO))).andExpect(status().isBadRequest());
	}

	@Test
	@TestDescription("입력 값이 잘못된 경우에 에러가 발생하는 테스트")
	public void createEvent_Bad_Request_Wrong_Input() throws Exception {
		EventDTO eventDTO = EventDTO.builder().name("Spring").description("REST API Development")
				.beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
				.closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
				.beginEventDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
				.endEventDateTime(LocalDateTime.of(2018, 11, 23, 14, 21)).basePrice(10000).maxPrice(200)
				.limitOfEnrollment(100).location("강남역 D2 스타트업 팩토리").build();

		this.mockMvc
				.perform(post("/api/events").contentType(MediaType.APPLICATION_JSON_UTF8)
						.content(this.objectMapper.writeValueAsString(eventDTO)))
				.andDo(print()).andExpect(status().isBadRequest()).andExpect(jsonPath("$[0].objectName").exists())
//		.andExpect(jsonPath("$[0].field").exists())
				.andExpect(jsonPath("$[0].defaultMessage").exists()).andExpect(jsonPath("$[0].code").exists());
//		.andExpect(jsonPath("$[0].rejectedValue").exists());
	}
}
