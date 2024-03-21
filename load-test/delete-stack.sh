#!/bin/bash -e

STACK_NAME=${1:-"ubi"}

. stack.properties

read -p "Are you sure you want to delete the following stack? ${STACK_NAME} (y/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]
then
  aws cloudformation delete-stack \
    --stack-name ${STACK_NAME} \
    --profile ${PROFILE} \
    --region ${REGION}
fi

