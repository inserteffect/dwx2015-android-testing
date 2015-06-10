package com.inserteffect.demo;

import com.inserteffect.demo.Service.Provider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.test.suitebuilder.annotation.SmallTest;

import static com.inserteffect.demo.Service.ServiceException;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SmallTest
public class ServiceTest {

    @Mock
    Context mContext;

    @Mock
    ConnectivityManager mConnectivityManager;

    @Mock
    NetworkInfo mNetworkInfo;

    private Service mService;

    @Before
    public void setUp() {
        mService = new Service(mContext, Mockito.mock(Provider.class));
        when(mContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mConnectivityManager);
    }

    @Test(expected = ServiceException.class)
    public void shouldThrowServiceExceptionIfNotConnectedOrConnecting() throws ServiceException {

        when(mConnectivityManager.getActiveNetworkInfo()).thenReturn(mNetworkInfo);
        when(mNetworkInfo.isConnectedOrConnecting()).thenReturn(false);
        mService.getData();
    }

    @Test(expected = ServiceException.class)
    public void shouldThrowServiceExceptionIfNoNetworkInfoAvailable() throws ServiceException {

        when(mConnectivityManager.getActiveNetworkInfo()).thenReturn(null);
        mService.getData();
    }

    @Test
    public void shouldReturnData() throws ServiceException {

        when(mConnectivityManager.getActiveNetworkInfo()).thenReturn(mNetworkInfo);
        when(mNetworkInfo.isConnectedOrConnecting()).thenReturn(true);
        assertNotNull(mService.getData());
    }
}
