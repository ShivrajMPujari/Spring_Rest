package com.bridgeIt.restDemo;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

public class ConsumerService {

//	@RabbitListener(bindings= @QueueBinding(value=@Queue(), exchange = @Exchange(value="MyExchange",type=ExchangeTypes.FANOUT)))
//	public void consumerMessage(byte[] data) {
//	    String consumedMessage = new String(data);
//	    System.out.println(" [x] Consumed  '" + consumedMessage + "'");
//	}

	
	
	
}
