package com.johndeweydev.awps.models.repo.serial.sessionreposerial;

import android.util.Log;

import com.johndeweydev.awps.data.DeviceConnectionParamData;
import com.johndeweydev.awps.models.api.launcher.LauncherStages;
import com.johndeweydev.awps.models.api.launcher.LauncherEvent;
import com.johndeweydev.awps.data.LauncherOutputData;
import com.johndeweydev.awps.data.MicFirstMessageData;
import com.johndeweydev.awps.data.MicSecondMessageData;
import com.johndeweydev.awps.data.PmkidFirstMessageData;
import com.johndeweydev.awps.models.api.launcher.LauncherSingleton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class SessionRepoSerial {

  private SessionRepoSerialEvent sessionRepoSerialEvent;
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
      sessionRepoSerialEvent.onRepositoryOutputError(error);
    }

    @Override
    public void onLauncherInputError(String input) {
      sessionRepoSerialEvent.onRepositoryInputError(input);
    }
  };

  public void setEventHandler(SessionRepoSerialEvent sessionRepoSerialEvent) {
    this.sessionRepoSerialEvent = sessionRepoSerialEvent;
    Log.d("dev-log", "SessionRepository.setEventHandler: Session repository event " +
            "callback set");
  }

  public void setLauncherEventHandler() {
    LauncherSingleton.getInstance().getLauncher().setLauncherEventHandler(
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
      sessionRepoSerialEvent.onRepositoryOutputFormatted(launcherOutputData);
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
        sessionRepoSerialEvent.onRepositoryStarted();
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
        sessionRepoSerialEvent.onRepositoryCommandParserCurrentArma(
                namedArmament, formattedBssid);
        break;
      case "TARGET_ARMA_SET":
        sessionRepoSerialEvent.onRepositoryCommandParserTargetAndArmaSet(
                namedArmament, formattedBssid);
        break;
    }
  }

  private void armamentContext(ArrayList<String> strDataList) {
    if (Objects.equals(strDataList.get(1), "ACTIVATE")) {
      sessionRepoSerialEvent.onRepositoryArmamentActivation();
    } else if (Objects.equals(strDataList.get(1), "DEACTIVATE")) {
      sessionRepoSerialEvent.onRepositoryArmamentDeactivation();
    }
  }

  private void pmkidContext(ArrayList<String> strDataList) {
    switch (strDataList.get(1)) {
      case "FOUND_APS":
        String numberOfAps = strDataList.get(2);
        sessionRepoSerialEvent.onRepositoryNumberOfFoundAccessPoints(numberOfAps);
        break;
      case "SCAN":
        processScannedAccessPointsAndNotifyViewModel(strDataList);
        break;
      case "FINISH_SCAN":
        sessionRepoSerialEvent.onRepositoryFinishScanning();
        break;
      case "AP_NOT_FOUND":
        sessionRepoSerialEvent.onRepositoryAccessPointNotFound();
        break;
      case "LAUNCHING_SEQUENCE":
        sessionRepoSerialEvent.onRepositoryLaunchingSequence();
        break;
      case "SNIFF_STARTED":
        sessionRepoSerialEvent.onRepositoryTaskCreated();
        break;
      case "WRONG_KEY_TYPE":
        String keyType = strDataList.get(3);
        sessionRepoSerialEvent.onRepositoryPmkidWrongKeyType(keyType);
        break;
      case "SNIFF_STATUS":
        int status = Integer.parseInt(strDataList.get(2));
        sessionRepoSerialEvent.onRepositoryTaskStatus("PMKID", status);
        break;
      case "MSG_1":
        String bssid = strDataList.get(2);
        String client = strDataList.get(3);
        String pmkid = strDataList.get(4);

        PmkidFirstMessageData pmkidFirstMessageData = new PmkidFirstMessageData(
                bssid, client, pmkid
        );
        sessionRepoSerialEvent.onRepositoryEapolMessage(
                "PMKID", 1, pmkidFirstMessageData,
                null, null);
        break;
      case "FINISHING_SEQUENCE":
        sessionRepoSerialEvent.onRepositoryFinishingSequence();
        break;
      case "SUCCESS":
        sessionRepoSerialEvent.onRepositorySuccess();
        break;
      case "FAILURE":
        sessionRepoSerialEvent.onRepositoryFailure(strDataList.get(2));
        break;
    }
  }

  private void micContext(ArrayList<String> strDataList) {
    switch (strDataList.get(1)) {
      case "FOUND_APS":
        String numberOfAps = strDataList.get(2);
        sessionRepoSerialEvent.onRepositoryNumberOfFoundAccessPoints(numberOfAps);
        break;
      case "SCAN":
        processScannedAccessPointsAndNotifyViewModel(strDataList);
        break;
      case "FINISH_SCAN":
        sessionRepoSerialEvent.onRepositoryFinishScanning();
        break;
      case "AP_NOT_FOUND":
        sessionRepoSerialEvent.onRepositoryAccessPointNotFound();
        break;
      case "LAUNCHING_SEQUENCE":
        sessionRepoSerialEvent.onRepositoryLaunchingSequence();
        break;
      case "DEAUTH_STARTED":
        sessionRepoSerialEvent.onRepositoryTaskCreated();
        break;
      case "INJECTED_DEAUTH":
        int status = Integer.parseInt(strDataList.get(2));
        sessionRepoSerialEvent.onRepositoryTaskStatus("MIC", status);
        break;
      case "MSG_1":
        String bssid = strDataList.get(2);
        String client = strDataList.get(3);
        String anonce = strDataList.get(4);

        MicFirstMessageData micFirstMessageData = new MicFirstMessageData(bssid, client, anonce);
        sessionRepoSerialEvent.onRepositoryEapolMessage(
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
        sessionRepoSerialEvent.onRepositoryEapolMessage(
                "MIC", 2, null, null,
                micSecondMessageData);
        break;
      case "FINISHING SEQUENCE":
        sessionRepoSerialEvent.onRepositoryFinishingSequence();
        break;
      case "SUCCESS":
        sessionRepoSerialEvent.onRepositorySuccess();
        break;
      case "FAILURE":
        sessionRepoSerialEvent.onRepositoryFailure(strDataList.get(2));
        break;
    }
  }

  private void reconnaissanceContext(ArrayList<String> strDataList) {
    if (Objects.equals(strDataList.get(1), "FOUND_APS")) {

      String numberOfAps = strDataList.get(2);
      sessionRepoSerialEvent.onRepositoryNumberOfFoundAccessPoints(numberOfAps);
    } else if (Objects.equals(strDataList.get(1), "SCAN")) {

      processScannedAccessPointsAndNotifyViewModel(strDataList);
    } else if (Objects.equals(strDataList.get(1), "FINISH_SCAN")) {
      sessionRepoSerialEvent.onRepositoryFinishScanning();
    }
  }

  private void deauthContext(ArrayList<String> strDataList) {
    switch (strDataList.get(1)) {
      case "FOUND_APS":
        String numberOfAps = strDataList.get(2);
        sessionRepoSerialEvent.onRepositoryNumberOfFoundAccessPoints(numberOfAps);
        break;
      case "SCAN":
        processScannedAccessPointsAndNotifyViewModel(strDataList);
        break;
      case "AP_NOT_FOUND":
        sessionRepoSerialEvent.onRepositoryAccessPointNotFound();
        break;
      case "FINISH_SCAN":
        sessionRepoSerialEvent.onRepositoryFinishScanning();
        break;
      case "LAUNCHING_SEQUENCE":
        sessionRepoSerialEvent.onRepositoryLaunchingSequence();
        break;
      case "DEAUTH_STARTED":
        sessionRepoSerialEvent.onRepositoryTaskCreated();
        break;
      case "INJECTED_DEAUTH":
        int numberOfInjectedDeauthentications = Integer.parseInt(strDataList.get(2));
        sessionRepoSerialEvent.onRepositoryTaskStatus("DEAUTH",
                numberOfInjectedDeauthentications);
        break;
      case "STOPPED":
        sessionRepoSerialEvent.onRepositoryDeauthStop(strDataList.get(2));
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
    sessionRepoSerialEvent.onRepositoryScannedAccessPoint(macAddress, ssid, rssi, channel);
  }

  public String connectToDevice(DeviceConnectionParamData deviceConnectionParamData) {
    LauncherStages status = LauncherSingleton.getInstance().getLauncher()
            .connectToDevice(deviceConnectionParamData);

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

  public void disconnectFromDevice() {
    LauncherSingleton.getInstance().getLauncher().disconnectFromDevice();
  }

  public void startEventDrivenReadFromDevice() {
    LauncherSingleton.getInstance().getLauncher().startEventDrivenReadFromDevice();
  }

  public void stopEventDrivenReadFromDevice() {
    LauncherSingleton.getInstance().getLauncher().stopEventDrivenReadFromDevice();
  }

  public void writeDataToDevice(String data) {
    LauncherSingleton.getInstance().getLauncher().writeDataToDevice(data);
  }
}
