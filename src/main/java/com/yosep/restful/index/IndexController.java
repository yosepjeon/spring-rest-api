package com.yosep.restful.index;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yosep.restful.events.EventController;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
public class IndexController {
	@GetMapping("/api")
	// link정보만 return하면 되기 때문에 ResourceSupport사용.
	public ResourceSupport index() {
		ResourceSupport index = new ResourceSupport();
		index.add(linkTo(EventController.class).withRel("events"));
		return index;
	}
}
