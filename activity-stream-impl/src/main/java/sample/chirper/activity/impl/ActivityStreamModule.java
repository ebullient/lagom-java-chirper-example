/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package sample.chirper.activity.impl;

import sample.chirper.chirp.api.ChirpService;

import sample.chirper.friend.api.FriendService;
import com.google.inject.Inject;
import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;
import sample.chirper.activity.api.ActivityStreamService;
import play.Configuration;
import play.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.example.lagom.ConsulService;
import com.example.lagom.ConsulConfiguration;

public class ActivityStreamModule extends AbstractModule implements ServiceGuiceSupport {

  private Configuration configuration;
  private ConsulConfiguration consulConfig;

  @SuppressWarnings("unused")
  private Environment environment;

  @Inject
  public ActivityStreamModule(Environment environment, Configuration configuration) throws UnknownHostException {
      this.environment = environment;
      this.configuration = configuration;
      if( configuration.getString("lagom.discovery.consul.agent-hostname") !=null ){
        this.consulConfig = new ConsulConfiguration(configuration);
        registerService();
      }
  }

  @Override
  protected void configure() {
    bindService(ActivityStreamService.class, ActivityStreamServiceImpl.class);
    bindClient(FriendService.class);
    bindClient(ChirpService.class);
  }

  private void registerService() throws UnknownHostException {
      String hostname = InetAddress.getLocalHost().getHostAddress();
      int servicePort = Integer.parseInt(configuration.getString("http.port"));
      ConsulService activityService = new ConsulService("activityservice", hostname, servicePort, "health");
      activityService.registerService(consulConfig.getHostname(),
                                      consulConfig.getPort());
  }
}
