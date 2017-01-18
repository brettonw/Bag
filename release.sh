#! /usr/bin/env bash

go --clean package && git add --all . && git commit && git push && \
git checkout master && git pull && git merge develop && git push && \
go --clean package && git add --all . && git commit && git push && \
go release &&
git checkout develop && git pull && git merge master && git push && \
go --clean package
