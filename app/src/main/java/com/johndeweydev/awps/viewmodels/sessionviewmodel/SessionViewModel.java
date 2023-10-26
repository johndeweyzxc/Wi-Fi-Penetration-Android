package com.johndeweydev.awps.viewmodels.sessionviewmodel;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.johndeweydev.awps.repository.UsbSerialOutputModel;
import com.johndeweydev.awps.repository.sessionrepository.SessionRepository;
import com.johndeweydev.awps.repository.sessionrepository.models.MicFirstMessageModel;
import com.johndeweydev.awps.repository.sessionrepository.models.MicSecondMessageModel;
import com.johndeweydev.awps.repository.sessionrepository.models.PmkidFirstMessageModel;
import com.johndeweydev.awps.usbserial.UsbSerialStatus;

public class SessionViewModel extends ViewModel {

  private String selectedArmament;
  public SessionRepository sessionRepository;
  public MutableLiveData<String> attackLogs = new MutableLiveData<>();
  public MutableLiveData<Integer> scannedAccessPoints = new MutableLiveData<>(0);
  public MutableLiveData<Integer> nearbyAccessPoints = new MutableLiveData<>(0);
  public MutableLiveData<Integer> failedAttacks = new MutableLiveData<>(0);
  public MutableLiveData<Integer> keysFound = new MutableLiveData<>(0);
  public MutableLiveData<UsbSerialOutputModel>
          currentMessageFormatted = new MutableLiveData<>();
  public MutableLiveData<String> currentSerialInputError = new MutableLiveData<>();
  public MutableLiveData<String> currentSerialOutputError = new MutableLiveData<>();

  SessionRepositoryEvent sessionRepositoryEvent = new SessionRepositoryEvent() {

    @Override
    public void onRepositoryOutputRaw(UsbSerialOutputModel usbSerialOutputModel) {

    }

    @Override
    public void onRepositoryOutputFormatted(UsbSerialOutputModel usbSerialOutputModel) {

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
            @Nullable PmkidFirstMessageModel pmkidFirstMessageModel,
            @Nullable MicFirstMessageModel micFirstMessageModel,
            @Nullable MicSecondMessageModel micSecondMessageModel
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

  public UsbSerialStatus connectToDevice(
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
