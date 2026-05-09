package com.swahilib.core.data.di;

import com.swahilib.core.data.repos.WordRepo;
import com.swahilib.core.database.daos.WordDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.github.jan.supabase.postgrest.Postgrest;
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
public final class DataModule_ProvideWordRepoFactory implements Factory<WordRepo> {
  private final Provider<WordDao> wordDaoProvider;

  private final Provider<Postgrest> supabaseProvider;

  private DataModule_ProvideWordRepoFactory(Provider<WordDao> wordDaoProvider,
      Provider<Postgrest> supabaseProvider) {
    this.wordDaoProvider = wordDaoProvider;
    this.supabaseProvider = supabaseProvider;
  }

  @Override
  public WordRepo get() {
    return provideWordRepo(wordDaoProvider.get(), supabaseProvider.get());
  }

  public static DataModule_ProvideWordRepoFactory create(Provider<WordDao> wordDaoProvider,
      Provider<Postgrest> supabaseProvider) {
    return new DataModule_ProvideWordRepoFactory(wordDaoProvider, supabaseProvider);
  }

  public static WordRepo provideWordRepo(WordDao wordDao, Postgrest supabase) {
    return Preconditions.checkNotNullFromProvides(DataModule.INSTANCE.provideWordRepo(wordDao, supabase));
  }
}
