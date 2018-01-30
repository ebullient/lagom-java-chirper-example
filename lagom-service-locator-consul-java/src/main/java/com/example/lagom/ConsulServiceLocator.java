package com.example.lagom;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.catalog.model.CatalogService;
import com.google.inject.Inject;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class ConsulServiceLocator implements ServiceLocator {
    private Logger log = LoggerFactory.getLogger(ConsulServiceLocator.class);

    private ConsulClient consulClient;
    private static String serviceProtocol;

    @Inject
    public ConsulServiceLocator(ConsulClient consulClient, ConsulConfiguration configuration) {
        log.info("Starting ConsulServiceLocator");
        this.consulClient = consulClient;
        this.serviceProtocol = configuration.getProtocol();
    }

    @Override
    public CompletionStage<Optional<URI>> locate(String name) {
        log.debug("Locating {}", name);
        return supplyAsync(() -> Optional.ofNullable(getRandomServiceUri(name)));
    }

    @Override
    public CompletionStage<Optional<URI>> locate(String name, Descriptor.Call<?, ?> serviceCall) {
        log.debug("Unsupported operation, locate with a Descriptor");
        return null;
    }

    @Override
    public <T> CompletionStage<Optional<T>> doWithService(String name, Descriptor.Call<?, ?> serviceCall, Function<URI, CompletionStage<T>> block) {
        log.info("Service operation with {}", name);
        URI uri = getRandomServiceUri(name);
        return block.apply(uri).thenApply(x -> Optional.of(x));
    }

    private URI getRandomServiceUri(String name) {
        List<CatalogService> services = consulClient.getCatalogService(name, QueryParams.DEFAULT).getValue();
        CatalogService service = services.get(ThreadLocalRandom.current().nextInt(0, services.size()));
        String serviceAddress = service.getServiceAddress();
        int servicePort = service.getServicePort();
        try {
            return new URI(String.format("%s://%s:%s", serviceProtocol, serviceAddress, servicePort));
        } catch (URISyntaxException e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(String.format("Could not create URI from %s:%s", serviceAddress, servicePort));
        }
    }
}
