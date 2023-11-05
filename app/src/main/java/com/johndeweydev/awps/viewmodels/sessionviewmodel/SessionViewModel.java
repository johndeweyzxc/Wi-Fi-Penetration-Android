package com.johndeweydev.awps.viewmodels.sessionviewmodel;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.johndeweydev.awps.models.data.AccessPointData;
import com.johndeweydev.awps.models.data.DeviceConnectionParamData;
import com.johndeweydev.awps.models.data.HashInfoEntity;
import com.johndeweydev.awps.models.data.LauncherOutputData;
import com.johndeweydev.awps.models.repo.serial.sessionreposerial.SessionRepoSerial;
import com.johndeweydev.awps.models.data.MicFirstMessageData;
import com.johndeweydev.awps.models.data.MicSecondMessageData;
import com.johndeweydev.awps.models.data.PmkidFirstMessageData;
import com.johndeweydev.awps.viewmodels.ViewModelIOControl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class SessionViewModel extends ViewModel implements ViewModelIOControl,
        SessionViewModelEvent {

  public boolean automaticAttack = false;
  public String selectedArmament;
  public SessionRepoSerial sessionRepoSerial;

  /**
   * Serial Listeners
   * The variables and live data below this comment is use for logging and setting up listeners
   * when an error occurred
   * */
  public MutableLiveData<String> currentAttackLog = new MutableLiveData<>();
  public MutableLiveData<String> currentSerialInputError = new MutableLiveData<>();
  public MutableLiveData<String> currentSerialOutputError = new MutableLiveData<>();
  public int attackLogNumber = 0;

  /**
   * INITIALIZATION PHASE
   * Variables and live data below this comment is use for setting up the target and activating
   * the armament
   * */
  public MutableLiveData<String> launcherStarted = new MutableLiveData<>();
  public MutableLiveData<String> launcherActivateConfirmation = new MutableLiveData<>();
  public String targetAccessPoint;
  public String targetAccessPointSsid;

  /**
   * TARGET LOCKING PHASE
   * Variables and live data below this comment is use for scanning access points and checking if
   * the target is found on the scanned access points
   * */
  public boolean userWantsToScanForAccessPoint = false;
  public ArrayList<AccessPointData> accessPointDataList = new ArrayList<>();
  public MutableLiveData<ArrayList<AccessPointData>> launcherFinishScanning =
          new MutableLiveData<>();
  public MutableLiveData<String> launcherAccessPointNotFound = new MutableLiveData<>();

  /**
   * EXECUTION PHASE
   * Variables and live data below this comment is use when the attack is ongoing
   * */
  public MutableLiveData<String> launcherMainTaskCreated = new MutableLiveData<>();
  public boolean attackOnGoing = false;

  /**
   * POST EXECUTION PHASE
   * Variables and live data below this comment is use when the attack has finished, an attack can
   * either be successfully or a failure
   * */
  public HashInfoEntity launcherExecutionResultData;
  public MutableLiveData<String> launcherExecutionResult = new MutableLiveData<>();

  public SessionViewModel(SessionRepoSerial sessionRepoSerial) {
    Log.d("dev-log", "SessionViewModel: Created new instance of SessionViewModel");
    this.sessionRepoSerial = sessionRepoSerial;
    sessionRepoSerial.setEventHandler(this);
  }

  public void writeControlCodeActivationToLauncher() {
    sessionRepoSerial.writeDataToDevice("06");
  }

  public void writeControlCodeDeactivationToLauncher() {
    sessionRepoSerial.writeDataToDevice("07");
  }

  public void writeControlCodeRestartLauncher() {
    sessionRepoSerial.writeDataToDevice("08");
  }

  public void writeControlCodeStopRunningAttack() {
    sessionRepoSerial.writeDataToDevice("07");
  }

  public void writeInstructionCodeForScanningDevicesToLauncher() {
    sessionRepoSerial.writeDataToDevice("01");
  }

  public void writeInstructionCodeToLauncher(String data) {
    String instructionCode = "";
    switch (selectedArmament) {
      case "PMKID Based Attack" -> instructionCode += "02";
      case "MIC Based Attack" -> instructionCode += "03";
      case "Deauther" -> instructionCode += "04";
    }
    instructionCode += data;
    sessionRepoSerial.writeDataToDevice(instructionCode);
  }

  @Override
  public void setLauncherEventHandler() {
    sessionRepoSerial.setLauncherEventHandler();
  }

  @Override
  public String connectToDevice(DeviceConnectionParamData deviceConnectionParamData) {
    return sessionRepoSerial.connectToDevice(deviceConnectionParamData);
  }

  @Override
  public void disconnectFromDevice() {
    sessionRepoSerial.disconnectFromDevice();
  }

  @Override
  public void startEventDrivenReadFromDevice() {
    sessionRepoSerial.startEventDrivenReadFromDevice();
  }

  @Override
  public void stopEventDrivenReadFromDevice() {
    sessionRepoSerial.stopEventDrivenReadFromDevice();
  }

  @Override
  public void onLauncherOutputFormatted(LauncherOutputData launcherOutputData) {
    Log.d("dev-log", "SessionViewModel.onLauncherOutputFormatted: Serial -> " +
            launcherOutputData.getOutput());
  }

  @Override
  public void onLauncherOutputError(String error) {
    Log.d("dev-log", "SessionViewModel.onLauncherOutputError: Serial -> " + error);
    currentSerialOutputError.postValue(error);
  }

  @Override
  public void onLauncherInputError(String input) {
    Log.d("dev-log", "SessionViewModel.onLauncherInputError: Serial -> " + input);
    currentSerialInputError.postValue(input);
  }

  @Override
  public void onLauncherStarted() {
    launcherStarted.postValue("Launcher module started");
    currentAttackLog.postValue("(" + attackLogNumber + ") " + "Module started");
    attackLogNumber++;
  }

  @Override
  public void onLauncherArmamentStatus(String armament, String targetBssid) {
    currentAttackLog.postValue("(" + attackLogNumber + ") " + "Using " + armament + "" +
            ", targeting " + targetBssid);
    attackLogNumber++;
  }

  @Override
  public void onLauncherInstructionIssued(String armament, String targetBssid) {
    if (userWantsToScanForAccessPoint) {
      currentAttackLog.postValue("(" + attackLogNumber + ") " + "Using " + armament);
      launcherActivateConfirmation.postValue("Proceed to scan for nearby access points?");
    } else {
      targetAccessPoint = targetBssid;
      currentAttackLog.postValue("(" + attackLogNumber + ") " + "Using " + armament +
              ", target set " + targetBssid);
      attackLogNumber++;
      launcherActivateConfirmation.postValue("Do you wish to activate the attack targeting "
              + targetBssid + " using " + selectedArmament + "?");
    }
  }

  @Override
  public void onLauncherArmamentActivation() {
    currentAttackLog.postValue("(" + attackLogNumber + ") " + "Armament activate!");
    attackLogNumber++;
  }

  @Override
  public void onLauncherArmamentDeactivation() {
    currentAttackLog.postValue("(" + attackLogNumber + ") " + "Armament deactivate!");
    attackLogNumber++;
  }

  @Override
  public void onLauncherNumberOfFoundAccessPoints(String numberOfAps) {
    currentAttackLog.postValue("(" + attackLogNumber + ") " + "Found " + numberOfAps +
            " access points");
    attackLogNumber++;
  }

  @Override
  public void onLauncherFoundAccessPoint(AccessPointData accessPointData) {
    if (userWantsToScanForAccessPoint) {
      accessPointDataList.add(accessPointData);
    }
    String ssid = accessPointData.ssid();
    int channel = accessPointData.channel();
    currentAttackLog.postValue("(" + attackLogNumber + ") " + ssid + " at channel " + channel);
    attackLogNumber++;
  }

  @Override
  public void onLauncherFinishScan() {
    if (userWantsToScanForAccessPoint) {
      launcherFinishScanning.postValue(accessPointDataList);
    }
    currentAttackLog.postValue("(" + attackLogNumber + ") " + "Done scanning access point");
    attackLogNumber++;
  }

  @Override
  public void onLauncherTargetAccessPointNotFound() {
    currentAttackLog.postValue("(" + attackLogNumber + ") " + "The target " +
            targetAccessPoint + " is not found");
    attackLogNumber++;
    launcherAccessPointNotFound.postValue(targetAccessPoint);
  }

  @Override
  public void onLauncherLaunchingSequence() {
    currentAttackLog.postValue("(" + attackLogNumber + ") "  + selectedArmament +
            " launching sequence");
    attackLogNumber++;
  }

  @Override
  public void onLauncherMainTaskCreated() {
    currentAttackLog.postValue("(" + attackLogNumber + ") " + "Main task created");
    attackLogNumber++;
    launcherMainTaskCreated.postValue(selectedArmament + " main task created");
    attackOnGoing = true;
  }

  @Override
  public void onLauncherPmkidWrongKeyType(String keyType) {
    currentAttackLog.postValue("(" + attackLogNumber + ") " + "Got wrong PMKID key type, "
            + keyType);
    attackLogNumber++;
  }

  @Override
  public void onLauncherPmkidWrongOui(String oui) {
    currentAttackLog.postValue("(" + attackLogNumber + ") " + "Got wrong PMKID key data OUI, "
            + oui);
    attackLogNumber++;
  }

  @Override
  public void onLauncherPmkidWrongKde(String kde) {
    currentAttackLog.postValue("(" + attackLogNumber + ") " + "Got wrong PMKID KDE, " +
            kde);
    attackLogNumber++;
  }

  @Override
  public void onLauncherMicIvRscIdNotSetToZero(String ivRscId) {
    currentAttackLog.postValue("(" + attackLogNumber + ") " + "The IV or RSC or ID is not set " +
            "to all zero, " + ivRscId);
    attackLogNumber++;
  }

  @Override
  public void onLauncherMainTaskCurrentStatus(String attackType, int attackStatus) {
    currentAttackLog.postValue("(" + attackLogNumber + ") " + attackType + ", status is " +
            attackStatus);
    attackLogNumber++;
  }

  @Override
  public void onLauncherReceivedEapolMessage(
          String attackType,
          int messageNumber,
          @Nullable PmkidFirstMessageData pmkidFirstMessageData,
          @Nullable MicFirstMessageData micFirstMessageData,
          @Nullable MicSecondMessageData micSecondMessageData
  ) {
    if (attackType.equals("PMKID") && messageNumber == 1) {
      if (pmkidFirstMessageData == null) {
        Log.d("dev-log", "SessionViewModel.onLauncherReceivedEapolMessage: " +
                "PMKID data is null");
        return;
      }

      String result = "PMKID is " + pmkidFirstMessageData.pmkid();

      launcherExecutionResultData = new HashInfoEntity(
              targetAccessPointSsid, pmkidFirstMessageData.bssid(),
              pmkidFirstMessageData.client(), "PMKID", pmkidFirstMessageData.pmkid(),
              "None", createStringDateTime()
      );

      currentAttackLog.postValue("(" + attackLogNumber + ") " + result);
      attackLogNumber++;
    } else if (attackType.equals("MIC")) {

      if (messageNumber == 1) {
        if (micFirstMessageData == null) {
          Log.d("dev-log", "SessionViewModel.onLauncherReceivedEapolMessage: " +
                  "MIC first message is null");
          return;
        }

        String result = "Anonce is " + micFirstMessageData.anonce();

        launcherExecutionResultData = new HashInfoEntity(targetAccessPointSsid,
                micFirstMessageData.bssid(), micFirstMessageData.clientMacAddress(),
                "MIC", "None", micFirstMessageData.anonce(),
                createStringDateTime());

        currentAttackLog.postValue("(" + attackLogNumber + ") " +
                "Got anonce from first EAPOL message. " + result);
        attackLogNumber++;
      } else if (messageNumber == 2) {
        if (micSecondMessageData == null) {
          Log.d("dev-log", "SessionViewModel.onLauncherReceivedEapolMessage: " +
                  "MIC first message is null");
          return;
        }

        String result = "MIC is " + micSecondMessageData.getMic();

        launcherExecutionResultData.hashData = micSecondMessageData.getMic();
        launcherExecutionResultData.keyData += micSecondMessageData.getAllData();

        currentAttackLog.postValue("(" + attackLogNumber + ") " +
                "Got EAPOL data from second message. " + result);
        attackLogNumber++;
      }
    }
  }

  private String createStringDateTime() {
    LocalDateTime dateTime = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss yyyy-MM-dd");
    return dateTime.format(formatter);
  }

  @Override
  public void onLauncherFinishingSequence() {
    currentAttackLog.postValue("(" + attackLogNumber + ") " +  selectedArmament +
            " finishing sequence");
    attackLogNumber++;
  }

  @Override
  public void onLauncherSuccess() {
    currentAttackLog.postValue("(" + attackLogNumber + ") " + selectedArmament +
            " successfully executed");
    attackLogNumber++;
    launcherExecutionResult.postValue("Success");
    attackOnGoing = false;

  }

  @Override
  public void onLauncherFailure(String targetBssid) {
    currentAttackLog.postValue("(" + attackLogNumber + ") " +  selectedArmament + " failed");
    attackLogNumber++;
    launcherExecutionResult.postValue("Failed");
    attackOnGoing = false;

  }

  @Override
  public void onLauncherMainTaskInDeautherStopped(String targetBssid) {
    currentAttackLog.postValue("(" + attackLogNumber + ") " + "In " + selectedArmament +
            " deauthentication task stopped");
    attackLogNumber++;
  }
}
