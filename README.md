# lagom-java-chirper-example
Fork of lagom-java-chirper-example, to add docker-compose support.

## Running the Chirper Example

1. Obtain the source for this repository:
  * HTTPS: `git clone https://github.com/ebullient/lagom-java-chirper-example.git`
  * SSH: `git clone git@github.com:ebullient/lagom-java-chirper-example.git`

2. Change to the lagom-java-chirper-example directory

        $ cd lagom-java-chirper-example        # cd to project directory
        $ eval $(./deploy/compose/run.sh env)  # set command aliases
        $ alias                                # think-run and think-compose

3. Pull and build initial images required for running the system.

        $ think-compose pull         # Use docker-compose to pull base images
        $ think-compose build proxy  # Use docker-compose to build a proxy image

4. Make sure you have node.js. If you don't, this is one way to get it:

        $ wget -qO- https://raw.githubusercontent.com/creationix/nvm/v0.33.2/install.sh | bash
        $ export NVM_DIR="$HOME/.nvm"
        $ [ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh"  # This loads nvm
        $ nvm install --lts

4. Compile any image updates:

        $ sbt -DbuildTarget=compose clean docker:publishLocal

5. Start the chirper service (as a collection of containers):

        $ think-run start

6. Wait for the system to start.

        $ think-run wait

OR

        $ think-compose logs

7. Do some things!!

8. Stop the system

        $ think-run stop

## Project Status

Current status: *FUNCTIONAL*

All looking good =)

Could even look into using docker-compose depends on directives to have the services wait for cassandra/consul =)

## Differences from original (lagom) project

- 2h timeout set for websocket based services
- maven build removed (sbt worked for me, and didn't want to maintain 2 build systems)
- `proxy` folder has an haproxy docker container that will act as the front door instead of ingress
- `lagom-service-locator-consul-java` folder is the impl of ServiceLocator that uses consul (still returns null, but that path is never called at the mo)
- `<servicename>-impl/src/main/compose-resource` folder contains `platform.conf` for running on compose
- `deploy\compose` folder contains the docker-compose.yml that can be used to start the whole lot. 
- added a `/health` endpoint to each service for consul/haproxy to use to know if the service is up
- added logic to each services Module class to register the service with Consul if the env has configuration for the consul hostname

