package com.swahilib.core.database.di;

import com.swahilib.core.database.AppDatabase;
import com.swahilib.core.database.daos.WordDao;
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
public final class DatabaseModule_ProvideWordDaoFactory implements Factory<WordDao> {
  private final Provider<AppDatabase> dbProvider;

  private DatabaseModule_ProvideWordDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public WordDao get() {
    return provideWordDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideWordDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideWordDaoFactory(dbProvider);
  }

  public static WordDao provideWordDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideWordDao(db));
  }
}
