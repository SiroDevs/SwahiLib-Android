package com.swahilib.core.data.di;

import com.swahilib.core.data.repos.SayingRepo;
import com.swahilib.core.database.daos.SayingDao;
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
public final class DataModule_ProvideSayingRepoFactory implements Factory<SayingRepo> {
  private final Provider<SayingDao> sayingDaoProvider;

  private final Provider<Postgrest> supabaseProvider;

  private DataModule_ProvideSayingRepoFactory(Provider<SayingDao> sayingDaoProvider,
      Provider<Postgrest> supabaseProvider) {
    this.sayingDaoProvider = sayingDaoProvider;
    this.supabaseProvider = supabaseProvider;
  }

  @Override
  public SayingRepo get() {
    return provideSayingRepo(sayingDaoProvider.get(), supabaseProvider.get());
  }

  public static DataModule_ProvideSayingRepoFactory create(Provider<SayingDao> sayingDaoProvider,
      Provider<Postgrest> supabaseProvider) {
    return new DataModule_ProvideSayingRepoFactory(sayingDaoProvider, supabaseProvider);
  }

  public static SayingRepo provideSayingRepo(SayingDao sayingDao, Postgrest supabase) {
    return Preconditions.checkNotNullFromProvides(DataModule.INSTANCE.provideSayingRepo(sayingDao, supabase));
  }
}
