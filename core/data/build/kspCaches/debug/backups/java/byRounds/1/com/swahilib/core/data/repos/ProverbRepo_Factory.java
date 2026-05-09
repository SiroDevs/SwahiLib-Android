package com.swahilib.core.data.repos;

import com.swahilib.core.database.daos.ProverbDao;
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
public final class ProverbRepo_Factory implements Factory<ProverbRepo> {
  private final Provider<ProverbDao> proverbsDaoProvider;

  private final Provider<Postgrest> supabaseProvider;

  private ProverbRepo_Factory(Provider<ProverbDao> proverbsDaoProvider,
      Provider<Postgrest> supabaseProvider) {
    this.proverbsDaoProvider = proverbsDaoProvider;
    this.supabaseProvider = supabaseProvider;
  }

  @Override
  public ProverbRepo get() {
    return newInstance(proverbsDaoProvider.get(), supabaseProvider.get());
  }

  public static ProverbRepo_Factory create(Provider<ProverbDao> proverbsDaoProvider,
      Provider<Postgrest> supabaseProvider) {
    return new ProverbRepo_Factory(proverbsDaoProvider, supabaseProvider);
  }

  public static ProverbRepo newInstance(ProverbDao proverbsDao, Postgrest supabase) {
    return new ProverbRepo(proverbsDao, supabase);
  }
}
