package com.swahilib.feature.settings;

import com.swahilib.core.data.repos.PrefsRepo;
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<PrefsRepo> prefsRepoProvider;

  private SettingsViewModel_Factory(Provider<PrefsRepo> prefsRepoProvider) {
    this.prefsRepoProvider = prefsRepoProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(prefsRepoProvider.get());
  }

  public static SettingsViewModel_Factory create(Provider<PrefsRepo> prefsRepoProvider) {
    return new SettingsViewModel_Factory(prefsRepoProvider);
  }

  public static SettingsViewModel newInstance(PrefsRepo prefsRepo) {
    return new SettingsViewModel(prefsRepo);
  }
}
