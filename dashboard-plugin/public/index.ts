import './index.scss';

import { UblDashboardPlugin } from './plugin';

// This exports static code and TypeScript types,
// as well as, OpenSearch Dashboards Platform `plugin()` initializer.
export function plugin() {
  return new UblDashboardPlugin();
}
export { UblDashboardPluginSetup, UblDashboardPluginStart } from './types';
