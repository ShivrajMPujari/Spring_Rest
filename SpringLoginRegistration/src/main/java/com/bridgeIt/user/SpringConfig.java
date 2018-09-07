package com.bridgeIt.user;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import javax.sql.DataSource;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.EventHub;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
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

import com.bridgeIt.user.model.TradeUser;
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

	
	@Bean 
	TradeUser getAdmin(HFCAClient caClient) {
		System.out.println("try deserialize admin");
        TradeUser admin = tryDeserializeAdmin("admin");
        System.out.println(admin+" got admin while trydeserializing...");
       // TradeUser admin =null;
        if (admin == null) {
        	System.out.println(" admin is null");
            Enrollment adminEnrollment = null;
			try {
				adminEnrollment = caClient.enroll("admin", "adminpw");
			} catch (EnrollmentException | org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException e) {
				e.printStackTrace();
			}
            admin = new TradeUser("admin", "importer", "ImporterMSP", adminEnrollment);
            serializeAdmin(admin);
            System.out.println("admin serialized...");
        }
        
        return admin;
    }
	
	TradeUser tryDeserializeAdmin(String name) {
		//	Path path = Paths.get("//home//bridgelabz//Documents//workspace-sts-3.9.5.RELEASE//"+name + ".jso");	
			//System.out.println(path.toString()+" is the path "+path.getRoot());
	        if (Files.exists(Paths.get("//home//bridgelabz//Documents//workspace-sts-3.9.5.RELEASE//"+name + ".jso"))) {
	        	System.out.println();
	            return deSerializeAdmin(name);
	        }
	        return null;
	    }
	
	 TradeUser deSerializeAdmin(String name) {
	        try (ObjectInputStream decoder = new ObjectInputStream(
	                Files.newInputStream(Paths.get("//home//bridgelabz//Documents//workspace-sts-3.9.5.RELEASE//"+name + ".jso")))) {
	            return (TradeUser) decoder.readObject();
	        } catch (Exception e) {
				e.printStackTrace();
			} 
			return null;
	    }
	 
	 void serializeAdmin(TradeUser tradeUser) {
			System.out.println(tradeUser+" is serializing...");
			try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(
	                Paths.get("//home//bridgelabz//Documents//workspace-sts-3.9.5.RELEASE//"+tradeUser.getName() + ".jso")))) {
	            oos.writeObject(tradeUser);
	        	System.out.println(tradeUser+" is written...");
	        } catch (Exception e) {

				e.printStackTrace();
			}
			
		}
	 
	 @Bean
	 public Channel getChannel(HFClient client,TradeUser admin) {
			
		  
		  try {
			client.setUserContext(admin);
		} catch (org.hyperledger.fabric.sdk.exception.InvalidArgumentException e) {

			e.printStackTrace();
		}
		//	client.setUserContext(userContext)
			
		    Peer peer1 = null;
			try {
				peer1 = client.newPeer("peer0.importer.bridgeIt.com", "grpc://localhost:7051");
			} catch (org.hyperledger.fabric.sdk.exception.InvalidArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


	      EventHub eventHub1 = null;
		try {
			eventHub1 = client.newEventHub("eventhub01", "grpc://localhost:7053");
		} catch (org.hyperledger.fabric.sdk.exception.InvalidArgumentException e) {

			e.printStackTrace();
		}

	      Orderer orderer = null;
		try {
			orderer = client.newOrderer("orderer.bridgeIt.com", "grpc://localhost:7050");
		} catch (org.hyperledger.fabric.sdk.exception.InvalidArgumentException e) {

			e.printStackTrace();
		}

	      Channel channel = null;
		try {
			channel = client.newChannel("mychannel");
		} catch (org.hyperledger.fabric.sdk.exception.InvalidArgumentException e) {

			e.printStackTrace();
		}
	      try {
			channel.addPeer(peer1);
		} catch (org.hyperledger.fabric.sdk.exception.InvalidArgumentException e) {

			e.printStackTrace();
		}

	      try {
			channel.addEventHub(eventHub1);
		} catch (org.hyperledger.fabric.sdk.exception.InvalidArgumentException e) {

			e.printStackTrace();
		}

	      try {
			channel.addOrderer(orderer);
		} catch (org.hyperledger.fabric.sdk.exception.InvalidArgumentException e) {

			e.printStackTrace();
		}
	      try {
			channel.initialize();
		} catch (org.hyperledger.fabric.sdk.exception.InvalidArgumentException | TransactionException e) {

			e.printStackTrace();
		}
	      return channel;
		}
	
}
