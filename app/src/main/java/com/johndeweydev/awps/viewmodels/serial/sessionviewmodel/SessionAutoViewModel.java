package com.johndeweydev.awps.viewmodels.serial.sessionviewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.johndeweydev.awps.models.data.AccessPointData;
import com.johndeweydev.awps.models.repo.serial.sessionreposerial.SessionRepoSerial;

import java.util.ArrayList;
import java.util.Objects;

public class SessionAutoViewModel extends SessionViewModel {

  public MutableLiveData<AccessPointData> currentTarget = new MutableLiveData<>(null);

  public MutableLiveData<Integer> nearbyAps = new MutableLiveData<>(0);
  public MutableLiveData<Integer> failedAttacks = new MutableLiveData<>(0);
  public MutableLiveData<Integer> pwned = new MutableLiveData<>(0);

  public MutableLiveData<String> userCommandState = new MutableLiveData<>("STOPPED");
  ArrayList<AccessPointData> previouslyAttackedTargets = new ArrayList<>();

  public SessionAutoViewModel(SessionRepoSerial sessionRepoSerial) {
    super(sessionRepoSerial);
  }

  public void startAttack() {
    userCommandState.setValue("RUNNING");
    writeInstructionCodeForScanningDevicesToLauncher();
  }

  public void stopAttack() {
    userCommandState.setValue("STOPPED");
  }

  @Override
  public void onRepoStarted() {
    super.onRepoStarted();
    if (Objects.equals(userCommandState.getValue(), "RUNNING")) {
      writeInstructionCodeForScanningDevicesToLauncher();
    }
  }

  @Override
  public void onRepoArmamentActivation() {}

  @Override
  public void onRepoInstructionIssued(String armament, String targetBssid) {
    currentAttackLog.postValue("(" + attackLogNumber + ")" + " Using " + armament);
    attackLogNumber++;
    if (Objects.equals(userCommandState.getValue(), "RUNNING")) {
      writeControlCodeActivationToLauncher();
    }
  }

  @Override
  public void onRepoNumberOfFoundAccessPoints(String numberOfAps) {
    super.onRepoNumberOfFoundAccessPoints(numberOfAps);
    nearbyAps.postValue(Integer.parseInt(numberOfAps));
  }

  @Override
  public void onRepoFoundAccessPoint(AccessPointData accessPointData) {
    String ssid = accessPointData.ssid();
    int channel = accessPointData.channel();
    currentAttackLog.postValue("(" + attackLogNumber + ") " + ssid + " at channel " + channel);
    attackLogNumber++;

    if (!accessPointWasAttackBefore(accessPointData)) {
      Log.d("dev-log", "SessionAutoViewModel.onRepoFoundAccessPoint: Adding " + ssid +
              " as potential target");
      accessPointDataList.add(accessPointData);
    }
  }

  private boolean accessPointWasAttackBefore(AccessPointData accessPointData) {
    for (int i = 0; i < previouslyAttackedTargets.size(); i++) {
      AccessPointData prevTarget = previouslyAttackedTargets.get(i);

      if (Objects.equals(prevTarget.ssid(), accessPointData.ssid()) &&
              Objects.equals(prevTarget.macAddress(), accessPointData.macAddress())) {
        currentAttackLog.postValue("(" + attackLogNumber + ") " + accessPointData.ssid() +
                " was attacked before");
        attackLogNumber++;
        return true;
      }
    }
    return false;
  }

  @Override
  public void onRepoFinishScan() {
    currentAttackLog.postValue("(" + attackLogNumber + ") Selecting target");
    attackLogNumber++;

    if (Objects.equals(userCommandState.getValue(), "RUNNING")) {
      selectTarget();
    }
  }

  private void selectTarget() {
    if (accessPointDataList.isEmpty()) {
      currentAttackLog.postValue("(" + attackLogNumber + ") No potential targets found, " +
              "doing a scan again");
      attackLogNumber++;
      writeInstructionCodeForScanningDevicesToLauncher();
    } else {

      AccessPointData selectedTarget = accessPointDataList.get(0);
      currentTarget.postValue(selectedTarget);
      String ssid = selectedTarget.ssid();
      currentAttackLog.postValue("(" + attackLogNumber + ") Initiating attack on " + ssid);
      attackLogNumber++;

      // Set the SSID in the super class because it causes null value when saving it
      // in the database
      targetAccessPointSsid = selectedTarget.ssid();
      writeInstructionCodeToLauncher(selectedTarget.macAddress());
    }
  }

  public String formatMacAddress(String bssid) {
    StringBuilder formattedMac = new StringBuilder();
    for (int i = 0; i < bssid.length(); i += 2) {
      formattedMac.append(bssid.substring(i, i + 2));
      if (i < bssid.length() - 2) {
        formattedMac.append(":");
      }
    }
    return formattedMac.toString().toUpperCase();
  }

  @Override
  public void onRepoTargetAccessPointNotFound() {
    super.onRepoTargetAccessPointNotFound();

    if (Objects.equals(userCommandState.getValue(), "RUNNING")) {
      writeInstructionCodeForScanningDevicesToLauncher();
    }
  }

  @Override
  public void onRepoMainTaskCurrentStatus(String attackType, int attackStatus) {
    super.onRepoMainTaskCurrentStatus(attackType, attackStatus);

    if (Objects.equals(userCommandState.getValue(), "STOPPED")) {
      writeControlCodeDeactivationToLauncher();
    } else {
      if (attackStatus == 10) {
        writeControlCodeDeactivationToLauncher();
      }
    }
  }

  @Override
  public void onRepoSuccess() {
    super.onRepoSuccess();

    int currentNumberOfKeys;
    if (pwned.getValue() == null) {
      currentNumberOfKeys = 0;
    } else {
      currentNumberOfKeys = pwned.getValue();
    }

    pwned.postValue(currentNumberOfKeys + 1);
    if (previouslyAttackedTargets.size() == 20) {
      previouslyAttackedTargets.remove(0);
    }
    previouslyAttackedTargets.add(currentTarget.getValue());
    writeInstructionCodeForScanningDevicesToLauncher();
  }

  @Override
  public void onRepoFailure(String targetBssid) {
    super.onRepoFailure(targetBssid);

    int currentNumberOfFailedAttacks;
    if (failedAttacks.getValue() == null) {
      currentNumberOfFailedAttacks = 0;
    } else {
      currentNumberOfFailedAttacks = failedAttacks.getValue();
    }

    failedAttacks.postValue(currentNumberOfFailedAttacks + 1);
    if (previouslyAttackedTargets.size() == 20) {
      previouslyAttackedTargets.remove(0);
    }
    previouslyAttackedTargets.add(currentTarget.getValue());
  }
}
