import type { PluginListenerHandle } from '@capacitor/core';

export interface DeviceShakePlugin {
  enableListening(): Promise<void>;
  stopListening(): Promise<void>;
  addListener(
    eventName: 'shake',
    listenerFunc: () => void,
  ): Promise<PluginListenerHandle>;

  removeAllListeners(): Promise<void>;
}