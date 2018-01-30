package com.example.lagom;

import com.ecwid.consul.v1.ConsulClient;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.lightbend.lagom.javadsl.api.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.api.Configuration;
import play.api.Environment;
import play.api.inject.Binding;
import play.api.inject.Module;
import scala.collection.Seq;

public class ConsulServiceLocatorModule extends Module {

    @SuppressWarnings("unused")
    private final play.Environment environment;
    private final ConsulConfiguration consulConfiguration;
    private Logger log = LoggerFactory.getLogger(ConsulServiceLocatorModule.class);

    @Inject
    public ConsulServiceLocatorModule(play.Environment environment, play.Configuration configuration) {
        log.info("Instantiating ConsulServiceLocatorModule");
        this.consulConfiguration = new ConsulConfiguration(configuration);
        this.environment = environment;
    }

    @Override
    public Seq<Binding<?>> bindings(Environment environment, Configuration configuration) {
        return seq(
                bind(ServiceLocator.class).to(ConsulServiceLocator.class).in(Singleton.class),
                bind(ConsulClient.class).toInstance(new ConsulClient(consulConfiguration.getHostname(), consulConfiguration.getPort()))
        );
    }
}


