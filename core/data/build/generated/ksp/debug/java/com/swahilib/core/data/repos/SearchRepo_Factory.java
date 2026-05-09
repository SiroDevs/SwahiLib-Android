package com.swahilib.core.data.repos;

import com.swahilib.core.database.daos.SearchDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class SearchRepo_Factory implements Factory<SearchRepo> {
  private final Provider<SearchDao> searchesDaoProvider;

  private final Provider<Postgrest> supabaseProvider;

  private SearchRepo_Factory(Provider<SearchDao> searchesDaoProvider,
      Provider<Postgrest> supabaseProvider) {
    this.searchesDaoProvider = searchesDaoProvider;
    this.supabaseProvider = supabaseProvider;
  }

  @Override
  public SearchRepo get() {
    return newInstance(searchesDaoProvider.get(), supabaseProvider.get());
  }

  public static SearchRepo_Factory create(Provider<SearchDao> searchesDaoProvider,
      Provider<Postgrest> supabaseProvider) {
    return new SearchRepo_Factory(searchesDaoProvider, supabaseProvider);
  }

  public static SearchRepo newInstance(SearchDao searchesDao, Postgrest supabase) {
    return new SearchRepo(searchesDao, supabase);
  }
}
