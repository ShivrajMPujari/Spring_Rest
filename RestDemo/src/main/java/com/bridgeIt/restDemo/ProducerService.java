package com.bridgeIt.restDemo;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class ProducerService {

	
	public void sendMessage(byte[] message) {
		
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername("guest");
		factory.setPassword("guest");
		factory.setPort(5672);
		factory.setHost("local");
		Connection connection=null;
		try {
			connection = factory.newConnection();
			Channel channel=connection.createChannel();
			channel.exchangeDeclare("MyExchange", "fanout");
			channel.basicPublish("MyExchange", "MyKey", null, message);
			channel.close();
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		

	}
	
	
}
