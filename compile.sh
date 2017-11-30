#!/usr/bin/env bash

mvn clean compile assembly:single
mv target/alc-proj1-1.0.0-jar-with-dependencies.jar proj3.jar
