package com.johndeweydev.awps.model.data;

public record DeviceConnectionParamData(int baudRate, int dataBits, int stopBits, String parity,
                                        int deviceId, int portNum) {
}
