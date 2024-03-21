#!/bin/bash -e

STACK_NAME=${1:-"ubi"}

. stack.properties

aws cloudformation create-stack \
    --stack-name ${STACK_NAME} \
    --template-body file://cloudformation.yaml \
    --parameters \
        ParameterKey=KeyName,ParameterValue=${KEYNAME} \
    --region ${REGION} \
    --profile ${PROFILE}
