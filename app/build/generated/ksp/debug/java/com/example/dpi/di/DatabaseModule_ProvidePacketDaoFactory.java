package com.example.dpi.di;

import com.example.dpi.database.DpiDatabase;
import com.example.dpi.database.PacketDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvidePacketDaoFactory implements Factory<PacketDao> {
  private final Provider<DpiDatabase> databaseProvider;

  public DatabaseModule_ProvidePacketDaoFactory(Provider<DpiDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public PacketDao get() {
    return providePacketDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvidePacketDaoFactory create(
      Provider<DpiDatabase> databaseProvider) {
    return new DatabaseModule_ProvidePacketDaoFactory(databaseProvider);
  }

  public static PacketDao providePacketDao(DpiDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.providePacketDao(database));
  }
}
