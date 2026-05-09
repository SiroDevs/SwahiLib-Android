package com.swahilib.core.database.di;

import com.swahilib.core.database.AppDatabase;
import com.swahilib.core.database.daos.SayingDao;
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
public final class DatabaseModule_ProvideSayingDaoFactory implements Factory<SayingDao> {
  private final Provider<AppDatabase> dbProvider;

  private DatabaseModule_ProvideSayingDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public SayingDao get() {
    return provideSayingDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideSayingDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideSayingDaoFactory(dbProvider);
  }

  public static SayingDao provideSayingDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideSayingDao(db));
  }
}
