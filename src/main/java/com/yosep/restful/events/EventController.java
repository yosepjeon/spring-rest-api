package com.yosep.restful.events;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.net.URI;
import java.util.Optional;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yosep.restful.common.ErrorsResource;

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
//			return ResponseEntity.badRequest().body(errors);
			return badRequest(errors);
		}
		
		eventValidator.validate(eventDTO, errors);
		if(errors.hasErrors()) {
//			return ResponseEntity.badRequest().body(errors);
			return badRequest(errors);
		}
		
		Event event = modelMapper.map(eventDTO, Event.class);
		event.update();
		Event newEvent = this.eventRepository.save(event);
		
		ControllerLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
		
		// Hateoas가 제공해주는 기능 중 하나
		URI createdUri = selfLinkBuilder.toUri();
		EventResource eventResource = new EventResource(event);
		eventResource.add(linkTo(EventController.class).withRel("query-events"));
//		eventResource.add(selfLinkBuilder.withSelfRel());
		eventResource.add(selfLinkBuilder.withRel("update-event"));
		eventResource.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));
		return ResponseEntity.created(createdUri).body(eventResource);
		
//		return ResponseEntity.created(createdUri).body(event);
	}
	
	@GetMapping
	public ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler) {
		Page<Event> page = this.eventRepository.findAll(pageable);
		PagedResources<Resource<Event>> pagedResources = assembler.toResource(page, e -> new EventResource(e));
		pagedResources.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));
		
		return ResponseEntity.ok(pagedResources);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity getEvent(@PathVariable Integer id) {
		Optional<Event> optionalEvent = this.eventRepository.findById(id);
		if(!optionalEvent.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		
		Event event = optionalEvent.get();
		EventResource eventResource = new EventResource(event);
		eventResource.add(new Link("/docs/index.html#resources-events-get").withRel("profile"));

		return ResponseEntity.ok(eventResource);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity updateEvent(@PathVariable Integer id, @RequestBody @Valid EventDTO eventDto, Errors errors) {
		Optional<Event> optionalEvent = this.eventRepository.findById(id);
		
		if(!optionalEvent.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		
		if(errors.hasErrors()) {
			return badRequest(errors);
		}
		
		this.eventValidator.validate(eventDto, errors);
		if(errors.hasErrors()) {
			return badRequest(errors);
		}
		
		Event existingEvent = optionalEvent.get();
		// source: 어디에서 
		this.modelMapper.map(eventDto, existingEvent);
		Event savedEvent = this.eventRepository.save(existingEvent);
		
		EventResource eventResource = new EventResource(savedEvent);
		eventResource.add(new Link("/docs/index.html#resources-events-update").withRel("profile"));
		
		return ResponseEntity.ok(eventResource);
	}
	
	private ResponseEntity badRequest(Errors errors) {
		return ResponseEntity.badRequest().body(new ErrorsResource(errors));
	}
}
