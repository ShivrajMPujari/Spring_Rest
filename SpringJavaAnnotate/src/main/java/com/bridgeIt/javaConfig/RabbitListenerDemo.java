package com.bridgeIt.javaConfig;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

public class RabbitListenerDemo {

	@RabbitListener(queues="MyQueue1")
	public void consumerMessage(String data) {
	   
	    System.out.println(" [x] Consumed  '" + data + "'");
	}
	
	
}
