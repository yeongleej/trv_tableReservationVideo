package com.ssafy.jariyo;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class JariyoApplication {

	public static void main(String[] args) {
		SpringApplication.run(JariyoApplication.class, args);
	}

//	 @Bean
//	 public ServletWebServerFactory serverFactory(){
//	 	TomcatServletWebServerFactory tomcatServlet = new TomcatServletWebServerFactory(){
//	 		@Override
//	 		protected void postProcessContext(Context context) {
//	 			SecurityConstraint securityConstraint = new SecurityConstraint();
//	 			securityConstraint.setUserConstraint("CONFIDENTIAL");
//	 			SecurityCollection collection = new SecurityCollection();
//	 			collection.addPattern("/*");
//	 			securityConstraint.addCollection(collection);
//	 			context.addConstraint(securityConstraint);
//	 		}
//	 	};
//
//	 	tomcatServlet.addAdditionalTomcatConnectors(httpConnector());
//
//	 	return tomcatServlet;
//	 }
//
//	 private Connector httpConnector(){
//	 	Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
//	 	connector.setScheme("http");
//	 	connector.setPort(8282);
//	 	connector.setSecure(false);
//	 	connector.setRedirectPort(443);
//	 	return connector;
//	 }

}
