# lagom-java-chirper-example
Fork of lagom-java-chirper-example, to use as a sandbox to try to add docker-compose support.

To test..
```
sbt -DbuildTarget=compose clean docker:publishLocal
cd deploy/compose
docker-compose up -d cassandra web locator
#wait 30 seconds or so..
docker-compose up -d chirpservice friendservice activityservice
```

Current status: *FUNCTIONAL*

All looking good =) c

Could even look into using docker-compose depends on directives to have the services wait for cassandra/consul =)

