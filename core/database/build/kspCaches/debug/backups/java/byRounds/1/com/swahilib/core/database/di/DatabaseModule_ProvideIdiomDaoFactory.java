package com.swahilib.core.database.di;

import com.swahilib.core.database.AppDatabase;
import com.swahilib.core.database.daos.IdiomDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
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
public final class DatabaseModule_ProvideIdiomDaoFactory implements Factory<IdiomDao> {
  private final Provider<AppDatabase> dbProvider;

  private DatabaseModule_ProvideIdiomDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public IdiomDao get() {
    return provideIdiomDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideIdiomDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideIdiomDaoFactory(dbProvider);
  }

  public static IdiomDao provideIdiomDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideIdiomDao(db));
  }
}
