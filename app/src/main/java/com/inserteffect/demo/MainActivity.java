package com.inserteffect.demo;

import com.inserteffect.demo.plugin.OpenWeatherMap;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import butterknife.ButterKnife;
import butterknife.InjectView;
import dagger.Module;
import dagger.Provides;

import static com.inserteffect.demo.Service.Data;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    @Inject
    Service mService;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.message)
    TextView mMessage;

    @InjectView(R.id.list)
    ListView mListView;

    @InjectView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((App) getApplication()).getAppComponent().inject(this);

        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);

        mAdapter = new Adapter(this, new ArrayList<Data>());
        mListView.setAdapter(mAdapter);
        mListView.setEmptyView(mMessage);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        onRefresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            onRefresh();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        new AsyncTask<Integer, Void, List<Data>>() {

            public Service.ServiceException mException;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mAdapter.clear();
                mMessage.setText(null);
            }

            @Override
            protected List<Data> doInBackground(Integer... params) {
                List<Data> data = null;
                try {
                    data = mService.getData(params);
                } catch (Service.ServiceException e) {
                    e.printStackTrace();
                    mException = e;
                }
                return data;
            }

            @Override
            protected void onPostExecute(List<Data> data) {
                super.onPostExecute(data);
                if (!isCancelled() && !isFinishing()) {
                    if (mException != null) {
                        mMessage.setText(mException.getMessage());
                    } else {
                        if (data != null && data.size() > 0) {
                            mAdapter.addAll(data);
                        } else {
                            mMessage.setText("Empty.");
                        }
                    }
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }.execute(2951825);
    }

    @Module
    public final static class MainActivityModule {

        private final Context mContext;

        public MainActivityModule(Context context) {
            mContext = context;
        }

        @Provides
        @Singleton
        Service provideService() {
            return new Service(mContext, new OpenWeatherMap());
        }

    }

    static class Adapter extends ArrayAdapter<Data> {

        public Adapter(Context context, List<Data> data) {
            super(context, 0, data);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_2, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) convertView.getTag();

            final Data data = getItem(position);
            viewHolder.title.setText(data.getTitle());
            viewHolder.description.setText(data.getDescription());
            return convertView;
        }

        static class ViewHolder {

            @InjectView(R.id.title)
            TextView title;

            @InjectView(R.id.description)
            TextView description;

            public ViewHolder(View view) {
                ButterKnife.inject(this, view);
            }
        }
    }
}
