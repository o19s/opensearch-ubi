#!/bin/bash -e

curl -X POST http://localhost:8080/log -H "Content-Type: text/plain" -d "Some sample text to log"
