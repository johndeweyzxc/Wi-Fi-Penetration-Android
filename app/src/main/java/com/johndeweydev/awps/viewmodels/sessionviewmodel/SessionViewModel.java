package com.johndeweydev.awps.viewmodels.sessionviewmodel;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.johndeweydev.awps.data.AccessPointData;
import com.johndeweydev.awps.data.DeviceConnectionParamData;
import com.johndeweydev.awps.data.LauncherOutputData;
import com.johndeweydev.awps.models.repo.serial.sessionreposerial.SessionRepoSerial;
import com.johndeweydev.awps.data.MicFirstMessageData;
import com.johndeweydev.awps.data.MicSecondMessageData;
import com.johndeweydev.awps.data.PmkidFirstMessageData;
import com.johndeweydev.awps.models.repo.serial.sessionreposerial.SessionRepoSerialEvent;
import com.johndeweydev.awps.viewmodels.DefaultViewModelUsbSerial;

import java.util.ArrayList;

public class SessionViewModel extends ViewModel implements DefaultViewModelUsbSerial {

  public boolean automaticAttack = false;
  public String selectedArmament;
  public SessionRepoSerial sessionRepoSerial;

  public MutableLiveData<String> currentAttackLog = new MutableLiveData<>();
  private int attackLogNumber = 0;
  public MutableLiveData<String> launcherStarted = new MutableLiveData<>();
  public MutableLiveData<String> launcherActivateConfirmation = new MutableLiveData<>();
  public String targetAccessPoint;

  // Indicates if the user scanned an access point to look for potential targets in manual attack
  public boolean userWantsToScanForAccessPoint = false;
  public ArrayList<AccessPointData> accessPointDataList = new ArrayList<>();
  public MutableLiveData<ArrayList<AccessPointData>> launcherFinishScanning =
          new MutableLiveData<>();
  public MutableLiveData<String> launcherAccessPointNotFound = new MutableLiveData<>();
  public MutableLiveData<String> launcherMainTaskCreated = new MutableLiveData<>();
  public boolean attackOnGoing = false;
  public MutableLiveData<String> launcherExecutionResult = new MutableLiveData<>();

  public MutableLiveData<Integer> scannedAccessPoints = new MutableLiveData<>(0);
  public MutableLiveData<Integer> nearbyAccessPoints = new MutableLiveData<>(0);
  public MutableLiveData<Integer> failedAttacks = new MutableLiveData<>(0);
  public MutableLiveData<Integer> keysFound = new MutableLiveData<>(0);

  public MutableLiveData<String> currentSerialInputError = new MutableLiveData<>();
  public MutableLiveData<String> currentSerialOutputError = new MutableLiveData<>();

