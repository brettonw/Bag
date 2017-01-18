#! /usr/bin/env bash

go --clean package && git add --all . && git commit && git push
