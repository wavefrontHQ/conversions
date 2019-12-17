#!/usr/bin/env bash

pushd swagger
./generate.sh
popd

mv swagger/src/main src/main
mv swagger/src/test src/test
