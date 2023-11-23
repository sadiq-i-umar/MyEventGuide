package com.example.myeventguide;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class EventPageActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private Button registerButton;

    private Dialog confirmRegDialogue, successRegDialogue;

    private TextView nameInQRCode;

    private ImageView imageViewForTag;

    private Bitmap nameBitmap, qrBitmap, tagBitmap, bitmapToSave;

    private RelativeLayout tagRelLayout;

    int nameViewHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        registerButton = findViewById(R.id.buttonRegisterEvent);
        confirmRegDialogue = new Dialog(EventPageActivity.this, android.R.style.Theme_Light_NoTitleBar);
        successRegDialogue = new Dialog(EventPageActivity.this, android.R.style.Theme_Light_NoTitleBar);
        nameInQRCode = findViewById(R.id.tvNameInQRCode);

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
                confirmRegDialogue.findViewById(R.id.layoutDialogueCancellableArea).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmRegDialogue.cancel();
                    }
                });
                confirmRegDialogue.findViewById(R.id.dialogueCard).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Leave Empty. This onClickListener was defined to prevent closing the dialogue when the CardView or its child elements are touched
                        //Because of this onClickListener, only the area outside the CardView closes the dialogue when clicked
                    }
                });
            }
        });
    }

    public void confirmRegistration(View view) {
        confirmRegDialogue.cancel();
        successRegDialogue.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
        successRegDialogue.setContentView(R.layout.layout_successful_registration_message);
        successRegDialogue.setCancelable(true);
        successRegDialogue.setCanceledOnTouchOutside(true);
        successRegDialogue.show();
        imageViewForTag = successRegDialogue.findViewById(R.id.imageViewForTag);
        nameBitmap = getBitmapFromView(successRegDialogue.findViewById(R.id.tagRelLayout), 600);
        //nameBitmap = getBitmapFromView(nameInQRCode, 700);
        qrBitmap = generateBitMap("umarsadiqibrahim@gmail.com");
        //tagBitmap = overlay(nameBitmap, qrBitmap, nameInQRCode.getText().length(), nameInQRCode.getHeight());
        imageViewForTag.setImageBitmap(qrBitmap);
        Toast.makeText(EventPageActivity.this, "Registration Confirmed", Toast.LENGTH_SHORT).show();
        successRegDialogue.findViewById(R.id.saveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmapToSave = getBitmapFromImageView(successRegDialogue.findViewById(R.id.tagRelLayout), 1000);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
                    Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                    i.addCategory(Intent.CATEGORY_DEFAULT);
                    startActivityForResult(Intent.createChooser(i, "Choose directory"), 9999);
                }
            }
        });
        successRegDialogue.findViewById(R.id.layoutDialogueCancellableArea).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                successRegDialogue.cancel();
            }
        });
        successRegDialogue.findViewById(R.id.dialogueCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Leave Empty. This onClickListener was defined to prevent closing the dialogue when the CardView or its child elements are touched
                //Because of this onClickListener, only the area outside the CardView closes the dialogue when clicked
            }
        });
    }

    public void backToEventPage(View view) {
        successRegDialogue.cancel();
    }

    public Bitmap getBitmapFromView(View view, int maxWidth) {
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(maxWidth, View.MeasureSpec.AT_MOST);
        view.measure(widthMeasureSpec, 0); // Measure with the specified maximum width
        int measuredWidth = view.getMeasuredWidth();
        nameViewHeight = view.getMeasuredHeight();
        if (nameViewHeight > 311) {
            nameInQRCode.setTextSize(17);
        }

        // Create a Bitmap with the measured dimensions
        Bitmap bitmap = Bitmap.createBitmap(measuredWidth, view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        // Layout the view within the measured dimensions
        view.layout(0, 0, measuredWidth, view.getMeasuredHeight());

        // Draw the view onto the Bitmap
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.TRANSPARENT);
        view.draw(canvas);

        return bitmap;
    }

    public Bitmap getBitmapFromImageView(View view, int maxWidth) {
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(maxWidth, View.MeasureSpec.AT_MOST);
        view.measure(widthMeasureSpec, 0); // Measure with the specified maximum width
        int measuredWidth = view.getMeasuredWidth();
        nameViewHeight = view.getMeasuredHeight();
        if (nameViewHeight > 311) {
            nameInQRCode.setTextSize(17);
        }

        // Create a Bitmap with the measured dimensions
        Bitmap bitmap = Bitmap.createBitmap(measuredWidth, view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        // Layout the view within the measured dimensions
        view.layout(14, 35, measuredWidth, view.getMeasuredHeight());

        // Draw the view onto the Bitmap
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        view.draw(canvas);

        return bitmap;
    }

    public Bitmap generateBitMap(String text) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 400, 400);

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            return bitmap;

        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }

    public Bitmap overlay(Bitmap nameBitmap, Bitmap qrBitmap, int namelength, int nameViewHeight) {
        Bitmap bmOverlay = Bitmap.createBitmap(800, 800, nameBitmap.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        //The condition blocks below allow for positioning the text on the canvas depending on the number of characters it has
        if (nameViewHeight < 103) {
            canvas.drawBitmap(qrBitmap, 200, 250, null); // Refers to the qr code
            canvas.drawBitmap(nameBitmap, 400 - 23 * (namelength), 100, null); // Refers to the name
        } else if (nameViewHeight < 208) {
            canvas.drawBitmap(qrBitmap, 200, 250, null); // Refers to the qr code
            canvas.drawBitmap(nameBitmap, 60, 70, null); // Refers to the name
        } else if (nameViewHeight < 313) {
            canvas.drawBitmap(qrBitmap, 200, 330, null); // Refers to the qr code
            canvas.drawBitmap(nameBitmap, 60, 70, null); // Refers to the name
        }
        nameBitmap.recycle();
        qrBitmap.recycle();
        return bmOverlay;
    }

    public boolean saveBitmapToStorage(Bitmap bitmap, File directory, String filename) {

        // Create a file object with the specified filename and the directory
        File file = new File(directory, filename);

        try {
            // Create a FileOutputStream to write the Bitmap to the file
            FileOutputStream outputStream = new FileOutputStream(file);

            // Save the Bitmap as a PNG image (you can change the format to JPEG if needed)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

            // Close the FileOutputStream
            outputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case 9999:
                //Viewing the uri
                Log.i("Test", "Result URI " + data.getData());

                //Editing the uri string value to take the format of a directory path
                String newuri = data.getData().toString().substring(63);
                newuri = newuri.replace("%2F", "/").replace("%20"," ");

                String folderPath = "/storage/emulated/0/" + newuri; // Replace with the actual folder path
                File directory = new File(folderPath);
                String filename = "sadiqibrahimumar" + ".png";
                // Make sure the directory exists
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                if (saveBitmapToStorage(bitmapToSave, directory, filename)) {
                    Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Error Saving to Storage", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}