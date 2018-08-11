package com.bridgeIt.user;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.bridgeIt.user.model.User;
import com.bridgeIt.user.model.UserForgotPassword;
import com.bridgeIt.user.model.UserLogin;
import com.bridgeIt.user.service.UserService;
import com.bridgeIt.user.service.utility.RabbitMsgSender;

@RestController
public class UserController {

	
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	UserService  service;
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
	//	System.out.println(dao.insert());
		model.addAttribute("serverTime", formattedDate );
		
		return "home";
	}
	

	@RequestMapping(value="register", method = RequestMethod.POST ,consumes="application/json", produces="application/json" )
	public ResponseEntity<BaseResponse> responser (@Valid @RequestBody User user,BindingResult result){
	System.out.println(" in / register");
		BaseResponse response = new BaseResponse();
		ResponseEntity<BaseResponse> respond;
		if(result.hasErrors()) {
			
			List<?> errs=result.getFieldErrors();
			List <String>allErrorMsg = new ArrayList<String>();
			for (Object object : errs) {
				ObjectError objError=(ObjectError) object;
				allErrorMsg.add(objError.getDefaultMessage());
				System.out.println(objError.getDefaultMessage());
			}
		
			response.setStatus(HttpStatus.BAD_REQUEST);
			response.setCode(400);
			response.setMessage("please enter proper inputs");
			response.setErrors(allErrorMsg);
			respond = new ResponseEntity<BaseResponse>(response,HttpStatus.BAD_REQUEST);
			return respond;

		}
	
		 response=service.userReg(user);
		if(response.getStatus()==HttpStatus.BAD_REQUEST) {
			respond = new ResponseEntity<BaseResponse>(response,HttpStatus.BAD_REQUEST);
			return respond;
		}
		else {
			respond = new ResponseEntity<BaseResponse>(response,HttpStatus.OK);
			return respond;
		}
		
		
	}
	
	@RequestMapping(value="login" , method = RequestMethod.POST, produces="application/json")
	public ResponseEntity<BaseResponse> login(@RequestBody UserLogin userLogin){
		BaseResponse response = new BaseResponse();
		System.out.println( userLogin.getEmail()+" "+userLogin.getPassword());
		System.out.println("-in /login");
		if(service.login(userLogin.getEmail(), userLogin.getPassword())) {
			response.setStatus(HttpStatus.OK);
			response.setCode(200);
			response.setMessage("you are logged in sucessfully");
			User user =service.getUser( userLogin.getEmail());
			String token = service.getToken(user);
			response.setToken(token);
			response.setUser(user);
			
			return new ResponseEntity<BaseResponse>(response,HttpStatus.OK);
		}	
		else {
			response.setMessage("Login failed...you are not a valid user");
			response.setStatus(HttpStatus.BAD_REQUEST);
			response.setCode(400);
			return new ResponseEntity<BaseResponse>(response,HttpStatus.BAD_REQUEST);
		}
		
	}
	
	@Autowired
	RabbitMsgSender producer;
	
/*	@RequestMapping(value="rabbit", method = RequestMethod.POST , produces="application/json" )
	public void rabbit(@RequestParam("key") String key) {
		System.out.println(key);
		//producer.sendMsg(key);
	
		
	}*/
	
	@RequestMapping(value="verification/{flag}/{key}", method = RequestMethod.GET )
	public ResponseEntity<BaseResponse> verify(@PathVariable("key") String key ,  @PathVariable("flag") String flag,HttpServletResponse res) {
		BaseResponse response = new BaseResponse();
		if(flag.equals("getVerified")) {
			System.out.println(key);
			boolean result= service.verify(key);
			System.out.println("is verification got completed? "+result);
			
			if(result==true) {
				response.setCode(200);
				response.setStatus(HttpStatus.OK);
				response.setMessage("you are verified");
				try {
					res.sendRedirect("http://127.0.0.1:3000/#!/login");
				} catch (IOException e) {
					e.printStackTrace();
				}
				return new ResponseEntity<BaseResponse>(response,HttpStatus.OK);
			}else {
				response.setStatus(HttpStatus.BAD_REQUEST);
				response.setCode(400);
				response.setMessage("some thing went wrong!..");
				try {
					res.sendRedirect("http://127.0.0.1:3000/#!/session-out");
				} catch (IOException e) {
					
					e.printStackTrace();
				}
				
				return new ResponseEntity<BaseResponse>(response,HttpStatus.BAD_REQUEST);
				
			}
			
			
			
		}
		else if(flag.equals("reset_password")) {
			
			System.out.println(key+" from resetPassword");
			boolean result = service.checkSessionPassword(key);
			
			if(result==true) {
				response.setStatus(HttpStatus.OK);
				response.setCode(200);
				response.setMessage("session is alive");
				return new ResponseEntity<BaseResponse>(response,HttpStatus.OK);
			}else if (result==false) {
				response.setStatus(HttpStatus.BAD_REQUEST);
				response.setCode(400);
				response.setMessage("your session is no longer alive..");
				try {
					res.sendRedirect("http://127.0.0.1:3000/#!/session-out");
				} catch (IOException e) {
					
					e.printStackTrace();
				}
				return new ResponseEntity<BaseResponse>(response,HttpStatus.BAD_REQUEST);
			}
		
			return new ResponseEntity<BaseResponse>(response,HttpStatus.BAD_REQUEST);

		}
		return null;

		
	}
	
	@RequestMapping(value="resetPassword", method = RequestMethod.POST )
	public ResponseEntity<BaseResponse> resetPassword(@RequestBody UserForgotPassword userForgotPassword,HttpServletResponse res) {
		
		
		BaseResponse response = new BaseResponse();

		boolean result = service.checkSessionPassword(userForgotPassword.getUuid());
		
		if(result==true) {
			
			boolean changed = service.changePassword(userForgotPassword.getUuid(), userForgotPassword.getNewPassword());
			if(changed) {
				response.setMessage("your password is saved sucessfully");
				response.setCode(200);
				response.setStatus(HttpStatus.OK);
				return new ResponseEntity<BaseResponse>(response,HttpStatus.OK);
			}else {
				response.setMessage("some thing went wrong");
				response.setStatus(HttpStatus.BAD_REQUEST);
				response.setCode(400);
				return new ResponseEntity<BaseResponse>(response,HttpStatus.BAD_REQUEST);
				
			}
			
		}else  {
			response.setStatus(HttpStatus.BAD_REQUEST);
			response.setCode(400);
			response.setMessage("your session is no longer alive..");
			try {
				res.sendRedirect("http://127.0.0.1:3000/#!/session-out");
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			return new ResponseEntity<BaseResponse>(response,HttpStatus.BAD_REQUEST);
		}
	
		

	}
		
		
	@RequestMapping(value="forgotPassword", method = RequestMethod.POST ,produces="application/json" )
	public ResponseEntity<BaseResponse> conformationMail(@RequestBody UserLogin UserLogin) {
		System.out.println("in conformation");
		boolean result =service.sendConformationMail(UserLogin.getEmail());
		BaseResponse response = new BaseResponse();
		if(result==true) {
			response.setMessage("check your email to reset password");
			response.setStatus(HttpStatus.OK);
			response.setCode(200);
			return new ResponseEntity<BaseResponse>(response,HttpStatus.OK);
			
		}else {
			
			response.setStatus(HttpStatus.BAD_REQUEST);
			response.setMessage("please check your mail id properly");
			response.setCode(400);
			return new ResponseEntity<BaseResponse>(response,HttpStatus.BAD_REQUEST);
		}

	}
	

	

	
}
