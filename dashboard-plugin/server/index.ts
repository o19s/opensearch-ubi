import { PluginInitializerContext } from '../../../src/core/server';
import { UblDashboardPlugin } from './plugin';

// This exports static code and TypeScript types,
// as well as, OpenSearch Dashboards Platform `plugin()` initializer.

export function plugin(initializerContext: PluginInitializerContext) {
  return new UblDashboardPlugin(initializerContext);
}

export { UblDashboardPluginSetup, UblDashboardPluginStart } from './types';
