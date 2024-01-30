import { NavigationPublicPluginStart } from '../../../src/plugins/navigation/public';

export interface UblDashboardPluginSetup {
  getGreeting: () => string;
}
// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface UblDashboardPluginStart {}

export interface AppPluginStartDependencies {
  navigation: NavigationPublicPluginStart;
}
