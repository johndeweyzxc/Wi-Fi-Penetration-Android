package com.johndeweydev.awps.viewmodels.terminalviewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.johndeweydev.awps.repository.terminalrepository.TerminalRepository;

public class TerminalViewModelFactory implements ViewModelProvider.Factory {

  private final TerminalRepository terminalRepository;

  public TerminalViewModelFactory(TerminalRepository terminalRepository) {
    this.terminalRepository = terminalRepository;
  }

  @NonNull
  @Override
  @SuppressWarnings("unchecked")
  public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
    return (T) new TerminalViewModel(terminalRepository);
  }
}
