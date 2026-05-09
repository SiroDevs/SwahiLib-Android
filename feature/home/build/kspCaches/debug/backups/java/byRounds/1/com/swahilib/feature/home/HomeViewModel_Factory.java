package com.swahilib.feature.home;

import com.swahilib.core.data.repos.IdiomRepo;
import com.swahilib.core.data.repos.PrefsRepo;
import com.swahilib.core.data.repos.ProverbRepo;
import com.swahilib.core.data.repos.SayingRepo;
import com.swahilib.core.data.repos.WordRepo;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<IdiomRepo> idiomRepoProvider;

  private final Provider<ProverbRepo> proverbRepoProvider;

  private final Provider<SayingRepo> sayingRepoProvider;

  private final Provider<WordRepo> wordRepoProvider;

  private final Provider<PrefsRepo> prefsRepoProvider;

  private HomeViewModel_Factory(Provider<IdiomRepo> idiomRepoProvider,
      Provider<ProverbRepo> proverbRepoProvider, Provider<SayingRepo> sayingRepoProvider,
      Provider<WordRepo> wordRepoProvider, Provider<PrefsRepo> prefsRepoProvider) {
    this.idiomRepoProvider = idiomRepoProvider;
    this.proverbRepoProvider = proverbRepoProvider;
    this.sayingRepoProvider = sayingRepoProvider;
    this.wordRepoProvider = wordRepoProvider;
    this.prefsRepoProvider = prefsRepoProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(idiomRepoProvider.get(), proverbRepoProvider.get(), sayingRepoProvider.get(), wordRepoProvider.get(), prefsRepoProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<IdiomRepo> idiomRepoProvider,
      Provider<ProverbRepo> proverbRepoProvider, Provider<SayingRepo> sayingRepoProvider,
      Provider<WordRepo> wordRepoProvider, Provider<PrefsRepo> prefsRepoProvider) {
    return new HomeViewModel_Factory(idiomRepoProvider, proverbRepoProvider, sayingRepoProvider, wordRepoProvider, prefsRepoProvider);
  }

  public static HomeViewModel newInstance(IdiomRepo idiomRepo, ProverbRepo proverbRepo,
      SayingRepo sayingRepo, WordRepo wordRepo, PrefsRepo prefsRepo) {
    return new HomeViewModel(idiomRepo, proverbRepo, sayingRepo, wordRepo, prefsRepo);
  }
}
