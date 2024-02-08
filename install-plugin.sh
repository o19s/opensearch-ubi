#!/bin/bash -e

# Use this script to install the plugin in a locally running Opensearch.

DIR=`pwd`
sudo /usr/share/opensearch/bin/opensearch-plugin install file:${DIR}/build/distributions/opensearch-rest-plugin.zip
