#!/usr/bin/env bash

sbt -Dsecurity.assessment=true -Denvironment=local test
