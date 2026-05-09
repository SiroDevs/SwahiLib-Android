package com.swahilib.core.data.di;

import com.swahilib.core.data.repos.SubsRepo;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class DataModule_ProvideSubsRepoFactory implements Factory<SubsRepo> {
  @Override
  public SubsRepo get() {
    return provideSubsRepo();
  }

  public static DataModule_ProvideSubsRepoFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static SubsRepo provideSubsRepo() {
    return Preconditions.checkNotNullFromProvides(DataModule.INSTANCE.provideSubsRepo());
  }

  private static final class InstanceHolder {
    static final DataModule_ProvideSubsRepoFactory INSTANCE = new DataModule_ProvideSubsRepoFactory();
  }
}
