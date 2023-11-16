package com.johndeweydev.awps.models.repo.serial.sessionreposerial;

import android.util.Log;

import com.johndeweydev.awps.models.api.launcher.Launcher;
import com.johndeweydev.awps.models.api.launcher.LauncherSingleton;
import com.johndeweydev.awps.models.data.AccessPointData;
import com.johndeweydev.awps.models.data.LauncherOutputData;
import com.johndeweydev.awps.models.data.MicFirstMessageData;
import com.johndeweydev.awps.models.data.MicSecondMessageData;
import com.johndeweydev.awps.models.data.PmkidFirstMessageData;
import com.johndeweydev.awps.models.repo.serial.RepositoryIOEvent;
import com.johndeweydev.awps.models.repo.serial.RepositoryIOControl;
import com.johndeweydev.awps.models.repo.serial.sessionreposerial.attackphase.ExecutionPhase;
import com.johndeweydev.awps.models.repo.serial.sessionreposerial.attackphase.InitializationPhase;
import com.johndeweydev.awps.models.repo.serial.sessionreposerial.attackphase.PostExecutionPhase;
import com.johndeweydev.awps.models.repo.serial.sessionreposerial.attackphase.TargetLockingPhase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class SessionRepoSerial extends RepositoryIOControl implements Launcher.UsbSerialIOEvent {

  public interface RepositoryEvent extends RepositoryIOEvent, InitializationPhase,
          TargetLockingPhase, ExecutionPhase, PostExecutionPhase {}

  private SessionRepoSerial.RepositoryEvent repositoryEvent;
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
    repositoryEvent.onRepoOutputError(error);
  }

  @Override
  public void onUsbSerialInputError(String input) {
    repositoryEvent.onRepoInputError(input);
  }

  public void setEventHandler(SessionRepoSerial.RepositoryEvent repositoryEvent) {
    this.repositoryEvent = repositoryEvent;
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
      repositoryEvent.onRepoOutputFormatted(launcherOutputData);
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

    switch (strDataList.get(0)) {
      case "ESP_STARTED" -> repositoryEvent.onRepoStarted();
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
      case "CURRENT_ARMA" -> repositoryEvent.onRepoArmamentStatus(
              namedArmament, formattedBssid);
      case "TARGET_ARMA_SET" -> repositoryEvent.onRepoInstructionIssued(
              namedArmament, formattedBssid);
    }
  }

  private void armamentContext(ArrayList<String> strDataList) {
    if (Objects.equals(strDataList.get(1), "ACTIVATE")) {
      repositoryEvent.onRepoArmamentActivation();
    } else if (Objects.equals(strDataList.get(1), "DEACTIVATE")) {
      repositoryEvent.onRepoArmamentDeactivation();
    }
  }

  private void pmkidContext(ArrayList<String> strDataList) {
    switch (strDataList.get(1)) {
      case "AP_NOT_FOUND" -> repositoryEvent.onRepoTargetAccessPointNotFound();
      case "LAUNCHING_SEQUENCE" -> repositoryEvent.onRepoLaunchingSequence();
      case "SNIFF_STARTED" -> repositoryEvent.onRepoMainTaskCreated();
      case "WRONG_KEY_TYPE" -> {
        String keyType = strDataList.get(3);
        repositoryEvent.onRepoPmkidWrongKeyType(keyType);
      }
      case "WRONG_OUI" -> repositoryEvent.onRepoPmkidWrongOui(strDataList.get(2));
      case "WRONG_KDE" -> repositoryEvent.onRepoPmkidWrongKde(strDataList.get(2));
      case "SNIFF_STATUS" -> {
        int status = Integer.parseInt(strDataList.get(2));
        repositoryEvent.onRepoMainTaskCurrentStatus("PMKID", status);
      }
      case "MSG_1" -> handlePmkidMessage1(strDataList);
      case "FINISHING_SEQUENCE" -> repositoryEvent.onRepoFinishingSequence();
      case "SUCCESS" -> repositoryEvent.onRepoSuccess();
      case "FAILURE" -> repositoryEvent.onRepoFailure(strDataList.get(2));
    }
  }

  private void handlePmkidMessage1(ArrayList<String> dataList) {
    String bssid = dataList.get(2);
    String client = dataList.get(3);
    String pmkid = dataList.get(4);
    PmkidFirstMessageData pmkidFirstMessageData = new PmkidFirstMessageData(bssid, client, pmkid);
    repositoryEvent.onRepoReceivedEapolMessage(
            "PMKID", 1, pmkidFirstMessageData,
            null, null);
  }

  private void micContext(ArrayList<String> strDataList) {
    switch (strDataList.get(1)) {
      case "AP_NOT_FOUND" -> repositoryEvent.onRepoTargetAccessPointNotFound();
      case "LAUNCHING_SEQUENCE" -> repositoryEvent.onRepoLaunchingSequence();
      case "DEAUTH_STARTED" -> repositoryEvent.onRepoMainTaskCreated();
      case "INJECTED_DEAUTH" -> {
        int status = Integer.parseInt(strDataList.get(2));
        repositoryEvent.onRepoMainTaskCurrentStatus("MIC", status);
      }
      case "MSG_1" -> handleMicMessage1(strDataList);
      case "MSG_2" -> handleMicMessage2(strDataList);
      case "FINISHING SEQUENCE" -> repositoryEvent.onRepoFinishingSequence();
      case "SUCCESS" -> repositoryEvent.onRepoSuccess();
      case "FAILURE" -> repositoryEvent.onRepoFailure(strDataList.get(2));
    }
  }

  private void handleMicMessage1(ArrayList<String> dataList) {
    String bssid = dataList.get(2);
    String client = dataList.get(3);
    String anonce = dataList.get(4);
    MicFirstMessageData micFirstMessageData = new MicFirstMessageData(bssid, client, anonce);
    repositoryEvent.onRepoReceivedEapolMessage(
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

    repositoryEvent.onRepoReceivedEapolMessage("MIC", 2,
            null, null, micSecondMessageData);
  }

  private void reconnaissanceContext(ArrayList<String> strDataList) {

    switch (strDataList.get(1)) {
      case "FOUND_APS" -> {
        String numberOfAps = strDataList.get(2);
        repositoryEvent.onRepoNumberOfFoundAccessPoints(numberOfAps);
      }
      case "SCAN" -> processScannedAccessPointsAndNotifyViewModel(strDataList);
      case "FINISH_SCAN" -> repositoryEvent.onRepoFinishScan();
    }
  }

  private void deauthContext(ArrayList<String> strDataList) {
    switch (strDataList.get(1)) {
      case "AP_NOT_FOUND" -> repositoryEvent.onRepoTargetAccessPointNotFound();
      case "FINISH_SCAN" -> repositoryEvent.onRepoFinishScan();
      case "LAUNCHING_SEQUENCE" -> repositoryEvent.onRepoLaunchingSequence();
      case "DEAUTH_STARTED" -> repositoryEvent.onRepoMainTaskCreated();
      case "INJECTED_DEAUTH" -> {
        int numberOfInjectedDeauthentications = Integer.parseInt(strDataList.get(2));
        repositoryEvent.onRepoMainTaskCurrentStatus("DEAUTH",
                numberOfInjectedDeauthentications);
      }
      case "STOPPED" ->
              repositoryEvent.onRepoMainTaskInDeautherStopped(strDataList.get(2));
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
    repositoryEvent.onRepoFoundAccessPoint(accessPointData);
  }

}
