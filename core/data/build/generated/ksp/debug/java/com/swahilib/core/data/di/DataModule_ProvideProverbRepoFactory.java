package com.swahilib.core.data.di;

import com.swahilib.core.data.repos.ProverbRepo;
import com.swahilib.core.database.daos.ProverbDao;
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
public final class DataModule_ProvideProverbRepoFactory implements Factory<ProverbRepo> {
  private final Provider<ProverbDao> proverbDaoProvider;

  private final Provider<Postgrest> supabaseProvider;

  private DataModule_ProvideProverbRepoFactory(Provider<ProverbDao> proverbDaoProvider,
      Provider<Postgrest> supabaseProvider) {
    this.proverbDaoProvider = proverbDaoProvider;
    this.supabaseProvider = supabaseProvider;
  }

  @Override
  public ProverbRepo get() {
    return provideProverbRepo(proverbDaoProvider.get(), supabaseProvider.get());
  }

  public static DataModule_ProvideProverbRepoFactory create(Provider<ProverbDao> proverbDaoProvider,
      Provider<Postgrest> supabaseProvider) {
    return new DataModule_ProvideProverbRepoFactory(proverbDaoProvider, supabaseProvider);
  }

  public static ProverbRepo provideProverbRepo(ProverbDao proverbDao, Postgrest supabase) {
    return Preconditions.checkNotNullFromProvides(DataModule.INSTANCE.provideProverbRepo(proverbDao, supabase));
  }
}
