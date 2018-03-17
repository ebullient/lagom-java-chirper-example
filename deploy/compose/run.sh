#!/bin/bash

# This will help start/stop Game On services using docker-compose.
# Note the example docker-compose overlay file to facilitate single
# service iterative development while running other/unmodified core
# game services locally.
#
# `eval $(docker/go-run.sh env)` will set aliases to more easily invoke
# this script's actions from the command line.
#

SCRIPTDIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

# Ensure we're executing from project root directory
cd "${SCRIPTDIR}"/..

#set the action, default to help if none passed.
ACTION=help
if [ $# -ge 1 ]; then
  ACTION=$1
  shift
fi

FIRST_ROUND="cassandra web locator"
SECOND_ROUND="chirpservice friendservice activityservice proxy"

NOLOGS=0
#-- Parse args
while [[ $# -gt 0 ]]; do
  case "$1" in
  "--nologs")
    NOLOGS=1
  ;;
  all)
    PROJECTS="$FIRST_ROUND $SECOND_ROUND"
  ;;
  *) PROJECTS="$1 $PROJECTS"
  ;;
  esac
  shift
done

if [ -z "${PROJECTS}" ]; then
  PROJECTS="$FIRST_ROUND $SECOND_ROUND"
fi

OVERRIDE=
if [ -f $SCRIPTDIR/docker-compose.override.yml ]
then
  OVERRIDE="-f $SCRIPTDIR/docker-compose.override.yml"
fi
COMPOSE="docker-compose -f $SCRIPTDIR/docker-compose.yml ${OVERRIDE}"

wrap_docker() {
  echo "
> docker $@"
  docker $@
}

wrap_compose() {
  echo "
> ${COMPOSE} $@"
  ${COMPOSE} $@
}

up_log() {
  echo
  echo "*****"
  if [ $NOLOGS -eq 0 ]; then
    echo "Starting containers (detached) [$@]"
    echo "Logs will continue in the foreground."
  else
    echo "Starting containers (detached) [$@]"
    echo "View logs: "
    echo "    think-compose logs $@"
  fi
  echo
  echo "Launching containers will take some time as dependencies are coordinated."
  echo "*****"
  echo

  wrap_compose up -d $@
  if [ $NOLOGS -eq 0 ]; then
    sleep 3
    wrap_compose logs --tail="5" -f $@
  fi
}

down_rm() {
  echo "Stopping containers [$@]"
  wrap_compose stop $@
  echo
  echo "*****"
  echo "Cleaning up containers [$@]"
  wrap_compose rm $@
}

refresh() {
  ## Refresh base images (betas)
  echo "Pulling fresh images [$PROJECTS]"
  wrap_compose build --pull $PROJECTS
  if [ $? != 0 ]; then
    echo Docker build of $PROJECTS failed.. please examine logs and retry as appropriate.
    exit 2
  fi
}

usage() {
  echo "
  Actions:
    start
    stop
    status
    env

    refresh_images
    reload_proxy

  Use optional arguments to select specific image(s) by name"
}

case "$ACTION" in
  env)
    echo "alias think-compose='${COMPOSE}';"
    echo "alias think-run='${SCRIPTDIR}/run.sh';"
  ;;
  refresh_images)
    refresh $PROJECTS
  ;;
  reload_proxy)
    wrap_compose kill -s HUP proxy
  ;;
  start)
    NOLOGS=1
    wrap_compose rm -f
    up_log $FIRST_ROUND
    echo "waiting for base services to stabilize"
    sleep 30
    up_log $SECOND_ROUND
    echo "visit http://localhost:9000/ in a browser"
    echo "if you see a 503 error, the services haven't finished booting yet,"
    echo "check with 'think-compose ps', or 'think-compose logs <service-name>'"
    echo "To wait via script: think-run wait"
  ;;
  status)
    wrap_compose ps
  ;;
  stop)
    wrap_compose stop $SECOND_ROUND
    wrap_compose stop $FIRST_ROUND
  ;;
  wait)
    echo "Waiting until http://localhost:9000/ returns OK."
    echo "This may take awhile, as it is starting a number of containers at the same time."
    echo "If you're curious, cancel this, and use 'think-compose logs' to watch what is happening"

    until $(curl --output /dev/null --silent --fail http://localhost:9000/ 2>/dev/null)
    do
      printf '.'
      sleep 5s
    done
    echo ""
    echo "The environment is ready: https://localhost:9000/"
  ;;
  *)
    usage
  ;;
esac
