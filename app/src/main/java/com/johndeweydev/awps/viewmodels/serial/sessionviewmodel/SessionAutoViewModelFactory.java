package com.johndeweydev.awps.viewmodels.serial.sessionviewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.johndeweydev.awps.models.repo.serial.sessionreposerial.SessionRepoSerial;

public class SessionAutoViewModelFactory implements ViewModelProvider.Factory {

  private final SessionRepoSerial sessionRepoSerial;
  public SessionAutoViewModelFactory(SessionRepoSerial sessionRepoSerial) {
    this.sessionRepoSerial = sessionRepoSerial;
  }

  @NonNull
  @Override
  @SuppressWarnings("unchecked")
  public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
    return (T) new SessionAutoViewModel(sessionRepoSerial);
  }
}
