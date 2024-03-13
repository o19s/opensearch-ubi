#!/bin/bash -e

GRADLE_PROPERTIES_FILE=gradle.properties

function getProperty {
    PROP_KEY=$1
    PROP_VALUE=`cat $GRADLE_PROPERTIES_FILE | grep "$PROP_KEY" | cut -d'=' -f2`
    echo $PROP_VALUE
}

OPENSEARCH_VERSION=$(getProperty "opensearchVersion")
UBI_VERSION=$(getProperty "ubiVersion")

TAG_VERSION="v${UBI_VERSION}-os${OPENSEARCH_VERSION}"
echo "Tagging as ${TAG_VERSION}"

git tag -a "${TAG_VERSION}" -m "${TAG_VERSION}"
git push --tags
