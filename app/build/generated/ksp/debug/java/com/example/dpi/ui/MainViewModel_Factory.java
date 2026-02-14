package com.example.dpi.ui;

import android.app.Application;
import com.example.dpi.capture.PacketCaptureManager;
import com.example.dpi.database.DpiDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class MainViewModel_Factory implements Factory<MainViewModel> {
  private final Provider<Application> applicationProvider;

  private final Provider<DpiDatabase> databaseProvider;

  private final Provider<PacketCaptureManager> captureManagerProvider;

  public MainViewModel_Factory(Provider<Application> applicationProvider,
      Provider<DpiDatabase> databaseProvider,
      Provider<PacketCaptureManager> captureManagerProvider) {
    this.applicationProvider = applicationProvider;
    this.databaseProvider = databaseProvider;
    this.captureManagerProvider = captureManagerProvider;
  }

  @Override
  public MainViewModel get() {
    return newInstance(applicationProvider.get(), databaseProvider.get(), captureManagerProvider.get());
  }

  public static MainViewModel_Factory create(Provider<Application> applicationProvider,
      Provider<DpiDatabase> databaseProvider,
      Provider<PacketCaptureManager> captureManagerProvider) {
    return new MainViewModel_Factory(applicationProvider, databaseProvider, captureManagerProvider);
  }

  public static MainViewModel newInstance(Application application, DpiDatabase database,
      PacketCaptureManager captureManager) {
    return new MainViewModel(application, database, captureManager);
  }
}
