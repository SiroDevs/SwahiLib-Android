package com.swahilib.core.data.di;

import com.swahilib.core.data.repos.SearchRepo;
import com.swahilib.core.database.daos.SearchDao;
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
public final class DataModule_ProvideSearchRepoFactory implements Factory<SearchRepo> {
  private final Provider<SearchDao> searchDaoProvider;

  private final Provider<Postgrest> supabaseProvider;

  private DataModule_ProvideSearchRepoFactory(Provider<SearchDao> searchDaoProvider,
      Provider<Postgrest> supabaseProvider) {
    this.searchDaoProvider = searchDaoProvider;
    this.supabaseProvider = supabaseProvider;
  }

  @Override
  public SearchRepo get() {
    return provideSearchRepo(searchDaoProvider.get(), supabaseProvider.get());
  }

  public static DataModule_ProvideSearchRepoFactory create(Provider<SearchDao> searchDaoProvider,
      Provider<Postgrest> supabaseProvider) {
    return new DataModule_ProvideSearchRepoFactory(searchDaoProvider, supabaseProvider);
  }

  public static SearchRepo provideSearchRepo(SearchDao searchDao, Postgrest supabase) {
    return Preconditions.checkNotNullFromProvides(DataModule.INSTANCE.provideSearchRepo(searchDao, supabase));
  }
}
