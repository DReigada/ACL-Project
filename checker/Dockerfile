FROM ubuntu:latest

RUN apt-get update && apt-get install libqt53dcore5 -y

WORKDIR /app

COPY bin/ /bin/

ENTRYPOINT ["/bin/rr_checker_asp"]
