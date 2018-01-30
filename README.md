# lagom-java-chirper-example
Fork of lagom-java-chirper-example, to use as a sandbox to try to add docker-compose support.

To test..
```
sbt -DbuildTarget=compose clean docker:publishLocal
cd deploy/compose
docker-compose up -d cassandra web locator
docker-compose up chirpservice
```

Current status: *BORKED*

```
chirpservice       | [chirpservice-akka.actor.default-dispatcher-19] WARN akka.persistence.cassandra.snapshot.CassandraSnapshotStore - Failed to connect to Cassandra and initialize. It will be retried on demand. Caused by: null
```

```
chirpservice       | java.lang.NullPointerException
chirpservice       |    at com.lightbend.lagom.javadsl.api.ServiceLocator.locateAll(ServiceLocator.java:66)
chirpservice       |    at com.lightbend.lagom.javadsl.api.ServiceLocator.locateAll(ServiceLocator.java:42)
chirpservice       |    at com.lightbend.lagom.javadsl.persistence.cassandra.CassandraPersistenceModule$InitServiceLocatorHolder$$anon$4.locateAll(CassandraPersistenceModule.scala:81)
chirpservice       |    at com.lightbend.lagom.internal.persistence.cassandra.ServiceLocatorSessionProvider.$anonfun$lookupContactPoints$1(ServiceLocatorSessionProvider.scala:31)
```

