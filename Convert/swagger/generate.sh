#!/usr/bin/env bash

swagger-codegen generate -i https://metrics.wavefront.com/api/v2/swagger.json -c swagger-config.json -l java
