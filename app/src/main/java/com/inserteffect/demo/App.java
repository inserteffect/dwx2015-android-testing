package com.inserteffect.demo;

import android.app.Application;
import android.support.annotation.VisibleForTesting;

import javax.inject.Singleton;

import dagger.Component;

public class App extends Application {

    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mAppComponent = DaggerApp_AppComponent.builder()
                .mainActivityModule(new MainActivity.MainActivityModule(this))
                .build();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

    @VisibleForTesting
    public void setAppComponent(AppComponent appComponent) {
        mAppComponent = appComponent;
    }


    @Singleton
    @Component(modules = {MainActivity.MainActivityModule.class})
    public interface AppComponent {

        void inject(MainActivity activity);
    }
}
