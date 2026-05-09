package com.swahilib.core.data.repos;

import com.swahilib.core.database.daos.WordDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.github.jan.supabase.postgrest.Postgrest;
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
public final class WordRepo_Factory implements Factory<WordRepo> {
  private final Provider<WordDao> wordsDaoProvider;

  private final Provider<Postgrest> supabaseProvider;

  private WordRepo_Factory(Provider<WordDao> wordsDaoProvider,
      Provider<Postgrest> supabaseProvider) {
    this.wordsDaoProvider = wordsDaoProvider;
    this.supabaseProvider = supabaseProvider;
  }

  @Override
  public WordRepo get() {
    return newInstance(wordsDaoProvider.get(), supabaseProvider.get());
  }

  public static WordRepo_Factory create(Provider<WordDao> wordsDaoProvider,
      Provider<Postgrest> supabaseProvider) {
    return new WordRepo_Factory(wordsDaoProvider, supabaseProvider);
  }

  public static WordRepo newInstance(WordDao wordsDao, Postgrest supabase) {
    return new WordRepo(wordsDao, supabase);
  }
}
