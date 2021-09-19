package com.ipid.demo;

import static com.ipid.demo.constants.Constants.FIRST_BANNER_SUB_TITLE;
import static com.ipid.demo.constants.Constants.FIRST_BANNER_TITLE;
import static com.ipid.demo.constants.Constants.FOURTH_BANNER_SUB_TITLE;
import static com.ipid.demo.constants.Constants.FOURTH_BANNER_TITLE;
import static com.ipid.demo.constants.Constants.HTML_PERIOD;
import static com.ipid.demo.constants.Constants.SECOND_BANNER_SUB_TITLE;
import static com.ipid.demo.constants.Constants.SECOND_BANNER_TITLE;
import static com.ipid.demo.constants.Constants.THIRD_BANNER_SUB_TITLE;
import static com.ipid.demo.constants.Constants.THIRD_BANNER_TITLE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ipid.demo.adapters.SliderAdapter;
import com.ipid.demo.db.AppDatabase;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    private LinearLayout dotsLayout;
    private SliderAdapter adapter;
    private ViewPager2 pager2;
    private int list[];
    private TextView[] dots;
    private TextView title;
    private TextView content;
    private int currentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        AppDatabase.getDbInstance(this.getApplicationContext());

        dotsLayout = findViewById(R.id.dots_container);
        pager2 = findViewById(R.id.view_pager2);
        title = findViewById(R.id.textViewTitle);
        content = findViewById(R.id.textViewContent);

        list = new int[4];
        list[0] = R.mipmap.banner_item_1;
        list[1] = R.mipmap.banner_item_2;
        list[2] = R.mipmap.banner_item_3;
        list[3] = R.mipmap.banner_item_4;

        adapter = new SliderAdapter(getApplicationContext(), list);
        pager2.setAdapter(adapter);

        // auto scroll in 3 secs
        Handler handler = new Handler();

        //The second parameter ensures smooth scrolling
        Runnable update = () -> {
            if (currentPage == list.length) {
                currentPage = 0;
            }

            //The second parameter ensures smooth scrolling
            pager2.setCurrentItem(currentPage++, true);
        };

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        }, 3000, 3000);

        dots = new TextView[4];
        dotsIndicator();

        pager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                selectedIndicator(position);
                selectedText(position);
                super.onPageSelected(position);
            }
        });
    }

    private void selectedIndicator(int position) {
        for (int i = 0; i < dots.length; i++) {
            if (i == position) {
                dots[i].setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.font_color));
            } else {
                dots[i].setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.font_color_200));
            }
        }
    }

    private void dotsIndicator() {
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml(HTML_PERIOD));
            dots[i].setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
            dots[i].setTextSize(9);
            dotsLayout.addView(dots[i]);
        }
    }

    private void selectedText(int position) {
        switch(position) {
            case 0:
                title.setText(FIRST_BANNER_TITLE);
                content.setText(FIRST_BANNER_SUB_TITLE);
                break;
            case 1:
                title.setText(SECOND_BANNER_TITLE);
                content.setText(SECOND_BANNER_SUB_TITLE);
                break;
            case 2:
                title.setText(THIRD_BANNER_TITLE);
                content.setText(THIRD_BANNER_SUB_TITLE);
                break;
            case 3:
                title.setText(FOURTH_BANNER_TITLE);
                content.setText(FOURTH_BANNER_SUB_TITLE);
                break;
            default:
                throw new IllegalStateException("Illegal page view position.");
        }
    }

    public void onClickRegister(View view) {
        startActivity(new Intent(this, RegistrationActivity.class));
    }

    public void onClickLogin(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }
}