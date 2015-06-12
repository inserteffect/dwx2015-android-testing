package com.inserteffect.demo;

import com.inserteffect.demo.Service.Data;
import com.inserteffect.demo.Service.ServiceException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class, true, false);

    @Inject
    Service mService;

    @Before
    public void setUp() {

        final App app = (App) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
        final TestAppComponent component = DaggerMainActivityTest_TestAppComponent.builder()
                .testAndroidModule(new TestAndroidModule())
                .build();
        app.setAppComponent(component);
        component.inject(this);
    }

    @Test
    public void displayEmptyView() throws ServiceException {
        when(mService.getData(Matchers.<Integer>anyVararg())).thenReturn(new ArrayList<Data>());

        mActivityTestRule.launchActivity(new Intent());

        onView(withId(R.id.message))
                .check(matches(isDisplayed()))
                .check(matches(withText("Empty.")));
    }

    @Test
    public void displayErrorView() throws ServiceException {
        when(mService.getData(Matchers.<Integer>anyVararg())).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                throw new ServiceException("Test exception.");
            }
        });

        mActivityTestRule.launchActivity(new Intent());

        onView(withId(R.id.message))
                .check(matches(isDisplayed()))
                .check(matches(withText("Test exception.")));
    }

    @Test
    public void displayData() throws ServiceException {

        final ArrayList<Data> data = new ArrayList<>();
        data.add(new Data() {
            @Override
            public String getTitle() {
                return "Title";
            }

            @Override
            public String getDescription() {
                return "Description";
            }
        });
        when(mService.getData(Matchers.<Integer>anyVararg())).thenReturn(data);

        mActivityTestRule.launchActivity(new Intent());

        onData(allOf(is(instanceOf(Data.class))))
                .inAdapterView(withId(R.id.list))
                .atPosition(0)
                .onChildView(withId(R.id.title))
                .check(matches(withText("Title")));

        onData(allOf(is(instanceOf(Data.class))))
                .inAdapterView(withId(R.id.list))
                .atPosition(0)
                .onChildView(withId(R.id.description))
                .check(matches(withText("Description")));

        onView(withId(R.id.message))
                .check(matches(not(isDisplayed())));

    }

    @Test
    public void shouldDisplayExceptionMessageAfterRefreshing() throws ServiceException {

        when(mService.getData(Matchers.<Integer>anyVararg())).thenReturn(new ArrayList<Data>());

        mActivityTestRule.launchActivity(new Intent());

        onView(withId(R.id.message))
                .check(matches(isDisplayed()))
                .check(matches(withText("Empty.")));

        when(mService.getData(Matchers.<Integer>anyVararg())).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                throw new ServiceException("Test exception.");
            }
        });

        onView(withId(R.id.action_refresh))
                .perform(click());

        onView(withId(R.id.message))
                .check(matches(isDisplayed()))
                .check(matches(withText("Test exception.")));

    }

    @Singleton
    @Component(modules = TestAndroidModule.class)
    public interface TestAppComponent extends App.AppComponent {

        void inject(MainActivityTest activity);
    }

    @Module
    public class TestAndroidModule {

        @Provides
        @Singleton
        Service provideService() {
            return Mockito.mock(Service.class);
        }
    }
}
