package com.johndeweydev.awps.viewmodels.sessionviewmodel;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.johndeweydev.awps.data.LauncherOutputData;
import com.johndeweydev.awps.repository.sessionrepository.SessionRepository;
import com.johndeweydev.awps.data.MicFirstMessageData;
import com.johndeweydev.awps.data.MicSecondMessageData;
import com.johndeweydev.awps.data.PmkidFirstMessageData;
import com.johndeweydev.awps.repository.sessionrepository.SessionRepositoryEvent;

public class SessionViewModel extends ViewModel {

  private String selectedArmament;
  public SessionRepository sessionRepository;
  public MutableLiveData<String> attackLogs = new MutableLiveData<>();
  public MutableLiveData<Integer> scannedAccessPoints = new MutableLiveData<>(0);
  public MutableLiveData<Integer> nearbyAccessPoints = new MutableLiveData<>(0);
  public MutableLiveData<Integer> failedAttacks = new MutableLiveData<>(0);
  public MutableLiveData<Integer> keysFound = new MutableLiveData<>(0);
  public MutableLiveData<LauncherOutputData>
          currentMessageFormatted = new MutableLiveData<>();
  public MutableLiveData<String> currentSerialInputError = new MutableLiveData<>();
  public MutableLiveData<String> currentSerialOutputError = new MutableLiveData<>();

  SessionRepositoryEvent sessionRepositoryEvent = new SessionRepositoryEvent() {

    @Override
    public void onRepositoryOutputRaw(LauncherOutputData launcherOutputData) {

    }

    @Override
    public void onRepositoryOutputFormatted(LauncherOutputData launcherOutputData) {

    }

    @Override
    public void onRepositoryOutputError(String error) {

    }

    @Override
    public void onRepositoryInputError(String input) {

    }

    @Override
    public void onRepositoryCommandParserCurrentArma(String armament, String targetBssid) {

    }

    @Override
    public void onRepositoryCommandParserTargetAndArmaSet(String armament, String targetBssid) {

    }

    @Override
    public void onRepositoryStarted() {

    }

    @Override
    public void onRepositoryArmamentActivation() {

    }

    @Override
    public void onRepositoryArmamentDeactivation() {

    }

    @Override
    public void onRepositoryNumberOfFoundAccessPoints(String numberOfAps) {

    }

    @Override
    public void onRepositoryScannedAccessPoint(
            String macAddress, String ssid, String rssi, String channel) {

    }

    @Override
    public void onRepositoryFinishScanning() {

    }

    @Override
    public void onRepositoryAccessPointNotFound() {

    }

    @Override
    public void onRepositoryLaunchingSequence() {

    }

    @Override
    public void onRepositoryTaskCreated() {

    }

    @Override
    public void onRepositoryPmkidWrongKeyType(String keyType) {

    }

    @Override
    public void onRepositoryTaskStatus(String attackType, int attackStatus) {

    }

    @Override
    public void onRepositoryEapolMessage(
            String attackType,
            int messageNumber,
            @Nullable PmkidFirstMessageData pmkidFirstMessageData,
            @Nullable MicFirstMessageData micFirstMessageData,
            @Nullable MicSecondMessageData micSecondMessageData
    ) {

    }

    @Override
    public void onRepositoryFinishingSequence() {

    }

    @Override
    public void onRepositorySuccess() {

    }

    @Override
    public void onRepositoryFailure(String targetBssid) {

    }

    @Override
    public void onRepositoryDeauthStop(String targetBssid) {

    }
  };

  public SessionViewModel(SessionRepository sessionRepository) {
    this.sessionRepository = sessionRepository;
    sessionRepository.setSessionEvent(sessionRepositoryEvent);
  }

  public String connectToDevice(
          int baudRate, int dataBits, int stopBits, int parity, int deviceId, int portNum
  ) {
    return sessionRepository.connect(baudRate, dataBits, stopBits, parity, deviceId, portNum);
  }

  public void disconnectFromDevice() {
    sessionRepository.disconnect();
  }

  public void startEventDrivenReadFromDevice() {
    sessionRepository.startReading();
  }

  public void stopEventDrivenReadFromDevice() {
    sessionRepository.stopReading();
  }

  public void writeDataToDevice(String data) {
    sessionRepository.writeData(data);
  }
}
