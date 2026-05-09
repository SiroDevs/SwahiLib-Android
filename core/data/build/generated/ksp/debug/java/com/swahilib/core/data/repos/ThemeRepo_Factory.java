package com.swahilib.core.data.repos;

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
public final class ThemeRepo_Factory implements Factory<ThemeRepo> {
  private final Provider<PrefsRepo> prefsProvider;

  private ThemeRepo_Factory(Provider<PrefsRepo> prefsProvider) {
    this.prefsProvider = prefsProvider;
  }

  @Override
  public ThemeRepo get() {
    return newInstance(prefsProvider.get());
  }

  public static ThemeRepo_Factory create(Provider<PrefsRepo> prefsProvider) {
    return new ThemeRepo_Factory(prefsProvider);
  }

  public static ThemeRepo newInstance(PrefsRepo prefs) {
    return new ThemeRepo(prefs);
  }
}
