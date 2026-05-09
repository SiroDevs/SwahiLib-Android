package com.swahilib.feature.idiom;

import com.swahilib.core.data.repos.IdiomRepo;
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
public final class IdiomViewModel_Factory implements Factory<IdiomViewModel> {
  private final Provider<IdiomRepo> idiomRepoProvider;

  private IdiomViewModel_Factory(Provider<IdiomRepo> idiomRepoProvider) {
    this.idiomRepoProvider = idiomRepoProvider;
  }

  @Override
  public IdiomViewModel get() {
    return newInstance(idiomRepoProvider.get());
  }

  public static IdiomViewModel_Factory create(Provider<IdiomRepo> idiomRepoProvider) {
    return new IdiomViewModel_Factory(idiomRepoProvider);
  }

  public static IdiomViewModel newInstance(IdiomRepo idiomRepo) {
    return new IdiomViewModel(idiomRepo);
  }
}
