package com.yosep.restful.events;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/api/events", produces=MediaTypes.HAL_JSON_UTF8_VALUE)
public class EventController {
	
	private final EventRepository eventRepository;
	
	private final ModelMapper modelMapper;
	
	private final EventValidator eventValidator;
	
	// 스프링 4.3부터 생성자가 하나만 있고 이 생성자로 받아올 파라미터가 이미 빈으로 등록되어있으면 autowired 생략가
	@Autowired
	public EventController(EventRepository eventRepository,ModelMapper modelMapper, EventValidator eventValidator) {
		this.eventRepository = eventRepository;
		this.modelMapper = modelMapper;
		this.eventValidator = eventValidator;
	}
	
	@PostMapping
	public ResponseEntity createEvent(@RequestBody @Valid EventDTO eventDTO, Errors errors) {
		if(errors.hasErrors()) {
			// return ResponseEntity.badRequest().body(errors); -> 안됨 errors라는 객체를 json로 변환할 수 없음.
			// ObjectMapper BeanSerializer를 이용해서 자바 빈스펙을 만족하는 객체를 json으로 변환해줌.
			// errors는 자바빈 스펙을 준수하는 객체가 아니다. 그래서 BeanSerializer를 이용해서 json으로 변환할수 없음.
			return ResponseEntity.badRequest().body(errors);
		}
		
		eventValidator.validate(eventDTO, errors);
		if(errors.hasErrors()) {
			return ResponseEntity.badRequest().body(errors);
		}
		
		Event event = modelMapper.map(eventDTO, Event.class);
		event.update();
		Event newEvent = this.eventRepository.save(event);
		URI createdUri = linkTo(EventController.class).slash(newEvent.getId()).toUri();
		return ResponseEntity.created(createdUri).body(event);
	}
}
