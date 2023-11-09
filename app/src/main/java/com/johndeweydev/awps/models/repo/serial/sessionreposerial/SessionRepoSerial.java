package com.johndeweydev.awps.models.repo.serial.sessionreposerial;

import android.util.Log;

import com.johndeweydev.awps.models.data.AccessPointData;
import com.johndeweydev.awps.models.data.LauncherOutputData;
import com.johndeweydev.awps.models.data.MicFirstMessageData;
import com.johndeweydev.awps.models.data.MicSecondMessageData;
import com.johndeweydev.awps.models.data.PmkidFirstMessageData;
import com.johndeweydev.awps.models.api.launcher.LauncherSingleton;
import com.johndeweydev.awps.models.repo.serial.RepoIOEvent;
import com.johndeweydev.awps.models.repo.serial.RepoIOControl;
import com.johndeweydev.awps.viewmodels.sessionviewmodel.SessionViewModelEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class SessionRepoSerial extends RepoIOControl implements RepoIOEvent {

  private SessionViewModelEvent sessionViewModelEvent;
  private final StringBuilder queueData = new StringBuilder();
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
  public void onUsbSerialOutputError(String error) {
    sessionViewModelEvent.onLauncherOutputError(error);
  }

  @Override
  public void onUsbSerialInputError(String input) {
    sessionViewModelEvent.onLauncherInputError(input);
  }

  public void setEventHandler(SessionViewModelEvent sessionViewModelEvent) {
    this.sessionViewModelEvent = sessionViewModelEvent;
    Log.d("dev-log", "SessionRepository.setEventHandler: Session repository event " +
            "callback set");
  }

  public void setLauncherEventHandler() {
    LauncherSingleton.getInstance().getLauncher().setLauncherEventHandler(
            this);
    Log.d("dev-log", "SessionRepository.setLauncherEventHandler: Launcher event " +
            "callback set in the context of session repository");
  }

  private void processFormattedOutput() {
    String data = queueData.toString();
    String time = createStringTime();

    LauncherOutputData launcherOutputData = new LauncherOutputData(time, data);

    char firstChar = data.charAt(0);
    char lastChar = data.charAt(data.length() - 2);
    if (firstChar == '{' && lastChar == '}') {
      sessionViewModelEvent.onLauncherOutputFormatted(launcherOutputData);
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
      case "ESP_STARTED" -> sessionViewModelEvent.onLauncherStarted();
      case "CMD_PARSER" -> cmdParserContext(strDataList);
      case "ARMAMENT" -> armamentContext(strDataList);
      case "PMKID" -> pmkidContext(strDataList);
      case "MIC" -> micContext(strDataList);
      case "DEAUTH" -> deauthContext(strDataList);
      case "RECONNAISSANCE" -> reconnaissanceContext(strDataList);
    }
  }

  private void cmdParserContext(ArrayList<String> strDataList) {
    String currentArmament = strDataList.get(2);
    String currentBssidTarget = strDataList.get(3);

    String namedArmament = switch (currentArmament) {
      case "01" -> "Reconnaissance";
      case "02" -> "PMKID";
      case "03" -> "MIC";
      case "04" -> "Deauth";
      default -> "";
    };

    String formattedBssid = currentBssidTarget.substring(0, 2) + ":" +
            currentBssidTarget.substring(2, 4) + ":" +
            currentBssidTarget.substring(4, 6) + ":" +
            currentBssidTarget.substring(6, 8) + ":" +
            currentBssidTarget.substring(8, 10) + ":" +
            currentBssidTarget.substring(10, 12);

    switch (strDataList.get(1)) {
      case "CURRENT_ARMA" -> sessionViewModelEvent.onLauncherArmamentStatus(
              namedArmament, formattedBssid);
      case "TARGET_ARMA_SET" -> sessionViewModelEvent.onLauncherInstructionIssued(
              namedArmament, formattedBssid);
    }
  }

  private void armamentContext(ArrayList<String> strDataList) {
    if (Objects.equals(strDataList.get(1), "ACTIVATE")) {
      sessionViewModelEvent.onLauncherArmamentActivation();
    } else if (Objects.equals(strDataList.get(1), "DEACTIVATE")) {
      sessionViewModelEvent.onLauncherArmamentDeactivation();
    }
  }

  private void pmkidContext(ArrayList<String> strDataList) {
    switch (strDataList.get(1)) {
      case "FOUND_APS" -> {
        String numberOfAps = strDataList.get(2);
        sessionViewModelEvent.onLauncherNumberOfFoundAccessPoints(numberOfAps);
      }
      case "SCAN" -> processScannedAccessPointsAndNotifyViewModel(strDataList);
      case "FINISH_SCAN" -> sessionViewModelEvent.onLauncherFinishScan();
      case "AP_NOT_FOUND" -> sessionViewModelEvent.onLauncherTargetAccessPointNotFound();
      case "LAUNCHING_SEQUENCE" -> sessionViewModelEvent.onLauncherLaunchingSequence();
      case "SNIFF_STARTED" -> sessionViewModelEvent.onLauncherMainTaskCreated();
      case "WRONG_KEY_TYPE" -> {
        String keyType = strDataList.get(3);
        sessionViewModelEvent.onLauncherPmkidWrongKeyType(keyType);
      }
      case "WRONG_OUI" -> sessionViewModelEvent.onLauncherPmkidWrongOui(strDataList.get(2));
      case "WRONG_KDE" -> sessionViewModelEvent.onLauncherPmkidWrongKde(strDataList.get(2));
      case "SNIFF_STATUS" -> {
        int status = Integer.parseInt(strDataList.get(2));
        sessionViewModelEvent.onLauncherMainTaskCurrentStatus("PMKID", status);
      }
      case "MSG_1" -> handlePmkidMessage1(strDataList);
      case "FINISHING_SEQUENCE" -> sessionViewModelEvent.onLauncherFinishingSequence();
      case "SUCCESS" -> sessionViewModelEvent.onLauncherSuccess();
      case "FAILURE" -> sessionViewModelEvent.onLauncherFailure(strDataList.get(2));
    }
  }

  private void handlePmkidMessage1(ArrayList<String> dataList) {
    String bssid = dataList.get(2);
    String client = dataList.get(3);
    String pmkid = dataList.get(4);
    PmkidFirstMessageData pmkidFirstMessageData = new PmkidFirstMessageData(bssid, client, pmkid);
    sessionViewModelEvent.onLauncherReceivedEapolMessage(
            "PMKID", 1, pmkidFirstMessageData,
            null, null);
  }

  private void micContext(ArrayList<String> strDataList) {
    switch (strDataList.get(1)) {
      case "FOUND_APS" -> {
        String numberOfAps = strDataList.get(2);
        sessionViewModelEvent.onLauncherNumberOfFoundAccessPoints(numberOfAps);
      }
      case "SCAN" -> processScannedAccessPointsAndNotifyViewModel(strDataList);
      case "FINISH_SCAN" -> sessionViewModelEvent.onLauncherFinishScan();
      case "AP_NOT_FOUND" -> sessionViewModelEvent.onLauncherTargetAccessPointNotFound();
      case "LAUNCHING_SEQUENCE" -> sessionViewModelEvent.onLauncherLaunchingSequence();
      case "DEAUTH_STARTED" -> sessionViewModelEvent.onLauncherMainTaskCreated();
      case "INJECTED_DEAUTH" -> {
        int status = Integer.parseInt(strDataList.get(2));
        sessionViewModelEvent.onLauncherMainTaskCurrentStatus("MIC", status);
      }
      case "MSG_1" -> handleMicMessage1(strDataList);
      case "MSG_2" -> handleMicMessage2(strDataList);
      case "FINISHING SEQUENCE" -> sessionViewModelEvent.onLauncherFinishingSequence();
      case "SUCCESS" -> sessionViewModelEvent.onLauncherSuccess();
      case "FAILURE" -> sessionViewModelEvent.onLauncherFailure(strDataList.get(2));
    }
  }

  private void handleMicMessage1(ArrayList<String> dataList) {
    String bssid = dataList.get(2);
    String client = dataList.get(3);
    String anonce = dataList.get(4);
    MicFirstMessageData micFirstMessageData = new MicFirstMessageData(bssid, client, anonce);
    sessionViewModelEvent.onLauncherReceivedEapolMessage(
            "MIC", 1, null,
            micFirstMessageData, null);
  }

  private void handleMicMessage2(ArrayList<String> dataList) {
    String version = dataList.get(4);
    String type = dataList.get(5);
    String length = dataList.get(6);
    String keyDescriptionType = dataList.get(7);
    String keyInformation = dataList.get(8);
    String keyLength = dataList.get(9);

    String replayCounter = dataList.get(10);
    String snonce = dataList.get(11);
    String keyIv = dataList.get(12);
    String keyRsc = dataList.get(13);
    String keyId = dataList.get(14);
    String mic = dataList.get(15);

    String keyDataLength = dataList.get(16);
    String keyData = dataList.get(17);

    MicSecondMessageData micSecondMessageData = new MicSecondMessageData(
            version, type, length, keyDescriptionType, keyInformation, keyLength,
            replayCounter, snonce, keyIv, keyRsc, keyId, mic, keyDataLength, keyData);

    sessionViewModelEvent.onLauncherReceivedEapolMessage("MIC", 2,
            null, null, micSecondMessageData);
  }

  private void reconnaissanceContext(ArrayList<String> strDataList) {

    switch (strDataList.get(1)) {
      case "FOUND_APS" -> {
        String numberOfAps = strDataList.get(2);
        sessionViewModelEvent.onLauncherNumberOfFoundAccessPoints(numberOfAps);
      }
      case "SCAN" -> processScannedAccessPointsAndNotifyViewModel(strDataList);
      case "FINISH_SCAN" -> sessionViewModelEvent.onLauncherFinishScan();
    }
  }

  private void deauthContext(ArrayList<String> strDataList) {
    switch (strDataList.get(1)) {
      case "FOUND_APS" -> {
        String numberOfAps = strDataList.get(2);
        sessionViewModelEvent.onLauncherNumberOfFoundAccessPoints(numberOfAps);
      }
      case "SCAN" -> processScannedAccessPointsAndNotifyViewModel(strDataList);
      case "AP_NOT_FOUND" -> sessionViewModelEvent.onLauncherTargetAccessPointNotFound();
      case "FINISH_SCAN" -> sessionViewModelEvent.onLauncherFinishScan();
      case "LAUNCHING_SEQUENCE" -> sessionViewModelEvent.onLauncherLaunchingSequence();
      case "DEAUTH_STARTED" -> sessionViewModelEvent.onLauncherMainTaskCreated();
      case "INJECTED_DEAUTH" -> {
        int numberOfInjectedDeauthentications = Integer.parseInt(strDataList.get(2));
        sessionViewModelEvent.onLauncherMainTaskCurrentStatus("DEAUTH",
                numberOfInjectedDeauthentications);
      }
      case "STOPPED" ->
              sessionViewModelEvent.onLauncherMainTaskInDeautherStopped(strDataList.get(2));
    }
  }

  private void processScannedAccessPointsAndNotifyViewModel(
          ArrayList<String> strDataList
  ) {
    String macAddress = strDataList.get(2);
    String ssid = strDataList.get(3);
    StringBuilder asciiSsid = new StringBuilder();

    // Decodes the SSID from hexadecimal string to ascii characters
    for (int i = 0; i < ssid.length(); i += 2) {
      String hex = ssid.substring(i, i + 2);
      int decimal = Integer.parseInt(hex, 16);
      asciiSsid.append((char) decimal);
    }

    String rssi = strDataList.get(4);
    String channel = strDataList.get(5);
    AccessPointData accessPointData = new AccessPointData(
            macAddress, asciiSsid.toString(), Integer.parseInt(rssi), Integer.parseInt(channel)
    );
    sessionViewModelEvent.onLauncherFoundAccessPoint(accessPointData);
  }

}
