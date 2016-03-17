package com.butterfly.ram.butterfly;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Xml;
import android.view.MenuItem;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainMenu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ViewPager viewPager;
    private RecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        new NetworkTask().execute("http://api-fotki.yandex.ru/api/podhistory/poddate;2012-04-01T12:00:00Z/");
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        ViewPagerFragment yandexFotki = new ViewPagerFragment();
        adapter.addFragment(new ViewPagerFragment(), stringById(R.string.gallery));
        adapter.addFragment(yandexFotki, stringById(R.string.yafotki));
        adapter.addFragment(new ViewPagerFragment(), stringById(R.string.favorites));
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<ViewPagerFragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public ViewPagerFragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(ViewPagerFragment fragment, String title) {
//            fragment.title = title;
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_gallery) {
            viewPager.setCurrentItem(0);
        } else if (id == R.id.nav_yafotki) {
            viewPager.setCurrentItem(1);
        } else if (id == R.id.nav_favorites) {
            viewPager.setCurrentItem(2);
        } else if (id == R.id.nav_take_photo) {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivity(cameraIntent);
        } else if (id == R.id.nav_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        } else if (id == R.id.nav_report) {
            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            emailIntent.setType("vnd.android.cursor.item/email");
            String email = stringById(R.string.dev_email);
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ email });
            startActivity(Intent.createChooser(emailIntent, "Send mail using..."));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private String stringById(int id) {
        return getResources().getString(id);
    }

    private class NetworkTask extends AsyncTask<String, Void, List<String>> {
        @Override
        protected List<String> doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream stream = connection.getInputStream();
                    XmlPullParser parser = Xml.newPullParser();
                    parser.setInput(stream, null);
                    String imgUrl = null;
                    List<String> imgUrls = new ArrayList<>();
                    while ((imgUrl = processEntry(parser)) != null) {
                        imgUrls.add(imgUrl);
                    }
                    return imgUrls;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            return null;
        }

        private String processEntry(XmlPullParser parser) throws IOException, XmlPullParserException {
            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                String prefix = parser.getPrefix();
                // Starts by looking for the entry tag
                if ("f".equals(prefix) && "img".equals(name)) {
                    for (int i = 0; i < parser.getAttributeCount(); i++) {
                        if ("height".equals(parser.getAttributeName(i))) {
                            if ("75".equals(parser.getAttributeValue(i))) {
                                return parser.getAttributeValue(i + 1);
                            }
                        }
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<String> imageURLs) {

        }
    }
}
