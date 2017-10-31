FROM ubuntu:latest

RUN apt-get update && apt-get install libqt53dcore5 -y

WORKDIR /app

COPY bin/rr_checker /bin/rr_checker

ENTRYPOINT ["/bin/rr_checker"]

