package com.yosep.restful.events;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

 
//public class EventResource extends ResourceSupport{
//	
//	@JsonUnwrapped
//	private Event event;
//	
//	public EventResource(Event event) {
//		this.event = event;
//	}
//
//	public Event getEvent() {
//		return event;
//	}
//}

// JsonUnwrapped가 적용되어 있다.
// Bean이 아님
public class EventResource extends Resource<Event> {

	public EventResource(Event event, Link...links) {
		super(event, links);
		add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
		// TODO Auto-generated constructor stub
	}
	
}
