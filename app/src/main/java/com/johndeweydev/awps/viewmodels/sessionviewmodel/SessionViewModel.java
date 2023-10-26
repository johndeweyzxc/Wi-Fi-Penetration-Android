package com.johndeweydev.awps.viewmodels.sessionviewmodel;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.johndeweydev.awps.repository.sessionrepository.models.SessionOutputModel;
import com.johndeweydev.awps.repository.sessionrepository.SessionRepository;
import com.johndeweydev.awps.repository.sessionrepository.models.MicFirstMessageModel;
import com.johndeweydev.awps.repository.sessionrepository.models.MicSecondMessageModel;
import com.johndeweydev.awps.repository.sessionrepository.models.PmkidFirstMessageModel;
import com.johndeweydev.awps.repository.usbserialrepository.UsbSerialOutputModel;
import com.johndeweydev.awps.usbserial.UsbSerialStatus;

public class SessionViewModel extends ViewModel {

  private String selectedArmament;
  public SessionRepository sessionRepository;
  public MutableLiveData<String> currentAttackLogList = new MutableLiveData<>();
  public MutableLiveData<Integer> scannedAccessPoints = new MutableLiveData<>(0);
  public MutableLiveData<Integer> nearbyAccessPoints = new MutableLiveData<>(0);
  public MutableLiveData<Integer> failedAttacks = new MutableLiveData<>(0);
  public MutableLiveData<Integer> keysFound = new MutableLiveData<>(0);
  public MutableLiveData<SessionOutputModel> sessionOutputModel = new MutableLiveData<>();
  public MutableLiveData<String> currentOnSerialInputError = new MutableLiveData<>();
  public MutableLiveData<String> currentOnSerialOutputError = new MutableLiveData<>();

  SessionEvent sessionEvent = new SessionEvent() {
    @Override
    public void onSerialOutputRaw(SessionOutputModel sessionOutputModel) {

    }

    @Override
    public void onSerialOutputFormatted(SessionOutputModel sessionOutputModel) {

    }

    @Override
    public void onSerialOutputError(String errorMessageOnNewData) {

    }

    @Override
    public void onSerialInputError(String dataToWrite) {

    }

    @Override
    public void onLauncherCommandParserCurrentArma(String armament, String targetBssid) {

    }

    @Override
    public void onLauncherCommandParserTargetAndArmaSet(String armament, String targetBssid) {

    }

    @Override
    public void onLauncherStarted() {

    }

    @Override
    public void onLauncherArmamentActivation() {

    }

    @Override
    public void onLauncherArmamentDeactivation() {

    }

    @Override
    public void onLauncherNumberOfFoundAccessPoints(String numberOfAps) {

    }

    @Override
    public void onLauncherScannedAccessPoint(
            String macAddress, String ssid, String rssi, String channel) {

    }

    @Override
    public void onLauncherFinishScanning() {

    }

    @Override
    public void onLauncherAccessPointNotFound() {

    }

    @Override
    public void onLauncherLaunchingSequence() {

    }

    @Override
    public void onLauncherTaskCreated() {

    }

    @Override
    public void onLauncherPmkidWrongKeyType(String keyType) {

    }

    @Override
    public void onLauncherTaskStatus(String attackType, int attackStatus) {

    }

    @Override
    public void onLauncherEapolMessage(
            String attackType,
            int messageNumber,
            @Nullable PmkidFirstMessageModel pmkidFirstMessageModel,
            @Nullable MicFirstMessageModel micFirstMessageModel,
            @Nullable MicSecondMessageModel micSecondMessageModel
    ) {

    }

    @Override
    public void onLauncherFinishingSequence() {

    }

    @Override
    public void onLauncherSuccess() {

    }

    @Override
    public void onLauncherFailure(String targetBssid) {

    }

    @Override
    public void onLauncherDeauthStop(String targetBssid) {

    }
  };

  public SessionViewModel(SessionRepository sessionRepository) {
    this.sessionRepository = sessionRepository;
    sessionRepository.setSessionEvent(sessionEvent);
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
