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

Current status: *ALMOST FUNCTIONAL*

Seems to be running, except websockets are being rejected due to SecurityPolicy directives.

