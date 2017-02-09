package com.alexispounne.projectplatypusii;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.beardedhen.androidbootstrap.BootstrapProgressBar;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;
import com.mongodb.MongoClient;

import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView textView;
    private Dialog realDialog;
    //    private JetPlayer jetPlayer;
    private MongoClient mongoClient;
    private MediaPlayer mediaPlayer;
    private int resid = -1;
    private NavigationView navigationView;
    private LinearLayout linearLayout;
    private String loc="fr";

    public void changeLanguage(String lang) {
        finish();
        loc=lang;
        startActivity(getIntent());
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LangSupportContextWrapper.wrap(newBase,loc));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Locale myLocale=new Locale("fr");
        Locale.setDefault(myLocale);
        Configuration config=getBaseContext().getResources().getConfiguration();
        config.setLocale(myLocale);
        Context context=createConfigurationContext(config);
        Resources resource=context.getResources(); // Initialise locale
        TypefaceProvider.registerDefaultIconSets(); // Initialise android-bootstrap
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, R.string.pangram, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        textView = (TextView) findViewById(R.id.dayum);
        String[] hues = getResources().getStringArray(R.array.colorSystem);
        final ColorArrayAdapter listAdapter = new ColorArrayAdapter(this, android.R.layout.simple_list_item_1, hues);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.ThemeOverlay_Material_Dark);
        final LinearLayout contentMain = (LinearLayout) findViewById(R.id.content_main);
        builder.setTitle(R.string.settingsBackgroundColour)
                .setIcon(R.drawable.ic_color_dialog)
                .setCancelable(false)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setAdapter(listAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int code = listAdapter.getCode(which);
                        contentMain.setBackgroundColor(code);
                        int a = code >> 24;
                        int r = (code - (a << 24)) >> 16;
                        int g = (code - (a << 24) - (r << 16)) >> 8;
                        int b = code - (a << 24) - (r << 16) - (g << 8);
                        Log.i("Code", Integer.toHexString(code));
                        Log.i("r", Integer.toString(r));
                        Log.i("g", Integer.toString(g));
                        Log.i("b", Integer.toString(b));
//                        if ((r + g + b) < 384) textView.setTextColor(getColor(R.color.White));
                        if ((r*0.299+g*0.587+b*0.114) < 128) textView.setTextColor(getColor(R.color.White));
                        else textView.setTextColor(getColor(R.color.Black));
                        dialog.cancel();
                    }
                });
        realDialog = builder.create();
        BootstrapButton bootstrapButton = (BootstrapButton) findViewById(R.id.bsbutt);
        linearLayout = (LinearLayout) navigationView.getHeaderView(0).findViewById(R.id.drawerTop);
        int[] fr = getResources().getIntArray(R.array.tricolore);
        final GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, fr);
        final BootstrapProgressBar bootstrapProgressBar = (BootstrapProgressBar) findViewById(R.id.middleBar);
        bootstrapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random rand = new Random();
                int randNum = rand.nextInt(40) + 1;
                linearLayout.setBackground(gradientDrawable);
                bootstrapProgressBar.setProgress(randNum);
                ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(2000);
            }
        });
        AdvancedFragmentAdapter adapter = new AdvancedFragmentAdapter(getSupportFragmentManager());
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void playSong(int resid, boolean looping) {
        if (this.resid == -1) {
            this.resid = resid;
            mediaPlayer = MediaPlayer.create(this, resid);
        } else {
            final int track00 = R.raw.rodena, track01 = R.raw.der_entwurf, track02 = R.raw.eclogue;
            switch (this.resid) {
                case track00:
                    mediaPlayer = MediaPlayer.create(this, track01);
                    this.resid = track01;
                    break;
                case track01:
                    mediaPlayer = MediaPlayer.create(this, track02);
                    mediaPlayer.setVolume(1f, 1f);
                    this.resid = track02;
                    break;
                case track02:
                    mediaPlayer = MediaPlayer.create(this, track00);
                    this.resid = track00;
                    break;
            }
        }
        mediaPlayer.setLooping(looping);
        mediaPlayer.start();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Random rand = new Random();

        int x = rand.nextInt(43046720) + 2;
        int y = rand.nextInt(43046720) + 2;
        int z = rand.nextInt(20) + 1;

        textView.append("\nThe gcd of " + x + " and " + y + " is " + Integer.toString(IntMath.gcd(x, y)) + ",\nwhile the factorial of " + z + " is " + LongMath.factorial(z) + ".");
        HashFunction hashFunction = Hashing.crc32();
//        .putString("我能吞下玻璃而不傷身體。", Charsets.UTF_8)
        byte[] someBytes=new byte[rand.nextInt(9991)+10];
        rand.nextBytes(someBytes);
        HashCode hashCode = hashFunction.newHasher()
                .putBytes(someBytes)
                .putInt(2017)
                .hash();
        textView.append("\nRandom string: "+hashCode.toString().toUpperCase());
        Log.wtf(Integer.toString(someBytes.length)+" bytes", "!");
        Log.wtf("Java Runtime Vesion", System.getProperty("java.runtime.version"));
        realDialog.show();
        playSong(R.raw.rodena, true);
        linearLayout.setBackground(getDrawable(R.drawable.side_nav_bar));

    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.stop();
        mediaPlayer.release();
    }
}
