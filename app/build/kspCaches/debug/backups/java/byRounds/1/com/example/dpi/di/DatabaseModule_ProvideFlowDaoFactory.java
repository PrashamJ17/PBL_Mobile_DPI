package com.example.dpi.di;

import com.example.dpi.database.DpiDatabase;
import com.example.dpi.database.FlowDao;
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
public final class DatabaseModule_ProvideFlowDaoFactory implements Factory<FlowDao> {
  private final Provider<DpiDatabase> databaseProvider;

  public DatabaseModule_ProvideFlowDaoFactory(Provider<DpiDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public FlowDao get() {
    return provideFlowDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideFlowDaoFactory create(
      Provider<DpiDatabase> databaseProvider) {
    return new DatabaseModule_ProvideFlowDaoFactory(databaseProvider);
  }

  public static FlowDao provideFlowDao(DpiDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideFlowDao(database));
  }
}
