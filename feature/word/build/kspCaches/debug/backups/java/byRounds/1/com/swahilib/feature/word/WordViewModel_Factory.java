package com.swahilib.feature.word;

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
public final class WordViewModel_Factory implements Factory<WordViewModel> {
  private final Provider<WordRepo> wordRepoProvider;

  private WordViewModel_Factory(Provider<WordRepo> wordRepoProvider) {
    this.wordRepoProvider = wordRepoProvider;
  }

  @Override
  public WordViewModel get() {
    return newInstance(wordRepoProvider.get());
  }

  public static WordViewModel_Factory create(Provider<WordRepo> wordRepoProvider) {
    return new WordViewModel_Factory(wordRepoProvider);
  }

  public static WordViewModel newInstance(WordRepo wordRepo) {
    return new WordViewModel(wordRepo);
  }
}
