package com.example.deviceshake;

import com.getcapacitor.Logger;

public class DeviceShake {

    public String echo(String value) {
        Logger.info("Echo", value);
        return value;
    }
}
