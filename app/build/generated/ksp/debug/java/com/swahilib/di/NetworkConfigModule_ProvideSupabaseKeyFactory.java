package com.swahilib.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
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
public final class NetworkConfigModule_ProvideSupabaseKeyFactory implements Factory<String> {
  @Override
  public String get() {
    return provideSupabaseKey();
  }

  public static NetworkConfigModule_ProvideSupabaseKeyFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static String provideSupabaseKey() {
    return Preconditions.checkNotNullFromProvides(NetworkConfigModule.INSTANCE.provideSupabaseKey());
  }

  private static final class InstanceHolder {
    static final NetworkConfigModule_ProvideSupabaseKeyFactory INSTANCE = new NetworkConfigModule_ProvideSupabaseKeyFactory();
  }
}
