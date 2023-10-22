package com.johndeweydev.awps.viewmodels;

import androidx.lifecycle.ViewModel;

public class SessionViewModel extends ViewModel {

  private String selectedArmament;

  public void setSelectedArmament(String selectedArmament) {
    this.selectedArmament = selectedArmament;
  }

  public String getSelectedArmament() {
    return selectedArmament;
  }

}
