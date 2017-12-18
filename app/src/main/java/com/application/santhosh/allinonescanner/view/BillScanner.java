package com.application.santhosh.allinonescanner.view;

import com.application.santhosh.allinonescanner.model.BillScannerBo;
import com.application.santhosh.allinonescanner.presentation.MainActivity;
import com.application.santhosh.allinonescanner.R;
import com.application.santhosh.allinonescanner.recycler.RecyclerAdapter;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BillScanner extends Fragment {
    final int CAMERA_CAPTURE = 1;
    final int PIC_CROP = 2;
    Button billScan;
    private Uri picUri;
    EditText title, message;
    String prefix1 = null;
    File file;
    BillScannerBo billScannnerBo;


    private RecyclerView rv;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    List<BillScannerBo> itemList;


    public BillScanner() {

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_bill_scanner, container, false);

        billScan = (Button) rootView.findViewById(R.id.billScan);
        rv = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        new UpdateList().execute();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(layoutManager);
        rv.setHasFixedSize(true);
        rv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        rv.setAdapter(adapter);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        billScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                    startActivityForResult(captureIntent, CAMERA_CAPTURE);
                } catch (ActivityNotFoundException anfe) {
                    String errorMessage = "Whoops - your device doesn't support capturing images!";
                    Toast toast = Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });


        super.onResume();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bp = null;
        if (resultCode == MainActivity.RESULT_OK) {

            if (requestCode == CAMERA_CAPTURE) {
                picUri = data.getData();
                new PerformCrop().execute();
            } else if (requestCode == PIC_CROP) {
                Bundle extra = data.getExtras();

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    bp = extra.getParcelable("data");
                } else {
                    Uri uri = data.getData();
                    try {
                        bp = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    File f = createNewFile(bp);
                } catch (IOException e) {
                    Log.e("Error Creating File: ", picUri.toString());
                }


            }
        }
    }


    private class PerformCrop extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String errorMsg = null;
            try {
                Intent cropIntent = new Intent("com.android.camera.action.CROP");
                cropIntent.setDataAndType(picUri, "image/*");
                cropIntent.putExtra("crop", true);
                cropIntent.putExtra("outputX", 1024);
                cropIntent.putExtra("outputY", 2048);
                cropIntent.putExtra("return-data", true);
                startActivityForResult(cropIntent, PIC_CROP);

            } catch (ActivityNotFoundException anfe) {
                errorMsg = "Your Device doesnot support the Crop action." + "\n";
            }
            return errorMsg;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null && !s.isEmpty()) {
                String msg = s + "Please make sure your device has Google Photos installed .";
                Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
            }

            super.onPostExecute(s);
        }
    }


    private File createNewFile(Bitmap pic) throws IOException {
        if (pic != null) {
            final Bitmap bitmap = pic;
            LayoutInflater factory = LayoutInflater.from(getContext());
            final View dialog = factory.inflate(R.layout.alert_dialog, null);
            final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
            alertDialog.setView(dialog);
            SimpleDateFormat sdf = new SimpleDateFormat("MMM:dd:yyyy_hh:mm:ss");
            final String currentDateandTime = sdf.format(new Date());
            final EditText storeName = (EditText) dialog.findViewById(R.id.storeName);
            EditText date = (EditText) dialog.findViewById(R.id.date);
            date.setText(currentDateandTime.substring(0,11));
            dialog.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int imageNum = 0;
                    prefix1 = storeName.getText().toString();
                    String fileName = prefix1 + currentDateandTime + ".jpg";
                    String dir = Environment.getExternalStorageDirectory() + "/Bills/";
                    File newDirectory = new File(dir);
                    if (!newDirectory.exists()) {
                        if (newDirectory.mkdir()) {
                            Log.d(getContext().getClass().getName(), newDirectory.getAbsolutePath() + "Directory created");
                        }
                    }

                    file = new File(newDirectory, fileName);
                    while (file.exists()) {
                        imageNum++;
                        fileName = "image_" + String.valueOf(imageNum) + ".jpg";
                        file = new File(newDirectory, fileName);
                    }
                    Uri uriSavedImage = Uri.fromFile(file);
                    try {
                        final FileOutputStream fos = new FileOutputStream(file);
                        final BufferedOutputStream bos = new BufferedOutputStream(fos, 8192);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                        bos.flush();
                        bos.close();
                        file.createNewFile();
                        new DeleteLastFromDCIM().execute();
                        new UpdateList().execute();


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            dialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DeleteLastFromDCIM().execute();
                    alertDialog.dismiss();
                }
            });

            alertDialog.show();

        }
        return file;
    }

    private class UpdateList extends AsyncTask<String, Void, String> {

        String reverse, datereverse;

        @Override
        protected String doInBackground(String... params) {
            String reply = null;
            File file1 = new File(Environment.getExternalStorageDirectory() + "/Bills");
            File[] list = file1.listFiles();
            if (list != null && list.length != 0) {
                itemList = new ArrayList<BillScannerBo>();
                for (int i = 0; i < list.length; i++) {
                    billScannnerBo = new BillScannerBo();
                    billScannnerBo.setImgpath(list[i].getAbsolutePath());
                    reverse = new StringBuffer(list[i].getName()).reverse().toString().trim();
                    String name = new StringBuffer(reverse.substring(24)).reverse().toString();
                    billScannnerBo.setName(name);
                    String date = new StringBuffer(reverse.substring(13, 24)).reverse().toString();
                    billScannnerBo.setDate(date);
                    itemList.add(billScannnerBo);
                }
            } else {
                String dir = Environment.getExternalStorageDirectory() + "/Bills/";
                File newDirectory = new File(dir);
                if (!newDirectory.exists()) {
                    if (newDirectory.mkdir()) {
                        Log.d(getContext().getClass().getName(), newDirectory.getAbsolutePath() + "Directory created");
                    }
                }
                reply = "New Folder will be created to save bills.";
            }
            return reply;
        }

        @Override
        protected void onPostExecute(String s) {
       //     Comparator cmp = Collections.reverseOrder(null);
         //   Collections.sort(itemList.,cmp);
            RecyclerAdapter adapter = new RecyclerAdapter(getContext(), itemList);
            rv.setAdapter(adapter);
            if (s != null && !s.isEmpty()) {
                Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(s);
        }
    }


    private class DeleteLastFromDCIM extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            boolean success = false;
            String dir = Environment.getExternalStorageDirectory() + "DCIM";
            File f = new File(dir);
            String path = Environment.getExternalStorageDirectory()
                    + File.separator + "DCIM" + File.separator;

            if (new File(path + "100MEDIA").exists()) {
                path = path + "100MEDIA";
            } else if (new File(path + "Camera").exists()) {
                path = path + "Camera";
            } else if (new File(path + "100ANDRO").exists()) {
                path = path + "100ANDRO";
            }
            try {
                File[] images = new File(path).listFiles();
                File latestSavedImage = images[0];
                for (int i = 1; i < images.length; ++i) {
                    if (images[i].lastModified() > latestSavedImage.lastModified()) {
                        latestSavedImage = images[i];
                    }
                }
               success = latestSavedImage.delete();
                success = new File(path + latestSavedImage.getAbsoluteFile()).delete();
                return "success";
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

}
