package com.example.ourpact3;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.ourpact3.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity
{
    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 101;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (!checkPermission())
        {
            this.requestPermission();
        }
        // Start Overlay Service
        // Set up the Toolbar as the ActionBar

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_rules,R.id.navigation_exceptions, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);


    }


    private void requestPermission()
    {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
//        {
//            ActivityCompat.requestPermissions(this, new String[]{
//                    Manifest.permission.MEDIA_CONTENT_CONTROL,
//            }, PERMISSION_REQUEST_CODE);
//        } else
//        {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.MEDIA_CONTENT_CONTROL}, PERMISSION_REQUEST_CODE);
//        }
    }

    private boolean checkPermission()
    {
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        {
            boolean mediaControlPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.MEDIA_CONTENT_CONTROL) == PackageManager.PERMISSION_GRANTED;
            return mediaControlPermission;
        } else
        {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.MEDIA_CONTENT_CONTROL) == PackageManager.PERMISSION_GRANTED;
        }*/
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        /*if (requestCode == PERMISSION_REQUEST_CODE)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "I have Media Permission", Toast.LENGTH_LONG).show();

                // Permissions granted, proceed with accessing media
            } else
            {
                // Permissions denied, inform the user
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
            }
        }*/
    }
}


