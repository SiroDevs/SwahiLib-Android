package com.swahilib.core.data.repos;

import com.swahilib.core.database.daos.IdiomDao;
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
public final class IdiomRepo_Factory implements Factory<IdiomRepo> {
  private final Provider<IdiomDao> idiomsDaoProvider;

  private final Provider<Postgrest> supabaseProvider;

  private IdiomRepo_Factory(Provider<IdiomDao> idiomsDaoProvider,
      Provider<Postgrest> supabaseProvider) {
    this.idiomsDaoProvider = idiomsDaoProvider;
    this.supabaseProvider = supabaseProvider;
  }

  @Override
  public IdiomRepo get() {
    return newInstance(idiomsDaoProvider.get(), supabaseProvider.get());
  }

  public static IdiomRepo_Factory create(Provider<IdiomDao> idiomsDaoProvider,
      Provider<Postgrest> supabaseProvider) {
    return new IdiomRepo_Factory(idiomsDaoProvider, supabaseProvider);
  }

  public static IdiomRepo newInstance(IdiomDao idiomsDao, Postgrest supabase) {
    return new IdiomRepo(idiomsDao, supabase);
  }
}
