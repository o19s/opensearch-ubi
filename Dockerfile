FROM opensearchproject/opensearch:2.13.0

COPY ./build/distributions/opensearch-ubi-1.0.0-os2.13.0.zip /tmp/

RUN /usr/share/opensearch/bin/opensearch-plugin install file:/tmp/opensearch-ubi-1.0.0-os2.13.0.zip