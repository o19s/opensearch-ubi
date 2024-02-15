FROM opensearchproject/opensearch:2.11.1

COPY ./build/distributions/opensearch-ubl.zip /tmp/

RUN /usr/share/opensearch/bin/opensearch-plugin install file:/tmp/opensearch-ubl.zip
