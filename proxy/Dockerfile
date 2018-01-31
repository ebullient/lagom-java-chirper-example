FROM haproxy:1.7.9

LABEL maintainer="fish"

RUN mkdir -p /run/haproxy \
    mkdir -p /opt/haproxy

COPY ./haproxy.cfg /opt/haproxy/haproxy.cfg
COPY ./startup.sh /opt/startup.sh

EXPOSE 80

ENTRYPOINT ["/opt/startup.sh"]
CMD [""]
