package com.johndeweydev.awps.viewmodels.terminalviewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.johndeweydev.awps.models.repo.serial.terminalreposerial.TerminalRepoSerial;

public class TerminalViewModelFactory implements ViewModelProvider.Factory {

  private final TerminalRepoSerial terminalRepoSerial;

  public TerminalViewModelFactory(TerminalRepoSerial terminalRepoSerial) {
    this.terminalRepoSerial = terminalRepoSerial;
  }

  @NonNull
  @Override
  @SuppressWarnings("unchecked")
  public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
    return (T) new TerminalViewModel(terminalRepoSerial);
  }
}
