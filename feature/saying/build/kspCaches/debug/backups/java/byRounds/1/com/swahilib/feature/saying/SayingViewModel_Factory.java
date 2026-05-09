package com.swahilib.feature.saying;

import com.swahilib.core.data.repos.SayingRepo;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class SayingViewModel_Factory implements Factory<SayingViewModel> {
  private final Provider<SayingRepo> sayingRepoProvider;

  private SayingViewModel_Factory(Provider<SayingRepo> sayingRepoProvider) {
    this.sayingRepoProvider = sayingRepoProvider;
  }

  @Override
  public SayingViewModel get() {
    return newInstance(sayingRepoProvider.get());
  }

  public static SayingViewModel_Factory create(Provider<SayingRepo> sayingRepoProvider) {
    return new SayingViewModel_Factory(sayingRepoProvider);
  }

  public static SayingViewModel newInstance(SayingRepo sayingRepo) {
    return new SayingViewModel(sayingRepo);
  }
}
