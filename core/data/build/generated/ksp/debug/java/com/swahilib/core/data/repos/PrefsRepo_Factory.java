package com.swahilib.core.data.repos;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class PrefsRepo_Factory implements Factory<PrefsRepo> {
  private final Provider<Context> contextProvider;

  private PrefsRepo_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public PrefsRepo get() {
    return newInstance(contextProvider.get());
  }

  public static PrefsRepo_Factory create(Provider<Context> contextProvider) {
    return new PrefsRepo_Factory(contextProvider);
  }

  public static PrefsRepo newInstance(Context context) {
    return new PrefsRepo(context);
  }
}
