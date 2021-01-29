package tkstone.test.frontend.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;

@RestController
public class TestRestService {
	
	@Autowired
	private ConfigurableApplicationContext context;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private DiscoveryClient discoveryClient;
	
	@RequestMapping("/query")
	public QueryResponse query(@RequestParam(name = "name") String serviceName) {
		QueryResponse response = new QueryResponse();
		try {
			String ip = InetAddress.getByName(serviceName).getHostAddress();
			response.setName(serviceName);
			response.setIp(ip);
		}
		catch(Exception e) {
			e.printStackTrace();
			response.setIp("Unknown");
		}
		return response;
	}
	
	@RequestMapping("/invoke_backend")
	public TestRestResponse invokeBackend(@RequestParam(name="name") String serviceName, @RequestParam(name="port") int port) {
		String url = String.format("http://%s:%d/invoke",serviceName, port);
		System.out.println("Target url : " + url);
		return this.restTemplate.getForObject(url, TestRestResponse.class);
	}
	
	@RequestMapping("/connect")
	public String connect(@RequestParam(name="name") String serviceName, @RequestParam(name="port") int port){
		try {
			SocketAddress address = new InetSocketAddress(InetAddress.getByName(serviceName), port);
			Socket socket = new Socket();
			socket.connect(address, 2000);
			socket.close();
			return "connection success";
		}
		catch(Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}
	
	@RequestMapping("/shutdown")
	public String shutdown() {
		this.context.close();
		return "OK";
	}
	
	@RequestMapping("/get_services")
	public List<String> getServices() {
		return this.discoveryClient.getServices();
	}
	
}
