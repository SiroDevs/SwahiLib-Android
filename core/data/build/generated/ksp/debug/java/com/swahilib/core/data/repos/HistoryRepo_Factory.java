package com.swahilib.core.data.repos;

import com.swahilib.core.database.daos.HistoryDao;
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
public final class HistoryRepo_Factory implements Factory<HistoryRepo> {
  private final Provider<HistoryDao> historiesDaoProvider;

  private final Provider<Postgrest> supabaseProvider;

  private HistoryRepo_Factory(Provider<HistoryDao> historiesDaoProvider,
      Provider<Postgrest> supabaseProvider) {
    this.historiesDaoProvider = historiesDaoProvider;
    this.supabaseProvider = supabaseProvider;
  }

  @Override
  public HistoryRepo get() {
    return newInstance(historiesDaoProvider.get(), supabaseProvider.get());
  }

  public static HistoryRepo_Factory create(Provider<HistoryDao> historiesDaoProvider,
      Provider<Postgrest> supabaseProvider) {
    return new HistoryRepo_Factory(historiesDaoProvider, supabaseProvider);
  }

  public static HistoryRepo newInstance(HistoryDao historiesDao, Postgrest supabase) {
    return new HistoryRepo(historiesDao, supabase);
  }
}
