/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package sample.chirper.friend.impl;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import sample.chirper.friend.api.FriendService;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;
import play.Configuration;
import play.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.example.lagom.ConsulService;
import com.example.lagom.ConsulConfiguration;

public class FriendModule extends AbstractModule implements ServiceGuiceSupport {

  private Configuration configuration;
  private ConsulConfiguration consulConfig;

  @SuppressWarnings("unused")
  private Environment environment;

  @Inject
  public FriendModule(Environment environment, Configuration configuration) throws UnknownHostException {
      this.environment = environment;
      this.configuration = configuration;
      if( configuration.getString("lagom.discovery.consul.agent-hostname") !=null ){
        this.consulConfig = new ConsulConfiguration(configuration);
        registerService();
      }
  }

  @Override
  protected void configure() {
    bindService(FriendService.class, FriendServiceImpl.class);
  }

  private void registerService() throws UnknownHostException {
      String hostname = InetAddress.getLocalHost().getHostAddress();
      int servicePort = Integer.parseInt(configuration.getString("http.port"));

      ConsulService friendService = new ConsulService("friendservice", hostname, servicePort, "health");
      friendService.registerService(consulConfig.getHostname(),
                                    consulConfig.getPort());
  }
}
