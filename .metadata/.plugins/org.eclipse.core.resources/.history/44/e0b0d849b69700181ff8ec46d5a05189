package com.bridgeIt.user.service.utility;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMsgSender {

	
	@Autowired
	AmqpTemplate template;
	
	public	void sendMsg(UserMail mail ) {
		
		template.convertAndSend("myTopic", "myBindingKey", mail);;
		
	}
	
}
