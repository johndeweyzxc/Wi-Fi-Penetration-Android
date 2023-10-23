package com.johndeweydev.awps.viewmodels.sessionviewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.johndeweydev.awps.repository.usbserialrepository.UsbSerialOutputModel;

public class SessionViewModel extends ViewModel {

  private String selectedArmament;
  public MutableLiveData<String> currentAttackLogList = new MutableLiveData<>();
  public MutableLiveData<Integer> scannedAccessPoints = new MutableLiveData<>(0);
  public MutableLiveData<Integer> nearbyAccessPoints = new MutableLiveData<>(0);
  public MutableLiveData<Integer> failedAttacks = new MutableLiveData<>(0);
  public MutableLiveData<Integer> keysFound = new MutableLiveData<>(0);


  public void setSelectedArmament(String selectedArmament) {
    this.selectedArmament = "Config: " + selectedArmament;
  }

  public String getSelectedArmament() {
    return selectedArmament;
  }

  public void appendAttackLog(UsbSerialOutputModel usbSerialOutputModel) {

  }

}
