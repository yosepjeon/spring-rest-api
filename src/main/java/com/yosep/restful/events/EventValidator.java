package com.yosep.restful.events;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

// 이벤트 검증을 위한 클래스
@Component
public class EventValidator {

	public void validate(EventDTO eventDTO, Errors errors) {
		if (eventDTO.getBasePrice() > eventDTO.getMaxPrice() && eventDTO.getMaxPrice() > 0) {
			// field error
			errors.rejectValue("basePrice", "wrongValue", "BasePrice is wrong.");
			errors.rejectValue("maxPrice", "wrongValue", "maxPrice is wrong.");
			
			// global error
			errors.reject("wrongPrices","Values for prices are wrong");
		}

		LocalDateTime endEventDateTime = eventDTO.getEndEventDateTime();
		if (endEventDateTime.isBefore(eventDTO.getBeginEnrollmentDateTime())
				|| endEventDateTime.isBefore(eventDTO.getBeginEventDateTime())
				|| endEventDateTime.isBefore(eventDTO.getCloseEnrollmentDateTime())) {
			errors.rejectValue("endEventDateTime", "wrongValue","endEventDateTime is wrong");
		}
		
		// TODO beginEventDateTime
		// TODO CloseEnrollmentDateTime
	}
}
