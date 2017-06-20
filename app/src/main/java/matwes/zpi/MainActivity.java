package matwes.zpi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.login.LoginManager;

import matwes.zpi.Events.AddEventActivity;
import matwes.zpi.Events.EventsFragment;
import matwes.zpi.Events.MapFragment;
import matwes.zpi.Events.MyEventsFragment;
import matwes.zpi.Login.WelcomeActivity;
import matwes.zpi.Profile.MyProfileFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private MenuItem itemChangeView;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddEventActivity.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        replaceFragment(new EventsFragment(), "LIST");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.change_view) {
            if (isListFragmentAttached()) {
                replaceFragment(new MapFragment(), "MAP");
            } else {
                replaceFragment(new EventsFragment(), "LIST");
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        Fragment fragment = null;
        String tag = "";
        int fabVisibility = View.VISIBLE;

        if (id == R.id.events_list) {
            fragment = new EventsFragment();
            tag = "LIST";
        } else if (id == R.id.events_map) {
            fragment = new MapFragment();
            tag = "MAP";
        } else if (id == R.id.my_events) {
            fragment = new MyEventsFragment();
            tag = "MY";
        } else if (id == R.id.my_profile) {
            fragment = new MyProfileFragment();
            tag = "MY";
            fabVisibility = View.GONE;
        }
        else if (id == R.id.nav_log_out) {
            logout();
        }

        fab.setVisibility(fabVisibility);

        if (fragment != null)
            replaceFragment(fragment, tag);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void logout() {
        Common.setLoginStatus(this, false);
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    void replaceFragment(Fragment fragment, String tag) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_main, fragment, tag);
        ft.commit();
    }

    boolean isListFragmentAttached() {
        EventsFragment fragment = (EventsFragment) getSupportFragmentManager().findFragmentByTag("LIST");
        return (fragment != null && fragment.isVisible());
    }


}
