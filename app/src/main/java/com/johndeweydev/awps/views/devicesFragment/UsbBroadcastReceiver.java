package com.johndeweydev.awps.views.devicesFragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.johndeweydev.awps.BuildConfig;
import com.johndeweydev.awps.viewmodels.UsbSerialViewModel;

public class UsbBroadcastReceiver extends BroadcastReceiver {

  public static final String INTENT_ACTION_GRANT_USB = BuildConfig.APPLICATION_ID + ".GRANT_USB";

  @Override
  public void onReceive(Context context, Intent intent) {
    if(INTENT_ACTION_GRANT_USB.equals(intent.getAction())) {
      if (UsbSerialViewModel.hasUsbDevicePermission()) {
        Log.d("dev-log", "DevicesFragment.UsbBroadcastReceiver.checkPermission: " +
                "Usb device permission granted");
        DevicesFragment.requestUsbPermission();
      } else {
        Log.d("dev-log", "DevicesFragment.UsbBroadcastReceiver.checkPermission: " +
                "Usb device permission denied by user");
      }
    }
  }
}
