package org.ast.apachecxfsoap.orginal;

import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.jaxws.support.JaxWsServiceFactoryBean;
import org.ast.apachecxfsoap.CxfConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import javax.xml.ws.Endpoint;

import static org.ast.apachecxfsoap.orginal.Constants.SERVICE_ADDRESS;

@SpringBootApplication
@Import(CxfConfiguration.class)
public class ApacheCxfSoapApplication {

    @Bean
    public RegistrationService registrationService() {
        return new RegistrationService();
    }

    @Bean
    public JaxWsServiceFactoryBean jaxWsServiceFactoryBean() {
        JaxWsServiceFactoryBean serviceFactoryBean = new JaxWsServiceFactoryBean();
        serviceFactoryBean.setValidate(true);
        return serviceFactoryBean;
    }

    @Bean
    public JaxWsServerFactoryBean jaxWsServerFactoryBean() {
        return new JaxWsServerFactoryBean(jaxWsServiceFactoryBean());
    }

    @Bean
    public Endpoint registrationEndpoint(SpringBus springBus) {
        EndpointImpl endpoint = new EndpointImpl(springBus, registrationService(), jaxWsServerFactoryBean());
        endpoint.publish(SERVICE_ADDRESS);
        return endpoint;
    }

    public static void main(String[] args) {
        SpringApplication.run(ApacheCxfSoapApplication.class, args);
    }
}
