/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package sample.chirper.load.impl;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;
import sample.chirper.activity.api.ActivityStreamService;
import sample.chirper.chirp.api.ChirpService;
import sample.chirper.friend.api.FriendService;
import sample.chirper.load.api.LoadTestService;
import play.Configuration;
import play.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.example.lagom.ConsulService;
import com.example.lagom.ConsulConfiguration;

public class LoadTestModule extends AbstractModule implements ServiceGuiceSupport {

  private ConsulConfiguration consulConfig;
  private Configuration configuration;

  @SuppressWarnings("unused")
  private Environment environment;

  @Inject
  public LoadTestModule(Environment environment, Configuration configuration) throws UnknownHostException {
      this.environment = environment;
      this.configuration = configuration;
      if( configuration.getString("lagom.discovery.consul.agent-hostname") !=null ){
        this.consulConfig = new ConsulConfiguration(configuration);
        registerService();
      }
  }

  @Override
  protected void configure() {
    bindService(LoadTestService.class, LoadTestServiceImpl.class);
    bindClient(FriendService.class);
    bindClient(ChirpService.class);
    bindClient(ActivityStreamService.class);
  }

  private void registerService() throws UnknownHostException {
      String hostname = InetAddress.getLocalHost().getHostAddress();
      int servicePort = Integer.parseInt(configuration.getString("http.port"));

      ConsulService loadtestService = new ConsulService("loadtestservice", hostname, servicePort, "health");
      loadtestService.registerService(consulConfig.getHostname(),
                                      consulConfig.getPort());
  }
}
