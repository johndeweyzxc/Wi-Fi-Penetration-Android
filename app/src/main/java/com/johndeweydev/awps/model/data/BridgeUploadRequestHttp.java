package com.johndeweydev.awps.model.data;

public record BridgeUploadRequestHttp(String ssid, String bssid, String client_mac_address,
                                      String key_type, String a_nonce, String hash_data,
                                      String key_data, String latitude, String longitude,
                                      String address, String date_captured) {
}
