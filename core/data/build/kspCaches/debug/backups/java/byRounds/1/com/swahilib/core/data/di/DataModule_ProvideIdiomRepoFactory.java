package com.swahilib.core.data.di;

import com.swahilib.core.data.repos.IdiomRepo;
import com.swahilib.core.database.daos.IdiomDao;
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
public final class DataModule_ProvideIdiomRepoFactory implements Factory<IdiomRepo> {
  private final Provider<IdiomDao> idiomDaoProvider;

  private final Provider<Postgrest> supabaseProvider;

  private DataModule_ProvideIdiomRepoFactory(Provider<IdiomDao> idiomDaoProvider,
      Provider<Postgrest> supabaseProvider) {
    this.idiomDaoProvider = idiomDaoProvider;
    this.supabaseProvider = supabaseProvider;
  }

  @Override
  public IdiomRepo get() {
    return provideIdiomRepo(idiomDaoProvider.get(), supabaseProvider.get());
  }

  public static DataModule_ProvideIdiomRepoFactory create(Provider<IdiomDao> idiomDaoProvider,
      Provider<Postgrest> supabaseProvider) {
    return new DataModule_ProvideIdiomRepoFactory(idiomDaoProvider, supabaseProvider);
  }

  public static IdiomRepo provideIdiomRepo(IdiomDao idiomDao, Postgrest supabase) {
    return Preconditions.checkNotNullFromProvides(DataModule.INSTANCE.provideIdiomRepo(idiomDao, supabase));
  }
}
