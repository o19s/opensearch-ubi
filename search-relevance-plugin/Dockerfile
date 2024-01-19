FROM opensearchproject/opensearch:2.11.1

ADD build/distributions/opensearch-rest-plugin.zip /tmp

RUN /usr/share/opensearch/bin/opensearch-plugin install file:/tmp/opensearch-rest-plugin.zip
