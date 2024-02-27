FROM opensearchproject/opensearch:2.12.0

COPY ./build/distributions/opensearch-ubi.zip /tmp/

RUN /usr/share/opensearch/bin/opensearch-plugin install file:/tmp/opensearch-ubi.zip
