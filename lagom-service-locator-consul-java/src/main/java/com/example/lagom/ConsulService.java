package com.example.lagom;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.agent.model.NewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

public class ConsulService {
    private final Logger log = LoggerFactory.getLogger(ConsulService.class);
    private String serviceName;

    private String hostname;
    private int port;
    private String serviceId;
    private Optional<NewService.Check> serviceCheck = Optional.empty();

    public ConsulService(String serviceName, String hostname, int port) {
        this.serviceName = serviceName;
        this.hostname = hostname;
        this.port = port;
        this.serviceId = UUID.randomUUID().toString();
    }

    public ConsulService(String serviceName, String hostname, int port, String healthEndpoint) {
        this(serviceName, hostname, port);
        String healthCheckUrl = String.format("http://%s:%s/%s", hostname, port, healthEndpoint);

        NewService.Check serviceCheck = new NewService.Check();
        serviceCheck.setHttp(healthCheckUrl);
        serviceCheck.setInterval("10s");
        serviceCheck.setTimeout("1s");
        this.serviceCheck = Optional.ofNullable(serviceCheck);
    }

    public void registerService(String consulHostname, int consulPort) {
        NewService service = new NewService();
        service.setId(serviceId);
        service.setName(serviceName);
        service.setPort(port);
        service.setAddress(hostname);

        if (serviceCheck.isPresent()) {
            service.setCheck(serviceCheck.get());
        }

        ConsulClient client = new ConsulClient(consulHostname, consulPort);
        client.agentServiceRegister(service);
        log.info("Registered service {} with {}:{} at {}:{}", serviceName, hostname, port, consulHostname, consulPort);
    }
}
