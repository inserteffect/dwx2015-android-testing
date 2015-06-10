package com.inserteffect.demo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import java.util.List;

public class Service {

    private final Context mContext;

    private final Provider mProvider;

    public Service(@NonNull Context context, @NonNull Provider provider) {
        mContext = context;
        mProvider = provider;
    }

    public List<Data> getData(Integer... ids) throws ServiceException {
        if (isConnectedOrConnecting()) {
            return mProvider.getData(ids);
        } else {
            throw new ServiceException("No network connection.");
        }
    }

    private boolean isConnectedOrConnecting() {
        final ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isConnected = false;
        if (connectivityManager != null) {
            final NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return isConnected;
    }

    public interface Data {

        String getTitle();

        String getDescription();

    }

    public interface Provider {

        List<Data> getData(Integer... id) throws ServiceException;
    }

    public static class ServiceException extends Exception {

        public ServiceException(String message) {
            super(message);
        }
    }

}
