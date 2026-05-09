package com.swahilib.feature.splash;

import com.swahilib.core.data.repos.PrefsRepo;
import com.swahilib.core.data.repos.SubsRepo;
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
public final class SplashViewModel_Factory implements Factory<SplashViewModel> {
  private final Provider<PrefsRepo> prefsRepoProvider;

  private final Provider<SubsRepo> subsRepoProvider;

  private SplashViewModel_Factory(Provider<PrefsRepo> prefsRepoProvider,
      Provider<SubsRepo> subsRepoProvider) {
    this.prefsRepoProvider = prefsRepoProvider;
    this.subsRepoProvider = subsRepoProvider;
  }

  @Override
  public SplashViewModel get() {
    return newInstance(prefsRepoProvider.get(), subsRepoProvider.get());
  }

  public static SplashViewModel_Factory create(Provider<PrefsRepo> prefsRepoProvider,
      Provider<SubsRepo> subsRepoProvider) {
    return new SplashViewModel_Factory(prefsRepoProvider, subsRepoProvider);
  }

  public static SplashViewModel newInstance(PrefsRepo prefsRepo, SubsRepo subsRepo) {
    return new SplashViewModel(prefsRepo, subsRepo);
  }
}
