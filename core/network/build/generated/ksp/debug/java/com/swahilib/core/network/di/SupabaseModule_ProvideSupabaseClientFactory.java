package com.swahilib.core.network.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.github.jan.supabase.SupabaseClient;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("javax.inject.Named")
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
public final class SupabaseModule_ProvideSupabaseClientFactory implements Factory<SupabaseClient> {
  private final Provider<String> urlProvider;

  private final Provider<String> keyProvider;

  private SupabaseModule_ProvideSupabaseClientFactory(Provider<String> urlProvider,
      Provider<String> keyProvider) {
    this.urlProvider = urlProvider;
    this.keyProvider = keyProvider;
  }

  @Override
  public SupabaseClient get() {
    return provideSupabaseClient(urlProvider.get(), keyProvider.get());
  }

  public static SupabaseModule_ProvideSupabaseClientFactory create(Provider<String> urlProvider,
      Provider<String> keyProvider) {
    return new SupabaseModule_ProvideSupabaseClientFactory(urlProvider, keyProvider);
  }

  public static SupabaseClient provideSupabaseClient(String url, String key) {
    return Preconditions.checkNotNullFromProvides(SupabaseModule.INSTANCE.provideSupabaseClient(url, key));
  }
}
