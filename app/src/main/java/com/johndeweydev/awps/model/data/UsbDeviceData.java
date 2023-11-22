package com.johndeweydev.awps.model.data;

import android.hardware.usb.UsbDevice;

import com.hoho.android.usbserial.driver.UsbSerialDriver;

public record UsbDeviceData(UsbDevice usbDevice, int devicePort, UsbSerialDriver usbSerialDriver) {

}
