package com.bridgeIt.user;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.Properties;
import javax.sql.DataSource;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import com.bridgeIt.user.service.utility.MailSender;
import com.bridgeIt.user.service.utility.UserMail;
import com.mchange.v2.c3p0.ComboPooledDataSource;

@EnableWebMvc
@ComponentScan("com.bridgeIt.user")
@Configuration
public class SpringConfig extends WebMvcConfigurerAdapter {

	
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		
		registry.addMapping("/**").allowedMethods("GET","POST").allowedOrigins("*").allowedHeaders("*");
		
	}
	
	
	@Bean
	public InternalResourceViewResolver internalViewResolver () {		
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
	    public MessageConverter jsonMessageConverter(){
	        return new JsonMessageConverter();
	    }
	
	
	
	@Bean 
	AmqpTemplate getTemplate () {

		 RabbitTemplate r = new RabbitTemplate();
		 r.setConnectionFactory(factory());
		 r.setMessageConverter(jsonMessageConverter());
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
	
	@Bean
	SimpleRabbitListenerContainerFactory rabbitListenerFactory() {
		
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(factory());
		factory.setConcurrentConsumers(3);
		factory.setMaxConcurrentConsumers(10);
		return factory;	
	}
	
	@Autowired
	MailSender sender;

	
	@Bean
	SimpleMessageListenerContainer getSimpleMessageListenerContainer(ConnectionFactory connectionFactory) {
		SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer(connectionFactory);
		simpleMessageListenerContainer.setQueueNames(queueName);
		simpleMessageListenerContainer.setMessageConverter(jsonMessageConverter());
		
		//simpleMessageListenerContainer.setMessageListener(new MailSender());
		
		simpleMessageListenerContainer.setMessageListener(new MessageListener() {
		
			
			
			@Override
			public void onMessage(Message message) {
				String string = new String(message.getBody());
				ObjectMapper mapper = new ObjectMapper();
				try {
					UserMail userMail = mapper.readValue(string, UserMail.class);
					sender.consumed(userMail);
				} catch (JsonParseException e) {
					e.printStackTrace();
				} catch (JsonMappingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println(string);
				
			}
		});
		return simpleMessageListenerContainer;
	}

	
	@Bean
	public DataSource dataSource() {
		
		ComboPooledDataSource cpds = new ComboPooledDataSource();
		cpds.setUser("root");
		cpds.setPassword("shiv");
		try {
			cpds.setDriverClass("com.mysql.jdbc.Driver");
			cpds.setJdbcUrl("jdbc:mysql://localhost:3306/Shiv?useSSL=false");
			cpds.setMinPoolSize(5);                                     
			cpds.setAcquireIncrement(5);
			cpds.setMaxPoolSize(20);
		
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		
		return cpds;
	}
	
	@Bean
	public JdbcTemplate template() {
		
		return new JdbcTemplate(dataSource());
	}
	
	@Bean
    public HFClient getHfClient() throws Exception {
        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        HFClient client = HFClient.createNewInstance();
        client.setCryptoSuite(cryptoSuite);
        return client;
    }
	
	@Bean
	HFCAClient getHFCaClient() {
		CryptoSuite suite = null;
		HFCAClient client = null;
		try {
			suite = CryptoSuite.Factory.getCryptoSuite();
			client = HFCAClient.createNewInstance("http://localhost:7054", null);
			client.setCryptoSuite(suite);
		} catch (IllegalAccessException e) {
			
			e.printStackTrace();
		} catch (InstantiationException e) {
		
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
		
			e.printStackTrace();
		} catch (CryptoException e) {
		
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
		
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
		
			e.printStackTrace();
		} catch (InvocationTargetException e) {
	
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		return client;
	}

	
}
