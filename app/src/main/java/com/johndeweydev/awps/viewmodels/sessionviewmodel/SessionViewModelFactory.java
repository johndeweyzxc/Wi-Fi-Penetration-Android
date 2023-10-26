package com.johndeweydev.awps.viewmodels.sessionviewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.johndeweydev.awps.repository.sessionrepository.SessionRepository;

public class SessionViewModelFactory implements ViewModelProvider.Factory {

  private final SessionRepository sessionRepository;

  public SessionViewModelFactory(SessionRepository sessionRepository) {
    this.sessionRepository = sessionRepository;
  }

  @NonNull
  @Override
  @SuppressWarnings("unchecked")
  public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
    return (T) new SessionViewModel(sessionRepository);
  }
}
