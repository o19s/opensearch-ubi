import {
  PluginInitializerContext,
  CoreSetup,
  CoreStart,
  Plugin,
  Logger,
} from '../../../src/core/server';

import { UblDashboardPluginSetup, UblDashboardPluginStart } from './types';
import { defineRoutes } from './routes';

export class UblDashboardPlugin
  implements Plugin<UblDashboardPluginSetup, UblDashboardPluginStart> {
  private readonly logger: Logger;

  constructor(initializerContext: PluginInitializerContext) {
    this.logger = initializerContext.logger.get();
  }

  public setup(core: CoreSetup) {
    this.logger.debug('ublDashboard: Setup');
    const router = core.http.createRouter();

    // Register server side APIs
    defineRoutes(router);

    return {};
  }

  public start(core: CoreStart) {
    this.logger.debug('ublDashboard: Started');
    return {};
  }

  public stop() {}
}
