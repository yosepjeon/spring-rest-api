package com.yosep.restful.events;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;

import com.yosep.restful.accounts.AccountService;
import com.yosep.restful.common.BaseControllerTest;
import com.yosep.restful.common.TestDescription;

public class EventControllerTests extends BaseControllerTest{

	// 주석 처리
//	@MockBean
//	EventRepository eventRepository;
	
	@Autowired
	EventRepository eventRepository;
	
	@Autowired
	AccountService accountService;

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

		mockMvc.perform(post("/api/events/")
				.header(HttpHeaders.AUTHORIZATION, getBearerToken())
				.contentType(MediaType.APPLICATION_JSON_UTF8).accept(MediaTypes.HAL_JSON)
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
								fieldWithPath("_links.profile.href").description("link to profile"),
								fieldWithPath("manager").description("manager..")
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

	public String getBearerToken() throws Exception {
		return "Bearer " + getAccessToken();
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

		mockMvc.perform(post("/api/events/").header(HttpHeaders.AUTHORIZATION, getBearerToken()).contentType(MediaType.APPLICATION_JSON_UTF8).accept(MediaTypes.HAL_JSON)
				.content(objectMapper.writeValueAsString(event))).andDo(print()).andExpect(status().isBadRequest());

	}

	@Test
	@TestDescription("입력 값이 비어있는 경우에 에러가 발생하는 테스트")
	public void createEvent_Bad_Request_Empty_Input() throws Exception {
		EventDTO eventDTO = EventDTO.builder().build();

		this.mockMvc.perform(post("/api/events").header(HttpHeaders.AUTHORIZATION, getBearerToken()).contentType(MediaType.APPLICATION_JSON_UTF8)
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
				.perform(post("/api/events").header(HttpHeaders.AUTHORIZATION, getBearerToken()).contentType(MediaType.APPLICATION_JSON_UTF8)
						.content(this.objectMapper.writeValueAsString(eventDTO)))
				.andDo(print()).andExpect(status().isBadRequest()).andExpect(jsonPath("content[0].objectName").exists())
//		.andExpect(jsonPath("$[0].field").exists())
				
				/*
				 * JsonUnwarpped는 Json array에는 Unwrap이 안됨. 아래로 코드 수정...
					.andExpect(jsonPath("$[0].defaultMessage").exists()).andExpect(jsonPath("$[0].code").exists()).andExpect(jsonPath("_links.index").exists());
				*/
				.andExpect(jsonPath("content[0].defaultMessage").exists()).andExpect(jsonPath("content[0].code").exists()).andExpect(jsonPath("_links.index").exists());
//		.andExpect(jsonPath("$[0].rejectedValue").exists());
	}
	
	@Test
	@TestDescription("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
	public void queryEvents() throws Exception {
		// Given
		IntStream.range(0, 30).forEach(i -> {
			this.generateEvent(i);
		});
		
		// TODO: create를 참조하며 문서화하자.
		// When
		this.mockMvc.perform(get("/api/events").param("page", "1").param("size", "10").param("sort", "name,DESC"))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("page").exists())
		.andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
		.andExpect(jsonPath("_links.self").exists())
		.andExpect(jsonPath("_links.profile").exists())
		.andDo(document("query-event"));
	}
	
	@Test
	@TestDescription("기존의 이벤트를 하나 조회하기")
	public void getEvent() throws Exception {
		// Given
		Event event = this.generateEvent(100);
		
		// TODO: create를 참조하며 문서화하자.
		// When & Then
		this.mockMvc.perform(get("/api/events/{id}",event.getId()))
		.andExpect(status().isOk())
		.andExpect(jsonPath("name").exists())
		.andExpect(jsonPath("id").exists())
		.andExpect(jsonPath("_links.self").exists())
		.andExpect(jsonPath("_links.profile").exists())
		.andDo(document("get-an-event"));
	}
	
	@Test
	@TestDescription("없는 이벤트를 조회했을 때 404 응답받기")
	public void getEvent404() throws Exception {
		// When & Then
		this.mockMvc.perform(get("/api/events/11883"))
		.andExpect(status().isNotFound());
	}
	
	@Test
	@TestDescription("이벤트를 정상적으로 수정하기")
	public void updateEvent() throws Exception {
		// Given
		Event event = this.generateEvent(200);
		EventDTO eventDto = this.modelMapper.map(event, EventDTO.class);
		String eventName = "Updated Event";
		eventDto.setName(eventName);
		
		// TODO: create를 참조하며 문서화하자.
		// When & Then
		this.mockMvc.perform(put("/api/events/{id}",event.getId())
				.header(HttpHeaders.AUTHORIZATION, getBearerToken())
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(this.objectMapper.writeValueAsString(eventDto))
				)
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("name").value(eventName))
		.andExpect(jsonPath("_links.self").exists())
		.andDo(document("update-event"));
	}
	
	@Test
	@TestDescription("입력값이 잘못된 경우에 이벤트 수정 실패")
	public void updateEvent400Wrong() throws Exception {
		// Given
		Event event = this.generateEvent(200);
		EventDTO eventDto = this.modelMapper.map(event, EventDTO.class);
		eventDto.setBasePrice(20000);
		eventDto.setMaxPrice(1000);
		
		// When & Then
		this.mockMvc.perform(put("/api/events/{id}",event.getId())
				.header(HttpHeaders.AUTHORIZATION, getBearerToken())
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(this.objectMapper.writeValueAsString(eventDto)))
		.andDo(print())
		.andExpect(status().isBadRequest());
	}
	
	@Test
	@TestDescription("존재하지 않는 이벤트 수정 실패")
	public void updateEvent404() throws Exception {
		// Given
		Event event = this.generateEvent(200);
		EventDTO eventDto = this.modelMapper.map(event, EventDTO.class);
		eventDto.setBasePrice(20000);
		eventDto.setMaxPrice(1000);
		
		// When & Then
		this.mockMvc.perform(put("/api/events/123123",event.getId())
				.header(HttpHeaders.AUTHORIZATION, getBearerToken())
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(this.objectMapper.writeValueAsString(eventDto)))
		.andDo(print())
		.andExpect(status().isNotFound());
	}
	
	@Test
	@TestDescription("입력값이 비어있는 경우에 이벤트 수정 실패")
	public void updateEvent400Empty() throws Exception {
		// Given
		Event event = this.generateEvent(200);
		EventDTO eventDto = new EventDTO();
		
		// When & Then
		this.mockMvc.perform(put("/api/events/{id}",event.getId())
				.header(HttpHeaders.AUTHORIZATION, getBearerToken())
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(this.objectMapper.writeValueAsString(eventDto)))
		.andDo(print())
		.andExpect(status().isBadRequest());
	}

	private Event generateEvent(int i) {
		// TODO Auto-generated method stub
		Event event = Event.builder().name("Spring").description("REST API Development")
				.beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
				.closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
				.beginEventDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
				.endEventDateTime(LocalDateTime.of(2018, 11, 26, 14, 21)).basePrice(100).maxPrice(200)
				.limitOfEnrollment(100).location("강남역 D2 스타트업 팩토리").free(false).offline(true).eventStatus(EventStatus.DRAFT).build();
		
		return this.eventRepository.save(event);
	}
	
	private String getAccessToken() throws Exception {
		// TODO Auto-generated method stub
		// Given
				String username = "enekelx1@naver.com";
				String password = "123123";
				
				// AppConfig에 이미 위의 회원 정보를 담아서 실행하기 때문에 주석 처리하였다. 이것때문에 1시간 삽질...
//				Account user = Account.builder()
//						.email(username)
//						.password(password)
//						.roles(Stream.of(AccountRole.ADMIN, AccountRole.USER).collect(Collectors.toSet()))
//						.build();
		//
//				this.accountService.saveAccount(user);
				
				String clientId = "myApp";
				String clientSecret = "pass";

				ResultActions perform = this.mockMvc.perform(post("/oauth/token")
						.with(httpBasic(clientId, clientSecret))
						.param("username", username)
						.param("password", password)
						.param("grant_type", "password"));
				
				MockHttpServletResponse response = perform.andReturn().getResponse();
				
				String responseBody = response.getContentAsString();
				
				Jackson2JsonParser parser = new Jackson2JsonParser();
				
				return parser.parseMap(responseBody).get("access_token").toString();
	}
}