  SessionRepoSerialEvent sessionRepoSerialEvent = new SessionRepoSerialEvent() {

    @Override
    public void onRepositoryOutputRaw(LauncherOutputData launcherOutputData) {}

    @Override
    public void onRepositoryOutputFormatted(LauncherOutputData launcherOutputData) {
      Log.d("dev-log", "SessionViewModel.onRepositoryOutputFormatted: Serial -> " +
              launcherOutputData.getOutput());
    }

    @Override
    public void onRepositoryOutputError(String error) {
      Log.d("dev-log", "SessionViewModel.onRepositoryOutputError: Serial -> " + error);
      currentSerialOutputError.postValue(error);
    }

    @Override
    public void onRepositoryInputError(String input) {
      Log.d("dev-log", "SessionViewModel.onRepositoryInputError: Serial -> " + input);
      currentSerialInputError.postValue(input);
    }

    @Override
    public void onRepositoryStarted() {
      launcherStarted.postValue("Launcher module started");
      currentAttackLog.postValue("(" + attackLogNumber + ") " + "Module started");
      attackLogNumber++;
    }

    @Override
    public void onRepositoryCommandParserCurrentArma(String armament, String targetBssid) {
      currentAttackLog.postValue("(" + attackLogNumber + ") " + "Using " + armament + "" +
              ", targeting " + targetBssid);
      attackLogNumber++;
    }

    @Override
    public void onRepositoryCommandParserTargetAndArmaSet(String armament, String targetBssid) {
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
    public void onRepositoryArmamentActivation() {
      currentAttackLog.postValue("(" + attackLogNumber + ") " + "Armament activate!");
      attackLogNumber++;
    }

    @Override
    public void onRepositoryArmamentDeactivation() {
      currentAttackLog.postValue("(" + attackLogNumber + ") " + "Armament deactivate!");
      attackLogNumber++;
    }

    @Override
    public void onRepositoryNumberOfFoundAccessPoints(String numberOfAps) {
      currentAttackLog.postValue("(" + attackLogNumber + ") " + "Found " + numberOfAps +
              " access points");
      attackLogNumber++;
    }

    @Override
    public void onRepositoryScannedAccessPoint(AccessPointData accessPointData) {
      if (userWantsToScanForAccessPoint) {
        accessPointDataList.add(accessPointData);
      }
      String ssid = accessPointData.getSsid();
      int channel = accessPointData.getChannel();
      currentAttackLog.postValue("(" + attackLogNumber + ") " + ssid + " at channel " + channel);
      attackLogNumber++;
    }

    @Override
    public void onRepositoryFinishScanning() {
      if (userWantsToScanForAccessPoint) {
        launcherFinishScanning.postValue(accessPointDataList);
      }
      currentAttackLog.postValue("(" + attackLogNumber + ") " + "Done scanning access point");
      attackLogNumber++;
    }

    @Override
    public void onRepositoryAccessPointNotFound() {
      currentAttackLog.postValue("(" + attackLogNumber + ") " + "The target " +
              targetAccessPoint + " is not found");
      attackLogNumber++;
      launcherAccessPointNotFound.postValue("The target " + targetAccessPoint + " is not found");
    }

    @Override
    public void onRepositoryLaunchingSequence() {
      // NOTE: Sometimes the launcher is stuck in the launching sequence so a
      // complete restart is required

      currentAttackLog.postValue("(" + attackLogNumber + ") "  + selectedArmament +
              " launching sequence");
      attackLogNumber++;
    }

    @Override
    public void onRepositoryTaskCreated() {
      currentAttackLog.postValue("(" + attackLogNumber + ") " + "Main task created");
      attackLogNumber++;
      launcherMainTaskCreated.postValue(selectedArmament + " main task created");
      attackOnGoing = true;
    }

    @Override
    public void onRepositoryPmkidWrongKeyType(String keyType) {
      currentAttackLog.postValue("(" + attackLogNumber + ") " + "Got wrong PMKID key type, "
              + keyType);
      attackLogNumber++;
    }

    @Override
    public void onRepositoryTaskStatus(String attackType, int attackStatus) {
      currentAttackLog.postValue("(" + attackLogNumber + ") " + attackType + ", status is " +
              attackStatus);
      attackLogNumber++;
    }

    @Override
    public void onRepositoryEapolMessage(
            String attackType,
            int messageNumber,
            @Nullable PmkidFirstMessageData pmkidFirstMessageData,
            @Nullable MicFirstMessageData micFirstMessageData,
            @Nullable MicSecondMessageData micSecondMessageData
    ) {

      // TODO: Save eapol message data to the database

      if (attackType.equals("PMKID") && messageNumber == 1) {
        if (pmkidFirstMessageData == null) {
          Log.d("dev-log", "SessionViewModel.onRepositoryEapolMessage: " +
                  "PMKID data is null");
          return;
        }

        String result = "BSSID is " + pmkidFirstMessageData.getBssid() + ", client is " +
                pmkidFirstMessageData.getClient() + ", PMKID is " +
                pmkidFirstMessageData.getPmkid();
        currentAttackLog.postValue("(" + attackLogNumber + ") " + result);
        attackLogNumber++;
      } else if (attackType.equals("MIC")) {

        if (messageNumber == 1) {
          if (micFirstMessageData == null) {
            Log.d("dev-log", "SessionViewModel.onRepositoryEapolMessage: " +
                    "MIC first message is null");
            return;
          }

          String result = "Anonce is " + micFirstMessageData.getAnonce();
          currentAttackLog.postValue("(" + attackLogNumber + ") " +
                  "Got anonce from first EAPOL message. " + result);
          attackLogNumber++;
        } else if (messageNumber == 2) {
          if (micSecondMessageData == null) {
            Log.d("dev-log", "SessionViewModel.onRepositoryEapolMessage: " +
                    "MIC first message is null");
            return;
          }

          String result = "MIC is " + micSecondMessageData.getMic();
          currentAttackLog.postValue("(" + attackLogNumber + ") " +
                  "Got EAPOL data from second message. " + result);
          attackLogNumber++;
        }
      }
    }

    @Override
    public void onRepositoryFinishingSequence() {
      currentAttackLog.postValue("(" + attackLogNumber + ") " +  selectedArmament +
              " finishing sequence");
      attackLogNumber++;
    }

    @Override
    public void onRepositorySuccess() {
      currentAttackLog.postValue("(" + attackLogNumber + ") " + selectedArmament +
              " successfully executed");
      attackLogNumber++;
      launcherExecutionResult.postValue("Success");
      attackOnGoing = false;

      // TODO: At this state set all launcher live data to null to reset their values
    }

    @Override
    public void onRepositoryFailure(String targetBssid) {
      currentAttackLog.postValue("(" + attackLogNumber + ") " +  selectedArmament + " failed");
      attackLogNumber++;
      launcherExecutionResult.postValue("Failed");
      attackOnGoing = false;

      // TODO: At this state set all launcher live data to null to reset their values
    }

    @Override
    public void onRepositoryDeauthStop(String targetBssid) {
      currentAttackLog.postValue("(" + attackLogNumber + ") " + "In " + selectedArmament +
              " deauthentication task stopped");
      attackLogNumber++;

      // TODO: At this state set all launcher live data to null to reset their values
    }
  };

  public SessionViewModel(SessionRepoSerial sessionRepoSerial) {
    Log.d("dev-log", "SessionViewModel: Created new instance of SessionViewModel");
    this.sessionRepoSerial = sessionRepoSerial;
    sessionRepoSerial.setEventHandler(sessionRepoSerialEvent);
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

  public void writeInstructionCodeToLauncher(String data) {
    String instructionCode = "";
    switch (selectedArmament) {
      case "PMKID Based Attack":
        instructionCode += "02";
        break;
      case "MIC Based Attack":
        instructionCode += "03";
        break;
      case "Deauther":
        instructionCode += "04";
        break;
    }
    instructionCode += data;
    sessionRepoSerial.writeDataToDevice(instructionCode);
  }

  public void writeControlCodeActivationToLauncher() {
    sessionRepoSerial.writeDataToDevice("06");
  }

  public void writeControlCodeDeactivationToLauncher() {
    sessionRepoSerial.writeDataToDevice("07");
  }

  public void writeInstructionCodeForScanningDevicesToLauncher() {
    sessionRepoSerial.writeDataToDevice("01");
  }
}
