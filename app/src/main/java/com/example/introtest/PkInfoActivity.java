package com.example.introtest;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class PkInfoActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_info);
        TextView tv_paking = (TextView) findViewById(R.id.parking_Lot);
        TextView tv_available = (TextView) findViewById(R.id.available_Lot);
        TextView tv_using = (TextView) findViewById(R.id.using_Lot);
        SpannableStringBuilder ssb1 = new SpannableStringBuilder(tv_paking.getText());
        ssb1.setSpan(new ForegroundColorSpan(Color.parseColor("#0EF116")), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_paking.setText(ssb1);

        SpannableStringBuilder ssb2 = new SpannableStringBuilder(tv_available.getText());
        ssb2.setSpan(new ForegroundColorSpan(Color.parseColor("#0027FF")), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_available.setText(ssb2);

        SpannableStringBuilder ssb3 = new SpannableStringBuilder(tv_using.getText());
        ssb3.setSpan(new ForegroundColorSpan(Color.parseColor("#FF0F00")), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_using.setText(ssb3);

        ImageButton btn_back = (ImageButton) findViewById(R.id.btn_map_back2);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
