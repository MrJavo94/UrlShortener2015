package urlshortener2015.imperialred.web;

import java.io.IOException;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.mashape.unirest.http.exceptions.UnirestException;

import urlshortener2015.imperialred.exception.CustomException;

@ControllerAdvice
public class ExceptionHandlingController {

	@ExceptionHandler(CustomException.class)
	public ModelAndView handleCustomException(CustomException ex) {

		ModelAndView model = new ModelAndView("error/error");
		model.addObject("errCode", ex.getErrCode());
		model.addObject("errMsg", ex.getErrMsg());

		return model;

	}
	
	@ExceptionHandler(Exception.class)
	public ModelAndView handleAllException(Exception ex) {

		ModelAndView model = new ModelAndView("error/error");
		model.addObject("errMsg", ex.toString());

		return model;

	}
}
