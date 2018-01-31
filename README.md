# lagom-java-chirper-example
Fork of lagom-java-chirper-example, to use as a sandbox to try to add docker-compose support.

To test..
```
sbt -DbuildTarget=compose clean docker:publishLocal
cd deploy/compose
docker-compose up -d cassandra web locator
#wait 30 seconds or so..
docker-compose up -d chirpservice friendservice activityservice
#visit http://localhost:9000/ in a browser 
```

if you need nodejs then you can acquire it using
```
wget -qO- https://raw.githubusercontent.com/creationix/nvm/v0.33.2/install.sh | bash
export NVM_DIR="$HOME/.nvm"
[ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh"  # This loads nvm
nvm install --lts
```

Current status: *FUNCTIONAL*

All looking good =)

Could even look into using docker-compose depends on directives to have the services wait for cassandra/consul =)



