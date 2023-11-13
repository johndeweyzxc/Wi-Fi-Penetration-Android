package com.johndeweydev.awps;

public class AppConstants {

  public final static int LOCATION_PERMISSION_REQUEST_CODE = 100;
  public static final String INTENT_ACTION_GRANT_USB = BuildConfig.APPLICATION_ID + ".GRANT_USB";

  public static final int LAUNCHER_WRITE_WAIT_MILLIS = 2000;
  public static final int BAUD_RATE = 19200;
  public static final int DATA_BITS = 8;
  public static final int STOP_BITS = 1;
  public static final String PARITY_NONE = "PARITY_NONE";

  public static final String REST_API_URL = "http://192.168.1.9:8000";
  public static final String REST_API_ROOT_ENDPOINT = "/";
  public static final String REST_API_UPLOAD_ENDPOINT = "/api/v1/upload-hash";

}
