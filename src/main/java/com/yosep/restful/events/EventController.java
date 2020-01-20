package com.yosep.restful.events;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/api/events", produces=MediaTypes.HAL_JSON_UTF8_VALUE)
public class EventController {
	
	private final EventRepository eventRepository;
	
	// 스프링 4.3부터 생성자가 하나만 있고 이 생성자로 받아올 파라미터가 이미 빈으로 등록되어있으면 autowired 생략가
	@Autowired
	public EventController(EventRepository eventRepository) {
		this.eventRepository = eventRepository;
	}
	
	@PostMapping
	public ResponseEntity createEvent(@RequestBody Event event) {
		Event newEvent = this.eventRepository.save(event);
		
		URI createdUri = linkTo(EventController.class).slash(newEvent.getId()).toUri();
		return ResponseEntity.created(createdUri).body(event);
	}
}
