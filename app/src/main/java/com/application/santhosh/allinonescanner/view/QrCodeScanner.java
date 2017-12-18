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

public class QrCodeScanner extends Fragment {
    //static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    TextView tvUrl, tvType;
    Button qrCode, goTo;

    public QrCodeScanner() {

    }

    //@Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_qr_code_scanner, container, false);
        qrCode = (Button) rootView.findViewById(R.id.qrCode);
        goTo = (Button) rootView.findViewById(R.id.goTo);
        tvUrl = (TextView) rootView.findViewById(R.id.contents);
        tvType = (TextView) rootView.findViewById(R.id.format);
        qrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    //start the scanning activity from the com.google.zxing.client.android.SCAN intent

                    Intent intent = new Intent(getContext(), CaptureActivity.class);
                    intent.setAction("com.google.zxing.client.android.SCAN");
                   // intent.setPackage("com.google.zxing.client.android");
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                    startActivityForResult(intent, 0);
                } catch (ActivityNotFoundException anfe) {
                    //on catch, show the download dialog
                    //showDialog(getContext(), "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
                }

            }
        });


        return rootView;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == 0) {

            if (resultCode == MainActivity.RESULT_OK) {

                //get the extras that are returned from the intent

                final String contents = intent.getStringExtra("SCAN_RESULT");

                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

                tvUrl.setText(contents);
                tvUrl.setTextSize(20);
                tvType.setText(format);
                tvType.setTextSize(20);
                goTo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String contents1;
                        if(contents.contains("https://")) {
                            contents1=contents;
                        }else{
                            StringBuilder sb = new StringBuilder(contents);
                            sb.insert(0,"https://");
                            contents1 = sb.toString();
                        }
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(contents1));
                        startActivity(i);
                    }
                });


            }
        }
    }
}
