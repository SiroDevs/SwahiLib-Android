package com.swahilib.feature.proverb;

import com.swahilib.core.data.repos.ProverbRepo;
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
public final class ProverbViewModel_Factory implements Factory<ProverbViewModel> {
  private final Provider<ProverbRepo> proverbRepoProvider;

  private ProverbViewModel_Factory(Provider<ProverbRepo> proverbRepoProvider) {
    this.proverbRepoProvider = proverbRepoProvider;
  }

  @Override
  public ProverbViewModel get() {
    return newInstance(proverbRepoProvider.get());
  }

  public static ProverbViewModel_Factory create(Provider<ProverbRepo> proverbRepoProvider) {
    return new ProverbViewModel_Factory(proverbRepoProvider);
  }

  public static ProverbViewModel newInstance(ProverbRepo proverbRepo) {
    return new ProverbViewModel(proverbRepo);
  }
}
