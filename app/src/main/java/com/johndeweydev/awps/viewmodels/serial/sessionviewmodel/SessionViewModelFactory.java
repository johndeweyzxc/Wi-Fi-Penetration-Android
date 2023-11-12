package com.johndeweydev.awps.viewmodels.serial.sessionviewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.johndeweydev.awps.models.repo.serial.sessionreposerial.SessionRepoSerial;

public class SessionViewModelFactory implements ViewModelProvider.Factory {

  private final SessionRepoSerial sessionRepoSerial;

  public SessionViewModelFactory(SessionRepoSerial sessionRepoSerial) {
    this.sessionRepoSerial = sessionRepoSerial;
  }

  @NonNull
  @Override
  @SuppressWarnings("unchecked")
  public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
    return (T) new SessionViewModel(sessionRepoSerial);
  }
}
