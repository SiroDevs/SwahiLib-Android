package com.swahilib.core.data.repos;

import com.swahilib.core.database.daos.SayingDao;
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
public final class SayingRepo_Factory implements Factory<SayingRepo> {
  private final Provider<SayingDao> sayingsDaoProvider;

  private final Provider<Postgrest> supabaseProvider;

  private SayingRepo_Factory(Provider<SayingDao> sayingsDaoProvider,
      Provider<Postgrest> supabaseProvider) {
    this.sayingsDaoProvider = sayingsDaoProvider;
    this.supabaseProvider = supabaseProvider;
  }

  @Override
  public SayingRepo get() {
    return newInstance(sayingsDaoProvider.get(), supabaseProvider.get());
  }

  public static SayingRepo_Factory create(Provider<SayingDao> sayingsDaoProvider,
      Provider<Postgrest> supabaseProvider) {
    return new SayingRepo_Factory(sayingsDaoProvider, supabaseProvider);
  }

  public static SayingRepo newInstance(SayingDao sayingsDao, Postgrest supabase) {
    return new SayingRepo(sayingsDao, supabase);
  }
}
