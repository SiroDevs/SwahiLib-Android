package com.swahilib.core.data.repos;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
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
public final class SubsRepo_Factory implements Factory<SubsRepo> {
  @Override
  public SubsRepo get() {
    return newInstance();
  }

  public static SubsRepo_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static SubsRepo newInstance() {
    return new SubsRepo();
  }

  private static final class InstanceHolder {
    static final SubsRepo_Factory INSTANCE = new SubsRepo_Factory();
  }
}
