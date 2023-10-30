package com.johndeweydev.awps.repository.sessionrepository;

import android.util.Log;

import com.johndeweydev.awps.launcher.LauncherStages;
import com.johndeweydev.awps.launcher.LauncherEvent;
import com.johndeweydev.awps.data.LauncherOutputData;
import com.johndeweydev.awps.data.MicFirstMessageData;
import com.johndeweydev.awps.data.MicSecondMessageData;
import com.johndeweydev.awps.data.PmkidFirstMessageData;
import com.johndeweydev.awps.launcher.LauncherSingleton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class SessionRepository {

  private SessionRepositoryEvent sessionRepositoryEvent;
  private final StringBuilder queueData = new StringBuilder();
  private final LauncherEvent launcherEvent = new LauncherEvent() {
    @Override
    public void onLauncherOutput(String data) {
      char[] dataChar = data.toCharArray();
      for (char c : dataChar) {
        if (c == '\n') {
          processFormattedOutput();
          queueData.setLength(0);
        } else {
          queueData.append(c);
        }
      }
    }

    @Override
    public void onLauncherOutputError(String error) {
      sessionRepositoryEvent.onRepositoryOutputError(error);
    }

    @Override
    public void onLauncherInputError(String input) {
      sessionRepositoryEvent.onRepositoryInputError(input);
    }
  };

  public void setEventHandler(SessionRepositoryEvent sessionRepositoryEvent) {
    this.sessionRepositoryEvent = sessionRepositoryEvent;
    Log.d("dev-log", "SessionRepository.setEventHandler: Session repository event " +
            "callback set");
  }

  public void setLauncherEventHandler() {
    LauncherSingleton.getInstance().getLauncher().setLauncherSerialDataEvent(
            launcherEvent);
    Log.d("dev-log", "SessionRepository.setLauncherEventHandler: Launcher event callback " +
            "set in the context of session repository");
  }

  private void processFormattedOutput() {
    String data = queueData.toString();
    String time = createStringTime();

    LauncherOutputData launcherOutputData = new LauncherOutputData(time, data);

    char firstChar = data.charAt(0);
    char lastChar = data.charAt(data.length() - 2);
    if (firstChar == '{' && lastChar == '}') {
      sessionRepositoryEvent.onRepositoryOutputFormatted(launcherOutputData);
      data = data.replace("{", "").replace("}", "");
      String[] splitStrData = data.split(",");

      ArrayList<String> strDataList = new ArrayList<>(Arrays.asList(splitStrData));
      processContentOfFormattedOutput(strDataList);
    }
  }

  private String createStringTime() {
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    return dateFormat.format(calendar.getTime());
  }

  private void processContentOfFormattedOutput(ArrayList<String> strDataList) {
    StringBuilder dataArguments = new StringBuilder();
    for (int i = 0; i < strDataList.size(); i++) {
      dataArguments.append(strDataList.get(i));
      dataArguments.append("-");
    }

    Log.d("dev-log", "SessionRepository.processContentOfFormattedOutput: " +
            "Data to process -> " + dataArguments);

    switch (strDataList.get(0)) {
      case "ESP_STARTED":
        sessionRepositoryEvent.onRepositoryStarted();
        break;
      case "CMD_PARSER":
        cmdParserContext(strDataList);
        break;
      case "ARMAMENT":
        armamentContext(strDataList);
        break;
      case "PMKID":
        pmkidContext(strDataList);
        break;
      case "MIC":
        micContext(strDataList);
        break;
      case "DEAUTH":
        deauthContext(strDataList);
        break;
      case "RECONNAISSANCE":
        reconnaissanceContext(strDataList);
        break;
    }
  }

  private void cmdParserContext(ArrayList<String> strDataList) {
    String currentArmament = strDataList.get(2);
    String currentBssidTarget = strDataList.get(3);

    String namedArmament = "";
    switch (currentArmament) {
      case "01":
        namedArmament = "Reconnaissance";
        break;
      case "02":
        namedArmament = "PMKID";
        break;
      case "03":
        namedArmament = "MIC";
        break;
      case "04":
        namedArmament = "Deauth";
        break;
    }

    String formattedBssid = currentBssidTarget.substring(0, 2) + ":" +
            currentBssidTarget.substring(2, 4) + ":" +
            currentBssidTarget.substring(4, 6) + ":" +
            currentBssidTarget.substring(6, 8) + ":" +
            currentBssidTarget.substring(8, 10) + ":" +
            currentBssidTarget.substring(10, 12);

    switch (strDataList.get(1)) {
      case "CURRENT_ARMA":
        sessionRepositoryEvent.onRepositoryCommandParserCurrentArma(
                namedArmament, formattedBssid);
        break;
      case "TARGET_ARMA_SET":
        sessionRepositoryEvent.onRepositoryCommandParserTargetAndArmaSet(
                namedArmament, formattedBssid);
        break;
    }
  }

  private void armamentContext(ArrayList<String> strDataList) {
    if (Objects.equals(strDataList.get(1), "ACTIVATE")) {
      sessionRepositoryEvent.onRepositoryArmamentActivation();
    } else if (Objects.equals(strDataList.get(1), "DEACTIVATE")) {
      sessionRepositoryEvent.onRepositoryArmamentDeactivation();
    }
  }

  private void pmkidContext(ArrayList<String> strDataList) {
    switch (strDataList.get(1)) {
      case "FOUND_APS":
        String numberOfAps = strDataList.get(2);
        sessionRepositoryEvent.onRepositoryNumberOfFoundAccessPoints(numberOfAps);
        break;
      case "SCAN":
        processScannedAccessPointsAndNotifyViewModel(strDataList);
        break;
      case "FINISH_SCAN":
        sessionRepositoryEvent.onRepositoryFinishScanning();
        break;
      case "AP_NOT_FOUND":
        sessionRepositoryEvent.onRepositoryAccessPointNotFound();
        break;
      case "LAUNCHING_SEQUENCE":
        sessionRepositoryEvent.onRepositoryLaunchingSequence();
        break;
      case "SNIFF_STARTED":
        sessionRepositoryEvent.onRepositoryTaskCreated();
        break;
      case "WRONG_KEY_TYPE":
        String keyType = strDataList.get(3);
        sessionRepositoryEvent.onRepositoryPmkidWrongKeyType(keyType);
        break;
      case "SNIFF_STATUS":
        int status = Integer.parseInt(strDataList.get(2));
        sessionRepositoryEvent.onRepositoryTaskStatus("PMKID", status);
        break;
      case "MSG_1":
        String bssid = strDataList.get(2);
        String client = strDataList.get(3);
        String pmkid = strDataList.get(4);

        PmkidFirstMessageData pmkidFirstMessageData = new PmkidFirstMessageData(
                bssid, client, pmkid
        );
        sessionRepositoryEvent.onRepositoryEapolMessage(
                "PMKID", 1, pmkidFirstMessageData,
                null, null);
        break;
      case "FINISHING_SEQUENCE":
        sessionRepositoryEvent.onRepositoryFinishingSequence();
        break;
      case "SUCCESS":
        sessionRepositoryEvent.onRepositorySuccess();
        break;
      case "FAILURE":
        sessionRepositoryEvent.onRepositoryFailure(strDataList.get(2));
        break;
    }
  }

  private void micContext(ArrayList<String> strDataList) {
    switch (strDataList.get(1)) {
      case "FOUND_APS":
        String numberOfAps = strDataList.get(2);
        sessionRepositoryEvent.onRepositoryNumberOfFoundAccessPoints(numberOfAps);
        break;
      case "SCAN":
        processScannedAccessPointsAndNotifyViewModel(strDataList);
        break;
      case "FINISH_SCAN":
        sessionRepositoryEvent.onRepositoryFinishScanning();
        break;
      case "AP_NOT_FOUND":
        sessionRepositoryEvent.onRepositoryAccessPointNotFound();
        break;
      case "LAUNCHING_SEQUENCE":
        sessionRepositoryEvent.onRepositoryLaunchingSequence();
        break;
      case "DEAUTH_STARTED":
        sessionRepositoryEvent.onRepositoryTaskCreated();
        break;
      case "INJECTED_DEAUTH":
        int status = Integer.parseInt(strDataList.get(2));
        sessionRepositoryEvent.onRepositoryTaskStatus("MIC", status);
        break;
      case "MSG_1":
        String bssid = strDataList.get(2);
        String client = strDataList.get(3);
        String anonce = strDataList.get(4);

        MicFirstMessageData micFirstMessageData = new MicFirstMessageData(bssid, client, anonce);
        sessionRepositoryEvent.onRepositoryEapolMessage(
                "MIC", 1, null,
                micFirstMessageData, null);
        break;
      case "MSG_2":
        String secondMessageInfo = strDataList.get(4) + strDataList.get(5) + strDataList.get(6) +
                strDataList.get(7) + strDataList.get(8) + strDataList.get(9);

        String clientM2 = strDataList.get(2);
        String bssidM2 = strDataList.get(3);
        String replayCounter = strDataList.get(10);
        String snonce = strDataList.get(11);
        String mic = strDataList.get(12);
        String wpaKeyData = strDataList.get(13);

        MicSecondMessageData micSecondMessageData = new MicSecondMessageData(
                clientM2, bssidM2, secondMessageInfo, replayCounter, snonce, mic, wpaKeyData
        );
        sessionRepositoryEvent.onRepositoryEapolMessage(
                "MIC", 2, null, null,
                micSecondMessageData);
        break;
      case "FINISHING SEQUENCE":
        sessionRepositoryEvent.onRepositoryFinishingSequence();
        break;
      case "SUCCESS":
        sessionRepositoryEvent.onRepositorySuccess();
        break;
      case "FAILURE":
        sessionRepositoryEvent.onRepositoryFailure(strDataList.get(2));
        break;
    }
  }

  private void reconnaissanceContext(ArrayList<String> strDataList) {
    if (Objects.equals(strDataList.get(1), "FOUND_APS")) {

      String numberOfAps = strDataList.get(2);
      sessionRepositoryEvent.onRepositoryNumberOfFoundAccessPoints(numberOfAps);
    } else if (Objects.equals(strDataList.get(1), "SCAN")) {

      processScannedAccessPointsAndNotifyViewModel(strDataList);
    } else if (Objects.equals(strDataList.get(1), "FINISH_SCAN")) {
      sessionRepositoryEvent.onRepositoryFinishScanning();
    }
  }

  private void deauthContext(ArrayList<String> strDataList) {
    switch (strDataList.get(1)) {
      case "FOUND_APS":
        String numberOfAps = strDataList.get(2);
        sessionRepositoryEvent.onRepositoryNumberOfFoundAccessPoints(numberOfAps);
        break;
      case "SCAN":
        processScannedAccessPointsAndNotifyViewModel(strDataList);
        break;
      case "AP_NOT_FOUND":
        sessionRepositoryEvent.onRepositoryAccessPointNotFound();
        break;
      case "FINISH_SCAN":
        sessionRepositoryEvent.onRepositoryFinishScanning();
        break;
      case "LAUNCHING_SEQUENCE":
        sessionRepositoryEvent.onRepositoryLaunchingSequence();
        break;
      case "DEAUTH_STARTED":
        sessionRepositoryEvent.onRepositoryTaskCreated();
        break;
      case "INJECTED_DEAUTH":
        int numberOfInjectedDeauthentications = Integer.parseInt(strDataList.get(2));
        sessionRepositoryEvent.onRepositoryTaskStatus("DEAUTH",
                numberOfInjectedDeauthentications);
        break;
      case "STOPPED":
        sessionRepositoryEvent.onRepositoryDeauthStop(strDataList.get(2));
        break;
    }
  }

  private void processScannedAccessPointsAndNotifyViewModel(
          ArrayList<String> strDataList
  ) {
    String macAddress = strDataList.get(2);
    String ssid = strDataList.get(3);
    String rssi = strDataList.get(4);
    String channel = strDataList.get(5);
    sessionRepositoryEvent.onRepositoryScannedAccessPoint(macAddress, ssid, rssi, channel);
  }

  public String connect(
          int baudRate, int dataBits, int stopBits, int parity, int deviceId, int portNum) {
    LauncherStages status = LauncherSingleton.getInstance().getLauncher().initiateConnectionToDevice(
            baudRate, dataBits, stopBits, parity, deviceId, portNum
    );
    switch (status) {
      case ALREADY_CONNECTED: return "Already connected";
      case DEVICE_NOT_FOUND: return "Device not found";
      case DRIVER_NOT_FOUND: return "Driver not found";
      case PORT_NOT_FOUND: return "Port not found";
      case NO_USB_PERMISSION: return "No usb permission";
      case SUCCESSFULLY_CONNECTED: return "Successfully connected";
      case UNSUPPORTED_PORT_PARAMETERS: return "Unsupported port parameters";
      case FAILED_OPENING_DEVICE: return "Failed to open the device";
      default: return "None";
    }
  }

  public void disconnect() {
    LauncherSingleton.getInstance().getLauncher().disconnect();
  }

  public void startReading() {
    LauncherSingleton.getInstance().getLauncher().startReading();
  }

  public void stopReading() {
    LauncherSingleton.getInstance().getLauncher().stopReading();
  }

  public void writeData(String data) {
    LauncherSingleton.getInstance().getLauncher().writeData(data);
  }
}
