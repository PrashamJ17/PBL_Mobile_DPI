package com.example.dpi.service;

import com.example.dpi.capture.PacketCaptureManager;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class DpiVpnService_MembersInjector implements MembersInjector<DpiVpnService> {
  private final Provider<PacketCaptureManager> captureManagerProvider;

  public DpiVpnService_MembersInjector(Provider<PacketCaptureManager> captureManagerProvider) {
    this.captureManagerProvider = captureManagerProvider;
  }

  public static MembersInjector<DpiVpnService> create(
      Provider<PacketCaptureManager> captureManagerProvider) {
    return new DpiVpnService_MembersInjector(captureManagerProvider);
  }

  @Override
  public void injectMembers(DpiVpnService instance) {
    injectCaptureManager(instance, captureManagerProvider.get());
  }

  @InjectedFieldSignature("com.example.dpi.service.DpiVpnService.captureManager")
  public static void injectCaptureManager(DpiVpnService instance,
      PacketCaptureManager captureManager) {
    instance.captureManager = captureManager;
  }
}
