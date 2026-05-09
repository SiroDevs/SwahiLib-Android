package com.swahilib.core.database.di;

import com.swahilib.core.database.AppDatabase;
import com.swahilib.core.database.daos.ProverbDao;
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
public final class DatabaseModule_ProvideProverbDaoFactory implements Factory<ProverbDao> {
  private final Provider<AppDatabase> dbProvider;

  private DatabaseModule_ProvideProverbDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public ProverbDao get() {
    return provideProverbDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideProverbDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideProverbDaoFactory(dbProvider);
  }

  public static ProverbDao provideProverbDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideProverbDao(db));
  }
}
