#!/bin/bash -e

STACK_NAME=${1:-"ubi"}

. stack.properties

NODE_IP=$(aws cloudformation describe-stacks \
	--stack-name ${STACK_NAME} \
	--query 'Stacks[0].Outputs[?OutputKey==`Node1PublicIP`].OutputValue' --output text \
	--region ${REGION} \
    --profile ${PROFILE})

# The output from this command should be an empty list of UBI stores (unless a store has already been created.)
curl -s http://${NODE_IP}:9200/_plugins/ubi | jq
