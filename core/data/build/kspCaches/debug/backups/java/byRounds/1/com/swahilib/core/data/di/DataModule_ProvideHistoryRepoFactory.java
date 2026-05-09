package com.swahilib.core.data.di;

import com.swahilib.core.data.repos.HistoryRepo;
import com.swahilib.core.database.daos.HistoryDao;
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
public final class DataModule_ProvideHistoryRepoFactory implements Factory<HistoryRepo> {
  private final Provider<HistoryDao> historyDaoProvider;

  private final Provider<Postgrest> supabaseProvider;

  private DataModule_ProvideHistoryRepoFactory(Provider<HistoryDao> historyDaoProvider,
      Provider<Postgrest> supabaseProvider) {
    this.historyDaoProvider = historyDaoProvider;
    this.supabaseProvider = supabaseProvider;
  }

  @Override
  public HistoryRepo get() {
    return provideHistoryRepo(historyDaoProvider.get(), supabaseProvider.get());
  }

  public static DataModule_ProvideHistoryRepoFactory create(Provider<HistoryDao> historyDaoProvider,
      Provider<Postgrest> supabaseProvider) {
    return new DataModule_ProvideHistoryRepoFactory(historyDaoProvider, supabaseProvider);
  }

  public static HistoryRepo provideHistoryRepo(HistoryDao historyDao, Postgrest supabase) {
    return Preconditions.checkNotNullFromProvides(DataModule.INSTANCE.provideHistoryRepo(historyDao, supabase));
  }
}
