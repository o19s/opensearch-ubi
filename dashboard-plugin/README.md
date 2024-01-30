# ublDashboard

A OpenSearch Dashboards plugin

---

## Development

See the [OpenSearch Dashboards contributing
guide](https://github.com/opensearch-project/OpenSearch-Dashboards/blob/main/CONTRIBUTING.md) for instructions
setting up your development environment.

    ## Scripts
    <dl>
      <dt><code>yarn osd bootstrap</code></dt>
      <dd>Execute this to install node_modules and setup the dependencies in your plugin and in OpenSearch Dashboards
      </dd>

      <dt><code>yarn plugin-helpers build</code></dt>
      <dd>Execute this to create a distributable version of this plugin that can be installed in OpenSearch Dashboards
      </dd>
    </dl>



---

https://opensearch.org/blog/dashboards-plugins-intro/

https://github.com/opensearch-project/OpenSearch-Dashboards/blob/main/CONTRIBUTING.md#development-environment-setup

https://github.com/opensearch-project/OpenSearch-Dashboards/blob/main/DEVELOPER_GUIDE.md

git clone https://github.com/opensearch-project/OpenSearch-Dashboards.git

cd OpenSearch-Dashboards

node scripts/generate_plugin.js my_plugin_name