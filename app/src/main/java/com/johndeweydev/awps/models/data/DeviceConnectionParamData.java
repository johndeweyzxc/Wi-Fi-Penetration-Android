package com.johndeweydev.awps.models.data;

public record DeviceConnectionParamData(int baudRate, int dataBits, int stopBits, String parity,
                                        int deviceId, int portNum) {
}
