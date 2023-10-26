package com.johndeweydev.awps.repository.sessionrepository;

import com.johndeweydev.awps.repository.LauncherSerialDataEvent;
import com.johndeweydev.awps.repository.sessionrepository.models.MicFirstMessageModel;
import com.johndeweydev.awps.repository.sessionrepository.models.MicSecondMessageModel;
import com.johndeweydev.awps.repository.sessionrepository.models.PmkidFirstMessageModel;
import com.johndeweydev.awps.repository.sessionrepository.models.SessionOutputModel;
import com.johndeweydev.awps.usbserial.UsbSerialMainSingleton;
import com.johndeweydev.awps.usbserial.UsbSerialStatus;
import com.johndeweydev.awps.viewmodels.sessionviewmodel.SessionEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class SessionRepository {

  private final StringBuilder queueData = new StringBuilder();
  private SessionEvent sessionEvent;

  LauncherSerialDataEvent launcherSerialDataEvent = new LauncherSerialDataEvent() {
    @Override
    public void onSerialOutput(String data) {
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
    public void onSerialOutputError(String errorMessageOnNewData) {
      sessionEvent.onSerialOutputError(errorMessageOnNewData);
    }

    @Override
    public void onSerialInputError(String dataToWrite) {
      sessionEvent.onSerialInputError(dataToWrite);
    }
  };

  public void setSessionEvent(SessionEvent sessionEvent) {
    this.sessionEvent = sessionEvent;
    UsbSerialMainSingleton.getInstance().getUsbSerialMain().setLauncherSerialDataEvent(
            launcherSerialDataEvent);
  }

  private void processFormattedOutput() {
    String strData = queueData.toString();

    SessionOutputModel sessionOutputModel = new SessionOutputModel(strData);
    sessionEvent.onSerialOutputRaw(sessionOutputModel);

    char firstChar = strData.charAt(0);
    char lastChar = strData.charAt(strData.length() - 2);
    if (firstChar == '{' && lastChar == '}') {
      sessionEvent.onSerialOutputFormatted(sessionOutputModel);
      String[] splitStrData = strData.split(",");

      ArrayList<String> strDataList = new ArrayList<>(Arrays.asList(splitStrData));
      processContentOfFormattedOutput(strDataList);
    }

  }

  private void processContentOfFormattedOutput(ArrayList<String> strDataList) {

    switch (strDataList.get(0)) {
      case "ESP_STARTED":
        sessionEvent.onLauncherStarted();
      case "CMD_PARSER":
        cmdParserContext(strDataList);
      case "ARMAMENT":
        armamentContext(strDataList);
      case "PMKID":
        pmkidContext(strDataList);
      case "MIC":
        micContext(strDataList);
      case "DEAUTH":
        deauthContext(strDataList);
      case "RECONNAISSANCE":
        reconnaissanceContext(strDataList);
    }
  }

  private void cmdParserContext(ArrayList<String> strDataList) {
    switch (strDataList.get(1)) {
      case "CURRENT_ARMA":
        String currentArmament = strDataList.get(2);
        String currentBssidTarget = strDataList.get(3);
        sessionEvent.onLauncherCommandParserCurrentArma(
                currentArmament, currentBssidTarget);
      case "TARGET_ARMA_SET":
        String armament = strDataList.get(2);
        String bssidTarget = strDataList.get(3);
        sessionEvent.onLauncherCommandParserTargetAndArmaSet(armament, bssidTarget);
    }
  }

  private void armamentContext(ArrayList<String> strDataList) {
    if (Objects.equals(strDataList.get(1), "ACTIVATE")) {
      sessionEvent.onLauncherArmamentActivation();
    } else if (Objects.equals(strDataList.get(1), "DEACTIVATE")) {
      sessionEvent.onLauncherArmamentDeactivation();
    }
  }

  private void pmkidContext(ArrayList<String> strDataList) {
    switch (strDataList.get(1)) {
      case "FOUND_APS":
        String numberOfAps = strDataList.get(2);
        sessionEvent.onLauncherNumberOfFoundAccessPoints(numberOfAps);
      case "SCAN":
        processScannedAccessPointsAndNotifyViewModel(strDataList);
      case "FINISH_SCAN":
        sessionEvent.onLauncherFinishScanning();
      case "AP_NOT_FOUND":
        sessionEvent.onLauncherAccessPointNotFound();
      case "LAUNCHING_SEQUENCE":
        sessionEvent.onLauncherLaunchingSequence();
      case "SNIFF_STARTED":
        sessionEvent.onLauncherTaskCreated();
      case "WRONG_KEY_TYPE":
        String keyType = strDataList.get(3);
        sessionEvent.onLauncherPmkidWrongKeyType(keyType);
      case "SNIFF_STATUS":
        int status = Integer.parseInt(strDataList.get(2));
        sessionEvent.onLauncherTaskStatus("PMKID", status);
      case "MSG_1":
        String bssid = strDataList.get(2);
        String client = strDataList.get(3);
        String pmkid = strDataList.get(4);

        PmkidFirstMessageModel pmkidFirstMessageModel = new PmkidFirstMessageModel(
                bssid, client, pmkid
        );
        sessionEvent.onLauncherEapolMessage(
                "PMKID", 1, pmkidFirstMessageModel,
                null, null);
      case "FINISHING_SEQUENCE":
        sessionEvent.onLauncherFinishingSequence();
      case "SUCCESS":
        sessionEvent.onLauncherSuccess();
      case "FAILURE":
        sessionEvent.onLauncherFailure(strDataList.get(2));
    }
  }

  private void micContext(ArrayList<String> strDataList) {
    switch (strDataList.get(1)) {
      case "FOUND_APS":
        String numberOfAps = strDataList.get(2);
        sessionEvent.onLauncherNumberOfFoundAccessPoints(numberOfAps);
      case "SCAN":
        processScannedAccessPointsAndNotifyViewModel(strDataList);
      case "FINISH_SCAN":
        sessionEvent.onLauncherFinishScanning();
      case "AP_NOT_FOUND":
        sessionEvent.onLauncherAccessPointNotFound();
      case "LAUNCHING_SEQUENCE":
        sessionEvent.onLauncherLaunchingSequence();
      case "DEAUTH_STARTED":
        sessionEvent.onLauncherTaskCreated();
      case "INJECTED_DEAUTH":
        int status = Integer.parseInt(strDataList.get(2));
        sessionEvent.onLauncherTaskStatus("MIC", status);
      case "MSG_1":
        String bssid = strDataList.get(2);
        String client = strDataList.get(3);
        String anonce = strDataList.get(4);

        MicFirstMessageModel micFirstMessageModel = new MicFirstMessageModel(bssid, client, anonce);
        sessionEvent.onLauncherEapolMessage(
                "MIC", 1, null,
                micFirstMessageModel, null);
      case "MSG_2":
        String secondMessageInfo = strDataList.get(4) + strDataList.get(5) + strDataList.get(6) +
                strDataList.get(7) + strDataList.get(8) + strDataList.get(9);

        String clientM2 = strDataList.get(2);
        String bssidM2 = strDataList.get(3);
        String replayCounter = strDataList.get(10);
        String snonce = strDataList.get(11);
        String mic = strDataList.get(12);
        String wpaKeyData = strDataList.get(13);

        MicSecondMessageModel micSecondMessageModel = new MicSecondMessageModel(
                clientM2, bssidM2, secondMessageInfo, replayCounter, snonce, mic, wpaKeyData
        );
        sessionEvent.onLauncherEapolMessage(
                "MIC", 2, null, null,
                micSecondMessageModel);
      case "FINISHING SEQUENCE":
        sessionEvent.onLauncherFinishingSequence();
      case "SUCCESS":
        sessionEvent.onLauncherSuccess();
      case "FAILURE":
        sessionEvent.onLauncherFailure(strDataList.get(2));
    }
  }

  private void reconnaissanceContext(ArrayList<String> strDataList) {
    if (Objects.equals(strDataList.get(1), "FOUND_APS")) {

      String numberOfAps = strDataList.get(2);
      sessionEvent.onLauncherNumberOfFoundAccessPoints(numberOfAps);
    } else if (Objects.equals(strDataList.get(1), "SCAN")) {

      processScannedAccessPointsAndNotifyViewModel(strDataList);
    } else if (Objects.equals(strDataList.get(1), "FINISH_SCAN")) {
      sessionEvent.onLauncherFinishScanning();
    }
  }

  private void deauthContext(ArrayList<String> strDataList) {
    switch (strDataList.get(1)) {
      case "FOUND_APS":
        String numberOfAps = strDataList.get(2);
        sessionEvent.onLauncherNumberOfFoundAccessPoints(numberOfAps);
      case "SCAN":
        processScannedAccessPointsAndNotifyViewModel(strDataList);
      case "AP_NOT_FOUND":
        sessionEvent.onLauncherAccessPointNotFound();
      case "FINISH_SCAN":
        sessionEvent.onLauncherFinishScanning();
      case "LAUNCHING_SEQUENCE":
        sessionEvent.onLauncherLaunchingSequence();
      case "DEAUTH_STARTED":
        sessionEvent.onLauncherTaskCreated();
      case "INJECTED_DEAUTH":
        int numberOfInjectedDeauthentications = Integer.parseInt(strDataList.get(2));
        sessionEvent.onLauncherTaskStatus("DEAUTH", numberOfInjectedDeauthentications);
      case "STOPPED":
        sessionEvent.onLauncherDeauthStop(strDataList.get(2));
    }
  }

  private void processScannedAccessPointsAndNotifyViewModel(
          ArrayList<String> strDataList
  ) {
    String macAddress = strDataList.get(2);
    String rssi = strDataList.get(3);
    String channel = strDataList.get(4);
    String ssid = strDataList.get(5);
    sessionEvent.onLauncherScannedAccessPoint(macAddress, ssid, rssi, channel);
  }

  public UsbSerialStatus connect(
          int baudRate, int dataBits, int stopBits, int parity, int deviceId, int portNum) {
    return UsbSerialMainSingleton.getInstance().getUsbSerialMain().connect(
            baudRate, dataBits, stopBits, parity, deviceId, portNum
    );
  }

  public void disconnect() {
    UsbSerialMainSingleton.getInstance().getUsbSerialMain().disconnect();
  }

  public void startReading() {
    UsbSerialMainSingleton.getInstance().getUsbSerialMain().startReading();
  }

  public void stopReading() {
    UsbSerialMainSingleton.getInstance().getUsbSerialMain().stopReading();
  }

  public void writeData(String data) {
    UsbSerialMainSingleton.getInstance().getUsbSerialMain().writeData(data);
  }
}
