package com.example.myeventguide;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

public class EventPageActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private Button registerButton;

    private Dialog confirmRegDialogue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        registerButton = findViewById(R.id.buttonRegisterEvent);
        confirmRegDialogue = new Dialog(EventPageActivity.this, android.R.style.Theme_Light_NoTitleBar);

        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPagerAdapter.addFragment(new EventDetailsFragment(), "Event Details");
        viewPagerAdapter.addFragment(new EventDescriptionFragment(), "Description");

        viewPager.setAdapter(viewPagerAdapter);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmRegDialogue.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
                confirmRegDialogue.setContentView(R.layout.layout_register_dialogue);
                confirmRegDialogue.setCancelable(true);
                confirmRegDialogue.setCanceledOnTouchOutside(true);
                confirmRegDialogue.show();
            }
        });
    }

    public void confirmRegistration(View view) {
        Toast.makeText(EventPageActivity.this, "Confirmed", Toast.LENGTH_SHORT).show();
        confirmRegDialogue.cancel();
    }
}