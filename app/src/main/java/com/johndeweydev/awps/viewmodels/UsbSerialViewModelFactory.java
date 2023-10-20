package com.johndeweydev.awps.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.johndeweydev.awps.repository.UsbSerialRepository;

public class UsbSerialViewModelFactory implements ViewModelProvider.Factory {

  private final UsbSerialRepository usbSerialRepository;

  public UsbSerialViewModelFactory(UsbSerialRepository usbSerialRepository) {
    this.usbSerialRepository = usbSerialRepository;
  }

  @NonNull
  @Override
  @SuppressWarnings("unchecked")
  public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
    return (T) new UsbSerialViewModel(usbSerialRepository);
  }
}
