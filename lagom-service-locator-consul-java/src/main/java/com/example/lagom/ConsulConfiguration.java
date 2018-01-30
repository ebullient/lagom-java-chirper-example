package com.example.lagom;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import play.Configuration;

/**
 * Mostly taken from github.com/olbpetersson's code as a learning exercise. Learn through re-typing.
 */

/**
 * Holds connection information for the consul instance so that the service can register itself on startup.
 */
@Singleton
public class ConsulConfiguration {

    private int port;
    private String hostname;
    private String protocol;

    private String hostnameKey = "lagom.discovery.consul.agent-hostname";
    private String portKey = "lagom.discovery.consul.agent-port";
    private String protocolKey = "lagom.discovery.consul.uri-scheme";

    @Inject
    public ConsulConfiguration(Configuration playConfiguration) {
        this.hostname = playConfiguration.getString(hostnameKey);
        this.port = Integer.parseInt(playConfiguration.getString(portKey));
        this.protocol = playConfiguration.getString(protocolKey);
    }

    public String getHostname() {
        return hostname;
    }

    public String getProtocol() {
        return protocol;
    }

    public int getPort() {
        return port;
    }

}
