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
public final class NetworkConfigModule_ProvideSupabaseUrlFactory implements Factory<String> {
  @Override
  public String get() {
    return provideSupabaseUrl();
  }

  public static NetworkConfigModule_ProvideSupabaseUrlFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static String provideSupabaseUrl() {
    return Preconditions.checkNotNullFromProvides(NetworkConfigModule.INSTANCE.provideSupabaseUrl());
  }

  private static final class InstanceHolder {
    static final NetworkConfigModule_ProvideSupabaseUrlFactory INSTANCE = new NetworkConfigModule_ProvideSupabaseUrlFactory();
  }
}
