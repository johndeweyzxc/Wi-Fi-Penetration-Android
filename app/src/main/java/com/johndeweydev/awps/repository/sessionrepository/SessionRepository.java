package com.johndeweydev.awps.repository.sessionrepository;

import com.johndeweydev.awps.repository.UsbSerialDataEvent;
import com.johndeweydev.awps.repository.UsbSerialOutputModel;
import com.johndeweydev.awps.repository.sessionrepository.models.MicFirstMessageModel;
import com.johndeweydev.awps.repository.sessionrepository.models.MicSecondMessageModel;
import com.johndeweydev.awps.repository.sessionrepository.models.PmkidFirstMessageModel;
import com.johndeweydev.awps.usbserial.UsbSerialMainSingleton;
import com.johndeweydev.awps.usbserial.UsbSerialStatus;
import com.johndeweydev.awps.viewmodels.sessionviewmodel.SessionRepositoryEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class SessionRepository {

  private final StringBuilder queueData = new StringBuilder();
  private SessionRepositoryEvent sessionRepositoryEvent;

  UsbSerialDataEvent usbSerialDataEvent = new UsbSerialDataEvent() {
    @Override
    public void onUsbSerialOutput(String data) {
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
    public void onUsbOutputError(String error) {
      sessionRepositoryEvent.onRepositoryOutputError(error);
    }

    @Override
    public void onUsbInputError(String input) {
      sessionRepositoryEvent.onRepositoryInputError(input);
    }
  };

  public void setSessionEvent(SessionRepositoryEvent sessionRepositoryEvent) {
    this.sessionRepositoryEvent = sessionRepositoryEvent;
    UsbSerialMainSingleton.getInstance().getUsbSerialMain().setLauncherSerialDataEvent(
            usbSerialDataEvent);
  }

  private void processFormattedOutput() {
    String data = queueData.toString();
    String time = createStringTime();

    UsbSerialOutputModel usbSerialOutputModel = new UsbSerialOutputModel(time, data);
    sessionRepositoryEvent.onRepositoryOutputRaw(usbSerialOutputModel);

    char firstChar = data.charAt(0);
    char lastChar = data.charAt(data.length() - 2);
    if (firstChar == '{' && lastChar == '}') {
      sessionRepositoryEvent.onRepositoryOutputFormatted(usbSerialOutputModel);
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

    switch (strDataList.get(0)) {
      case "ESP_STARTED":
        sessionRepositoryEvent.onRepositoryStarted();
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
        sessionRepositoryEvent.onRepositoryCommandParserCurrentArma(
                currentArmament, currentBssidTarget);
      case "TARGET_ARMA_SET":
        String armament = strDataList.get(2);
        String bssidTarget = strDataList.get(3);
        sessionRepositoryEvent.onRepositoryCommandParserTargetAndArmaSet(armament, bssidTarget);
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
      case "SCAN":
        processScannedAccessPointsAndNotifyViewModel(strDataList);
      case "FINISH_SCAN":
        sessionRepositoryEvent.onRepositoryFinishScanning();
      case "AP_NOT_FOUND":
        sessionRepositoryEvent.onRepositoryAccessPointNotFound();
      case "LAUNCHING_SEQUENCE":
        sessionRepositoryEvent.onRepositoryLaunchingSequence();
      case "SNIFF_STARTED":
        sessionRepositoryEvent.onRepositoryTaskCreated();
      case "WRONG_KEY_TYPE":
        String keyType = strDataList.get(3);
        sessionRepositoryEvent.onRepositoryPmkidWrongKeyType(keyType);
      case "SNIFF_STATUS":
        int status = Integer.parseInt(strDataList.get(2));
        sessionRepositoryEvent.onRepositoryTaskStatus("PMKID", status);
      case "MSG_1":
        String bssid = strDataList.get(2);
        String client = strDataList.get(3);
        String pmkid = strDataList.get(4);

        PmkidFirstMessageModel pmkidFirstMessageModel = new PmkidFirstMessageModel(
                bssid, client, pmkid
        );
        sessionRepositoryEvent.onRepositoryEapolMessage(
                "PMKID", 1, pmkidFirstMessageModel,
                null, null);
      case "FINISHING_SEQUENCE":
        sessionRepositoryEvent.onRepositoryFinishingSequence();
      case "SUCCESS":
        sessionRepositoryEvent.onRepositorySuccess();
      case "FAILURE":
        sessionRepositoryEvent.onRepositoryFailure(strDataList.get(2));
    }
  }

  private void micContext(ArrayList<String> strDataList) {
    switch (strDataList.get(1)) {
      case "FOUND_APS":
        String numberOfAps = strDataList.get(2);
        sessionRepositoryEvent.onRepositoryNumberOfFoundAccessPoints(numberOfAps);
      case "SCAN":
        processScannedAccessPointsAndNotifyViewModel(strDataList);
      case "FINISH_SCAN":
        sessionRepositoryEvent.onRepositoryFinishScanning();
      case "AP_NOT_FOUND":
        sessionRepositoryEvent.onRepositoryAccessPointNotFound();
      case "LAUNCHING_SEQUENCE":
        sessionRepositoryEvent.onRepositoryLaunchingSequence();
      case "DEAUTH_STARTED":
        sessionRepositoryEvent.onRepositoryTaskCreated();
      case "INJECTED_DEAUTH":
        int status = Integer.parseInt(strDataList.get(2));
        sessionRepositoryEvent.onRepositoryTaskStatus("MIC", status);
      case "MSG_1":
        String bssid = strDataList.get(2);
        String client = strDataList.get(3);
        String anonce = strDataList.get(4);

        MicFirstMessageModel micFirstMessageModel = new MicFirstMessageModel(bssid, client, anonce);
        sessionRepositoryEvent.onRepositoryEapolMessage(
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
        sessionRepositoryEvent.onRepositoryEapolMessage(
                "MIC", 2, null, null,
                micSecondMessageModel);
      case "FINISHING SEQUENCE":
        sessionRepositoryEvent.onRepositoryFinishingSequence();
      case "SUCCESS":
        sessionRepositoryEvent.onRepositorySuccess();
      case "FAILURE":
        sessionRepositoryEvent.onRepositoryFailure(strDataList.get(2));
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
      case "SCAN":
        processScannedAccessPointsAndNotifyViewModel(strDataList);
      case "AP_NOT_FOUND":
        sessionRepositoryEvent.onRepositoryAccessPointNotFound();
      case "FINISH_SCAN":
        sessionRepositoryEvent.onRepositoryFinishScanning();
      case "LAUNCHING_SEQUENCE":
        sessionRepositoryEvent.onRepositoryLaunchingSequence();
      case "DEAUTH_STARTED":
        sessionRepositoryEvent.onRepositoryTaskCreated();
      case "INJECTED_DEAUTH":
        int numberOfInjectedDeauthentications = Integer.parseInt(strDataList.get(2));
        sessionRepositoryEvent.onRepositoryTaskStatus("DEAUTH", numberOfInjectedDeauthentications);
      case "STOPPED":
        sessionRepositoryEvent.onRepositoryDeauthStop(strDataList.get(2));
    }
  }

  private void processScannedAccessPointsAndNotifyViewModel(
          ArrayList<String> strDataList
  ) {
    String macAddress = strDataList.get(2);
    String rssi = strDataList.get(3);
    String channel = strDataList.get(4);
    String ssid = strDataList.get(5);
    sessionRepositoryEvent.onRepositoryScannedAccessPoint(macAddress, ssid, rssi, channel);
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
