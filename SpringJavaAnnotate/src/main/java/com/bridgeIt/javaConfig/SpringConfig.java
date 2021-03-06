package com.bridgeIt.javaConfig;

import java.util.Properties;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@EnableWebMvc
@ComponentScan("com.bridgeIt.javaConfig")
@Configuration
public class SpringConfig extends WebMvcConfigurerAdapter {
	
	@Bean
	public InternalResourceViewResolver internalResolver() {
		
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		
		resolver.setViewClass(JstlView.class);
		resolver.setPrefix("/WEB-INF/views/");
		resolver.setSuffix(".jsp");
		return resolver;
	}
	
	static final String queueName="myQueue1";
	static final String topicName="myTopic";
	
	
	@Bean
	ConnectionFactory factory(){
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
		connectionFactory.setUsername("guest");
		connectionFactory.setPassword("guest");
		connectionFactory.setPort(5672);
		
		return connectionFactory;
		
	 }
	

	@Bean
	JavaMailSender getMailSender() {
		
		JavaMailSenderImpl implementation = new JavaMailSenderImpl();
		implementation.setDefaultEncoding("UTF-8");
		implementation.setHost("smtp.gmail.com");
		implementation.setUsername("tradefinancebridgelabz@gmail.com");
		implementation.setPassword("tradefinance2018");
		implementation.setPort(587);
		Properties props = implementation.getJavaMailProperties();
	    props.put("mail.transport.protocol", "smtp");
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.starttls.enable", "true");
	    props.put("mail.debug", "true");
		return implementation;
		
	}
	
	
	
	@Bean
	Queue queue() {
		
		
	 return new Queue(queueName, true);
	}
	
	@Bean
	TopicExchange topicExchange() {
		
		return new TopicExchange(topicName);
	}
	
	@Bean
	Binding binding () {
		
		
		return BindingBuilder.bind(queue()).to(topicExchange()).with("myBindingKey");
	}
	
	
//	@Bean
//    MessageListenerAdapter listenerAdapter(Receiver receiver) {
//        return new MessageListenerAdapter(receiver, "receiveMessage");
//    }
	
	@Bean 
	AmqpTemplate getTemplate () {

		 RabbitTemplate r = new RabbitTemplate();
		 r.setConnectionFactory(factory());
		return r;
	}
	
	@Bean
	RabbitAdmin getRabbitAdmin() {
		RabbitAdmin admin = new RabbitAdmin(factory());
		admin.declareExchange(topicExchange());
		admin.declareQueue(queue());
		admin.declareBinding(binding());
		return admin;
	}
	
/*	@Bean
	SimpleRabbitListenerContainerFactory rabbitListenerFactory() {
		
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(factory());
		factory.setConcurrentConsumers(3);
		factory.setMaxConcurrentConsumers(10);
		return factory;	
	}*/
	
	@Autowired
	Consumer consumer;
	
	
	
	@Bean
	SimpleMessageListenerContainer getSimpleMessageListenerContainer(ConnectionFactory connectionFactory) {
		SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer(connectionFactory);
		simpleMessageListenerContainer.setQueueNames(queueName);
		simpleMessageListenerContainer.setMessageListener(new MessageListener() {
		
			@Override
			public void onMessage(Message message) {
				String string = new String(message.getBody());
				consumer.consumed(string);
			
				
			}
		});
		return simpleMessageListenerContainer;
	}
	
	
	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		
		configurer.enable();
	}
	
}
