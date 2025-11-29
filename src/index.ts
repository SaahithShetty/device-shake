import { registerPlugin } from '@capacitor/core';

import type { DeviceShakePlugin } from './definitions';

const DeviceShake = registerPlugin<DeviceShakePlugin>('DeviceShake', {});

export * from './definitions';
export { DeviceShake };
