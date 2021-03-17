#!/usr/bin/env bash

generate_pojo() {
    the_file=$1
    the_package=$2
    echo "Generating POJOs from: '$the_file' in package: '$the_package' ..."
    jsonschema2pojo.bat \
      --source $the_file \
      --source-type JSON \
      --target gen/src/main/java \
      --package $the_package \
      --omit-hashcode-and-equals \
      --output-encoding UTF-8 \
      --target-language JAVA \
      --generate-builders
}

generate_pojo AIS_SignRequest.json com.swisscom.ais.client.rest.model.signreq
generate_pojo AIS_SignResponse.json com.swisscom.ais.client.rest.model.signresp
generate_pojo AIS_PendingRequest.json com.swisscom.ais.client.rest.model.pendingreq

# jsonschema2pojo --source AIS_SignRequest.json --source-type JSON --target gen/src/main/java --package com.swisscom.ais.client.rest.model.signreq --omit-hashcode-and-equals --output-encoding UTF-8 --target-language JAVA --generate-builders
# jsonschema2pojo --source AIS_SignResponse.json --source-type JSON --target gen/src/main/java --package com.swisscom.ais.client.rest.model.signresp --omit-hashcode-and-equals --output-encoding UTF-8 --target-language JAVA --generate-builders
# jsonschema2pojo --source AIS_PendingRequest.json --source-type JSON --target gen/src/main/java --package com.swisscom.ais.client.rest.model.pendingreq --omit-hashcode-and-equals --output-encoding UTF-8 --target-language JAVA --generate-builders

# Remark: there is no PendingResponse needed here, as the response from AIS for PendingRequest is actually SignResponse, either with
#   the signature fields filled in or just with Result.ResultMajor filled in with the status of the signature