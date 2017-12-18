package com.application.santhosh.allinonescanner.view;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.application.santhosh.allinonescanner.presentation.MainActivity;
import com.application.santhosh.allinonescanner.R;
import com.google.zxing.client.android.CaptureActivity;

public class BarCodeScanner extends Fragment {
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";

    TextView content,format;
    String contents,formats;
    Button barCode,goTO;


    public BarCodeScanner() {

    }

    // @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_bar_code_scanner, container, false);
        barCode = (Button) rootView.findViewById(R.id.barCode);
        goTO = (Button) rootView.findViewById(R.id.goTo);
        content = (TextView) rootView.findViewById(R.id.contents);
        format = (TextView) rootView.findViewById(R.id.format);

        barCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //start the scanning activity from the com.google.zxing.client.android.SCAN intent
                    Intent intent = new Intent(getContext(), CaptureActivity.class);
                    intent.setAction("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
                    startActivityForResult(intent, 0);
                } catch (ActivityNotFoundException anfe) {

                    //on catch, show the download dialog

                    //showDialog(this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();

                }

            }
        });
        return rootView;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == 0) {

            if (resultCode == MainActivity.RESULT_OK) {

                //get the extras that are returned from the intent

                contents = intent.getStringExtra("SCAN_RESULT");

                formats = intent.getStringExtra("SCAN_RESULT_FORMAT");
                content.setText(contents);
                content.setTextSize(20);
                format.setText(formats);
                format.setTextSize(20);
                goTO.setVisibility(View.VISIBLE);

                goTO.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri google = Uri.parse("https://www.google.com/#newwindow=1&q="+contents);
                        Intent i= new Intent(Intent.ACTION_VIEW);
                        i.setData(google);
                        startActivity(i);
                    }
                });

            }
        }
    }



}
