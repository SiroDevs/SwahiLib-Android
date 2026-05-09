package com.swahilib;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.swahilib.core.data.di.DataModule_ProvideIdiomRepoFactory;
import com.swahilib.core.data.di.DataModule_ProvidePrefsRepoFactory;
import com.swahilib.core.data.di.DataModule_ProvideProverbRepoFactory;
import com.swahilib.core.data.di.DataModule_ProvideSayingRepoFactory;
import com.swahilib.core.data.di.DataModule_ProvideSubsRepoFactory;
import com.swahilib.core.data.di.DataModule_ProvideWordRepoFactory;
import com.swahilib.core.data.repos.IdiomRepo;
import com.swahilib.core.data.repos.PrefsRepo;
import com.swahilib.core.data.repos.ProverbRepo;
import com.swahilib.core.data.repos.SayingRepo;
import com.swahilib.core.data.repos.SubsRepo;
import com.swahilib.core.data.repos.ThemeRepo;
import com.swahilib.core.data.repos.ThemeRepo_HiltModules;
import com.swahilib.core.data.repos.ThemeRepo_HiltModules_BindsModule_Binds_LazyMapKey;
import com.swahilib.core.data.repos.ThemeRepo_HiltModules_KeyModule_Provide_LazyMapKey;
import com.swahilib.core.data.repos.WordRepo;
import com.swahilib.core.database.AppDatabase;
import com.swahilib.core.database.daos.IdiomDao;
import com.swahilib.core.database.daos.ProverbDao;
import com.swahilib.core.database.daos.SayingDao;
import com.swahilib.core.database.daos.WordDao;
import com.swahilib.core.database.di.DatabaseModule_ProvideDatabaseFactory;
import com.swahilib.core.database.di.DatabaseModule_ProvideIdiomDaoFactory;
import com.swahilib.core.database.di.DatabaseModule_ProvideProverbDaoFactory;
import com.swahilib.core.database.di.DatabaseModule_ProvideSayingDaoFactory;
import com.swahilib.core.database.di.DatabaseModule_ProvideWordDaoFactory;
import com.swahilib.core.network.di.SupabaseModule_ProvidePostgrestFactory;
import com.swahilib.core.network.di.SupabaseModule_ProvideSupabaseClientFactory;
import com.swahilib.di.NetworkConfigModule_ProvideSupabaseKeyFactory;
import com.swahilib.di.NetworkConfigModule_ProvideSupabaseUrlFactory;
import com.swahilib.feature.home.HomeViewModel;
import com.swahilib.feature.home.HomeViewModel_HiltModules;
import com.swahilib.feature.home.HomeViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.swahilib.feature.home.HomeViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.swahilib.feature.idiom.IdiomViewModel;
import com.swahilib.feature.idiom.IdiomViewModel_HiltModules;
import com.swahilib.feature.idiom.IdiomViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.swahilib.feature.idiom.IdiomViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.swahilib.feature.init.InitViewModel;
import com.swahilib.feature.init.InitViewModel_HiltModules;
import com.swahilib.feature.init.InitViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.swahilib.feature.init.InitViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.swahilib.feature.proverb.ProverbViewModel;
import com.swahilib.feature.proverb.ProverbViewModel_HiltModules;
import com.swahilib.feature.proverb.ProverbViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.swahilib.feature.proverb.ProverbViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.swahilib.feature.saying.SayingViewModel;
import com.swahilib.feature.saying.SayingViewModel_HiltModules;
import com.swahilib.feature.saying.SayingViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.swahilib.feature.saying.SayingViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.swahilib.feature.settings.SettingsViewModel;
import com.swahilib.feature.settings.SettingsViewModel_HiltModules;
import com.swahilib.feature.settings.SettingsViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.swahilib.feature.settings.SettingsViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.swahilib.feature.splash.SplashViewModel;
import com.swahilib.feature.splash.SplashViewModel_HiltModules;
import com.swahilib.feature.splash.SplashViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.swahilib.feature.splash.SplashViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.swahilib.feature.word.WordViewModel;
import com.swahilib.feature.word.WordViewModel_HiltModules;
import com.swahilib.feature.word.WordViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.swahilib.feature.word.WordViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import io.github.jan.supabase.SupabaseClient;
import io.github.jan.supabase.postgrest.Postgrest;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

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
public final class DaggerSwahiLibApp_HiltComponents_SingletonC {
  private DaggerSwahiLibApp_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public SwahiLibApp_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements SwahiLibApp_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public SwahiLibApp_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements SwahiLibApp_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public SwahiLibApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements SwahiLibApp_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public SwahiLibApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements SwahiLibApp_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public SwahiLibApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements SwahiLibApp_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public SwahiLibApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements SwahiLibApp_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public SwahiLibApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements SwahiLibApp_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public SwahiLibApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends SwahiLibApp_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends SwahiLibApp_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    FragmentCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends SwahiLibApp_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends SwahiLibApp_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    ActivityCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity arg0) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(9).put(HomeViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, HomeViewModel_HiltModules.KeyModule.provide()).put(IdiomViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, IdiomViewModel_HiltModules.KeyModule.provide()).put(InitViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, InitViewModel_HiltModules.KeyModule.provide()).put(ProverbViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, ProverbViewModel_HiltModules.KeyModule.provide()).put(SayingViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, SayingViewModel_HiltModules.KeyModule.provide()).put(SettingsViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, SettingsViewModel_HiltModules.KeyModule.provide()).put(SplashViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, SplashViewModel_HiltModules.KeyModule.provide()).put(ThemeRepo_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, ThemeRepo_HiltModules.KeyModule.provide()).put(WordViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, WordViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }
  }

  private static final class ViewModelCImpl extends SwahiLibApp_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    Provider<HomeViewModel> homeViewModelProvider;

    Provider<IdiomViewModel> idiomViewModelProvider;

    Provider<InitViewModel> initViewModelProvider;

    Provider<ProverbViewModel> proverbViewModelProvider;

    Provider<SayingViewModel> sayingViewModelProvider;

    Provider<SettingsViewModel> settingsViewModelProvider;

    Provider<SplashViewModel> splashViewModelProvider;

    Provider<ThemeRepo> themeRepoProvider;

    Provider<WordViewModel> wordViewModelProvider;

    ViewModelCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        SavedStateHandle savedStateHandleParam, ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.homeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.idiomViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.initViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.proverbViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.sayingViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.settingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.splashViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.themeRepoProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
      this.wordViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 8);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(9).put(HomeViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (homeViewModelProvider))).put(IdiomViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (idiomViewModelProvider))).put(InitViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (initViewModelProvider))).put(ProverbViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (proverbViewModelProvider))).put(SayingViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (sayingViewModelProvider))).put(SettingsViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (settingsViewModelProvider))).put(SplashViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (splashViewModelProvider))).put(ThemeRepo_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (themeRepoProvider))).put(WordViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (wordViewModelProvider))).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @Override
      @SuppressWarnings("unchecked")
      public T get() {
        switch (id) {
          case 0: // com.swahilib.feature.home.HomeViewModel
          return (T) new HomeViewModel(singletonCImpl.provideIdiomRepoProvider.get(), singletonCImpl.provideProverbRepoProvider.get(), singletonCImpl.provideSayingRepoProvider.get(), singletonCImpl.provideWordRepoProvider.get(), singletonCImpl.providePrefsRepoProvider.get());

          case 1: // com.swahilib.feature.idiom.IdiomViewModel
          return (T) new IdiomViewModel(singletonCImpl.provideIdiomRepoProvider.get());

          case 2: // com.swahilib.feature.init.InitViewModel
          return (T) new InitViewModel(singletonCImpl.provideIdiomRepoProvider.get(), singletonCImpl.provideProverbRepoProvider.get(), singletonCImpl.provideSayingRepoProvider.get(), singletonCImpl.provideWordRepoProvider.get(), singletonCImpl.providePrefsRepoProvider.get());

          case 3: // com.swahilib.feature.proverb.ProverbViewModel
          return (T) new ProverbViewModel(singletonCImpl.provideProverbRepoProvider.get());

          case 4: // com.swahilib.feature.saying.SayingViewModel
          return (T) new SayingViewModel(singletonCImpl.provideSayingRepoProvider.get());

          case 5: // com.swahilib.feature.settings.SettingsViewModel
          return (T) new SettingsViewModel(singletonCImpl.providePrefsRepoProvider.get());

          case 6: // com.swahilib.feature.splash.SplashViewModel
          return (T) new SplashViewModel(singletonCImpl.providePrefsRepoProvider.get(), singletonCImpl.provideSubsRepoProvider.get());

          case 7: // com.swahilib.core.data.repos.ThemeRepo
          return (T) new ThemeRepo(singletonCImpl.providePrefsRepoProvider.get());

          case 8: // com.swahilib.feature.word.WordViewModel
          return (T) new WordViewModel(singletonCImpl.provideWordRepoProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends SwahiLibApp_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @Override
      @SuppressWarnings("unchecked")
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends SwahiLibApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends SwahiLibApp_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    Provider<AppDatabase> provideDatabaseProvider;

    Provider<SupabaseClient> provideSupabaseClientProvider;

    Provider<Postgrest> providePostgrestProvider;

    Provider<IdiomRepo> provideIdiomRepoProvider;

    Provider<ProverbRepo> provideProverbRepoProvider;

    Provider<SayingRepo> provideSayingRepoProvider;

    Provider<WordRepo> provideWordRepoProvider;

    Provider<PrefsRepo> providePrefsRepoProvider;

    Provider<SubsRepo> provideSubsRepoProvider;

    SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    IdiomDao idiomDao() {
      return DatabaseModule_ProvideIdiomDaoFactory.provideIdiomDao(provideDatabaseProvider.get());
    }

    ProverbDao proverbDao() {
      return DatabaseModule_ProvideProverbDaoFactory.provideProverbDao(provideDatabaseProvider.get());
    }

    SayingDao sayingDao() {
      return DatabaseModule_ProvideSayingDaoFactory.provideSayingDao(provideDatabaseProvider.get());
    }

    WordDao wordDao() {
      return DatabaseModule_ProvideWordDaoFactory.provideWordDao(provideDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<AppDatabase>(singletonCImpl, 1));
      this.provideSupabaseClientProvider = DoubleCheck.provider(new SwitchingProvider<SupabaseClient>(singletonCImpl, 3));
      this.providePostgrestProvider = DoubleCheck.provider(new SwitchingProvider<Postgrest>(singletonCImpl, 2));
      this.provideIdiomRepoProvider = DoubleCheck.provider(new SwitchingProvider<IdiomRepo>(singletonCImpl, 0));
      this.provideProverbRepoProvider = DoubleCheck.provider(new SwitchingProvider<ProverbRepo>(singletonCImpl, 4));
      this.provideSayingRepoProvider = DoubleCheck.provider(new SwitchingProvider<SayingRepo>(singletonCImpl, 5));
      this.provideWordRepoProvider = DoubleCheck.provider(new SwitchingProvider<WordRepo>(singletonCImpl, 6));
      this.providePrefsRepoProvider = DoubleCheck.provider(new SwitchingProvider<PrefsRepo>(singletonCImpl, 7));
      this.provideSubsRepoProvider = DoubleCheck.provider(new SwitchingProvider<SubsRepo>(singletonCImpl, 8));
    }

    @Override
    public void injectSwahiLibApp(SwahiLibApp arg0) {
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @Override
      @SuppressWarnings("unchecked")
      public T get() {
        switch (id) {
          case 0: // com.swahilib.core.data.repos.IdiomRepo
          return (T) DataModule_ProvideIdiomRepoFactory.provideIdiomRepo(singletonCImpl.idiomDao(), singletonCImpl.providePostgrestProvider.get());

          case 1: // com.swahilib.core.database.AppDatabase
          return (T) DatabaseModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 2: // io.github.jan.supabase.postgrest.Postgrest
          return (T) SupabaseModule_ProvidePostgrestFactory.providePostgrest(singletonCImpl.provideSupabaseClientProvider.get());

          case 3: // io.github.jan.supabase.SupabaseClient
          return (T) SupabaseModule_ProvideSupabaseClientFactory.provideSupabaseClient(NetworkConfigModule_ProvideSupabaseUrlFactory.provideSupabaseUrl(), NetworkConfigModule_ProvideSupabaseKeyFactory.provideSupabaseKey());

          case 4: // com.swahilib.core.data.repos.ProverbRepo
          return (T) DataModule_ProvideProverbRepoFactory.provideProverbRepo(singletonCImpl.proverbDao(), singletonCImpl.providePostgrestProvider.get());

          case 5: // com.swahilib.core.data.repos.SayingRepo
          return (T) DataModule_ProvideSayingRepoFactory.provideSayingRepo(singletonCImpl.sayingDao(), singletonCImpl.providePostgrestProvider.get());

          case 6: // com.swahilib.core.data.repos.WordRepo
          return (T) DataModule_ProvideWordRepoFactory.provideWordRepo(singletonCImpl.wordDao(), singletonCImpl.providePostgrestProvider.get());

          case 7: // com.swahilib.core.data.repos.PrefsRepo
          return (T) DataModule_ProvidePrefsRepoFactory.providePrefsRepo(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 8: // com.swahilib.core.data.repos.SubsRepo
          return (T) DataModule_ProvideSubsRepoFactory.provideSubsRepo();

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
