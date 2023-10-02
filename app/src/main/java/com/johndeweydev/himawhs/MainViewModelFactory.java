package com.johndeweydev.himawhs;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.johndeweydev.himawhs.repository.DefaultMainRepository;
import com.johndeweydev.himawhs.viewmodels.DefaultMainViewModel;

public class MainViewModelFactory implements ViewModelProvider.Factory {

  private final DefaultMainRepository defaultMainRepository;

  public MainViewModelFactory(DefaultMainRepository aDefaultMainRepository) {
    defaultMainRepository = aDefaultMainRepository;
  }

  @NonNull
  @Override
  @SuppressWarnings("unchecked")
  public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
    return (T) new DefaultMainViewModel(defaultMainRepository);
  }
}
