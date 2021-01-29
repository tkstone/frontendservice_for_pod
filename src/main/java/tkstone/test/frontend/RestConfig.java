package tkstone.test.frontend;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ConnectionReuseStrategy;


import java.util.Arrays;

@Configuration
public class RestConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }
    
    @Bean
    public RestTemplate restTemplate() {
        return getRestTemplate();
    }
    
    private RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(clientHttpRequestFactory());
        restTemplate.getMessageConverters().stream().filter(converter -> converter instanceof MappingJackson2HttpMessageConverter).forEach(converter -> {
            MappingJackson2HttpMessageConverter jsonConverter = (MappingJackson2HttpMessageConverter) converter;
            jsonConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.TEXT_HTML));
            jsonConverter.setObjectMapper(objectMapper());
        });

        return restTemplate;
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(getHttpClient());
        factory.setConnectTimeout(3000);
        
        return factory;
    }
    
    private HttpClient getHttpClient() {
    	return HttpClientBuilder
    		.create()
    		.setConnectionReuseStrategy(NoConnectionReuseStrategy.INSTANCE)
    		.build();
    }
    
}
