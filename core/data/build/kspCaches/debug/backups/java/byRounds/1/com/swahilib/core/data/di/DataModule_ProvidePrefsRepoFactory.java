package com.swahilib.core.data.di;

import android.content.Context;
import com.swahilib.core.data.repos.PrefsRepo;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DataModule_ProvidePrefsRepoFactory implements Factory<PrefsRepo> {
  private final Provider<Context> ctxProvider;

  private DataModule_ProvidePrefsRepoFactory(Provider<Context> ctxProvider) {
    this.ctxProvider = ctxProvider;
  }

  @Override
  public PrefsRepo get() {
    return providePrefsRepo(ctxProvider.get());
  }

  public static DataModule_ProvidePrefsRepoFactory create(Provider<Context> ctxProvider) {
    return new DataModule_ProvidePrefsRepoFactory(ctxProvider);
  }

  public static PrefsRepo providePrefsRepo(Context ctx) {
    return Preconditions.checkNotNullFromProvides(DataModule.INSTANCE.providePrefsRepo(ctx));
  }
}
