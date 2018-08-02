package com.bridgeIt.javaConfig;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.bridgeIt.javaConfig.model.Student;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
//	@Autowired
//	RabbitTemplate rabbitTemplate;
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		
		return "index";
	}
	
	@RequestMapping(value = "home", method = RequestMethod.POST)
	public ModelAndView home(@ModelAttribute Student student) {
		System.out.println(student);
		ModelAndView model = new ModelAndView();
		model.addObject("message", "welcome to home Page");
		model.setViewName("home");
		return model;
	}
	
	
	@Autowired
	Producer producer;
	
	@RequestMapping(value="rabbit", method = RequestMethod.POST , produces="application/json" )
	public void rabbit(@RequestParam("key") String key) {
		System.out.println(key);
		producer.sendMsg(key);
		
		
		
	}

	
}
