package com.johndeweydev.himawhs.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.johndeweydev.himawhs.repository.UsbSerialRepository;

public class UsbSerialViewModelFactory implements ViewModelProvider.Factory {

  private final UsbSerialRepository usbSerialRepository;

  public UsbSerialViewModelFactory(UsbSerialRepository aUsbSerialRepository) {
    usbSerialRepository = aUsbSerialRepository;
  }

  @NonNull
  @Override
  @SuppressWarnings("unchecked")
  public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
    return (T) new UsbSerialViewModel(usbSerialRepository);
  }
}
