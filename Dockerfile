FROM opensearchproject/opensearch:2.14.0

COPY ./build/distributions/opensearch-ubi-0.0.11-os2.14.0.zip /tmp/

RUN /usr/share/opensearch/bin/opensearch-plugin install file:/tmp/opensearch-ubi-0.0.11-os2.14.0.zip
