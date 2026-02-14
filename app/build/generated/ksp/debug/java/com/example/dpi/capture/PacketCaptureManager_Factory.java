package com.example.dpi.capture;

import com.example.dpi.database.DpiDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class PacketCaptureManager_Factory implements Factory<PacketCaptureManager> {
  private final Provider<DpiDatabase> databaseProvider;

  public PacketCaptureManager_Factory(Provider<DpiDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public PacketCaptureManager get() {
    return newInstance(databaseProvider.get());
  }

  public static PacketCaptureManager_Factory create(Provider<DpiDatabase> databaseProvider) {
    return new PacketCaptureManager_Factory(databaseProvider);
  }

  public static PacketCaptureManager newInstance(DpiDatabase database) {
    return new PacketCaptureManager(database);
  }
}
