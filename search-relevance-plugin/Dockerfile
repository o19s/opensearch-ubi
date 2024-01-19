FROM opensearchproject/opensearch:2.11.1

ADD build/distributions/opensearch-search-relevance-plugin.zip /tmp

RUN /usr/share/opensearch/bin/opensearch-plugin install file:/tmp/opensearch-search-relevance-plugin.zip
