/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package sample.chirper.chirp.impl;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;
import sample.chirper.chirp.api.ChirpService;
import play.Configuration;
import play.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.example.lagom.ConsulConfiguration;
import com.example.lagom.ConsulService;

public class ChirpModule extends AbstractModule implements ServiceGuiceSupport {

    private ConsulConfiguration consulConfig;
    private Configuration configuration;

    @SuppressWarnings("unused")
    private Environment environment;

    @Inject
    public ChirpModule(Environment environment, Configuration configuration) throws UnknownHostException {
        this.environment = environment;
        this.configuration = configuration;
        if( configuration.getString("lagom.discovery.consul.agent-hostname") !=null ){
          this.consulConfig = new ConsulConfiguration(configuration);
          registerService();
        }
    }

    @Override
    protected void configure() {
        bindService(ChirpService.class, ChirpServiceImpl.class);
        bind(ChirpTopic.class).to(ChirpTopicImpl.class);
        bind(ChirpRepository.class).to(ChirpRepositoryImpl.class);
    }

    private void registerService() throws UnknownHostException {
        String hostname = InetAddress.getLocalHost().getHostAddress();
        int servicePort = Integer.parseInt(configuration.getString("http.port"));

        ConsulService chirpService = new ConsulService("chirpservice", hostname, servicePort, "health");
        chirpService.registerService(consulConfig.getHostname(),
                                     consulConfig.getPort());
    }
}
