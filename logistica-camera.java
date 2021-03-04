package com.example.logistica;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

public class MainActivity extends AppCompatActivity {

    Button GetImageFromGalleryButton, UploadImageOnServerButton;

    ImageView ShowSelectedImage;

    TextView user, cpf;

    Bitmap FixBitmap;

    String ImageTag = "image_tag" ;

    String ImageData = "image_data" ;

    ProgressDialog progressDialog ;

    ByteArrayOutputStream byteArrayOutputStream ;

    byte[] byteArray ;

    String ConvertImage ;

    String GetImageNameFromEditText;

    HttpURLConnection httpURLConnection ;

    URL url;

    OutputStream outputStream;

    BufferedWriter bufferedWriter ;

    int RC ;

    BufferedReader bufferedReader ;

    StringBuilder stringBuilder;

    boolean check = true;

    TextView tv;

    private String currentPhotoPath;

    Button outraimagem;

    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        user = findViewById(R.id.usuario);
        cpf = findViewById(R.id.et_cpf);
        tv = findViewById(R.id.textview);

        outraimagem = findViewById(R.id.btnOutraImagem);

        GetImageFromGalleryButton = findViewById(R.id.buttonSelect);

        UploadImageOnServerButton = findViewById(R.id.buttonUpload);

        ShowSelectedImage = findViewById(R.id.imageView);

        byteArrayOutputStream = new ByteArrayOutputStream();

        /*Pegando nome do usuario digitado na tela de login*/

        pegarUsuario();

        outraimagem.setOnClickListener(view -> enviarOutraImagem());

        GetImageFromGalleryButton.setOnClickListener(view -> showPictureDialog());

        String currentDateTimeString = java.text.DateFormat.getDateInstance().format(new Date());

        UploadImageOnServerButton.setOnClickListener(view -> {
            tv.setText(cpf.getText().toString() + " - " + currentDateTimeString);
            GetImageNameFromEditText = tv.getText().toString();
            setPic();
            UploadImageToServer();

        });
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                        5);
            }
        }

    }

    private void pegarUsuario() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle params = intent.getExtras();
            if (params != null) {
                String login = params.getString("loginUser");
                user.setText(login);
            }
        }
    }

    private void enviarOutraImagem() {
        AlertDialog alerta;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Preparando para enviar outra imagem...");
        alerta = builder.create();
        alerta.show();
        new Handler().postDelayed(() -> {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
            }, 3000);

    }

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Clique abaixo para abrir a camera: ");
        String[] pictureDialogItems = {
                "Camera" };
        pictureDialog.setItems(pictureDialogItems,
                (dialog, which) -> {
                    if (which == 0) {
                        takePhotoFromCamera();
                    }
                });
        pictureDialog.show();
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void takePhotoFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.logistica",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                int CAMERA = 0;
                startActivityForResult(takePictureIntent, CAMERA);
            }
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        galleryAddPic();
        return image;
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = ShowSelectedImage.getWidth();
        int targetH = ShowSelectedImage.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        FixBitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        ShowSelectedImage.setImageBitmap(FixBitmap);
    }

    public void UploadImageToServer(){

        FixBitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byteArray = byteArrayOutputStream.toByteArray();
        ConvertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
        final AlertDialog[] alerta = new AlertDialog[1];
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);


        @SuppressLint("StaticFieldLeak")
        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

                progressDialog = ProgressDialog.show(MainActivity.this,"A imagem está sendo enviada...","Por favor, aguarde...",false,false);
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                progressDialog.dismiss();

                makeText(MainActivity.this,string1, LENGTH_SHORT).show();
                builder.setMessage("Deseja enviar outra imagem?");
                builder.setPositiveButton("Sim", (dialog, which) -> {
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                });
                builder.setNegativeButton("Não", (dialog, which) ->
                        makeText(MainActivity.this,"Clique no botão acima para enviar " +
                                "outra imagem a qualquer momento!", LENGTH_SHORT).show());
                alerta[0] = builder.create();
                alerta[0].show();
                outraimagem.setVisibility(View.VISIBLE);

            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            protected String doInBackground(Void... params) {

                String usr = user.getText().toString();
                String ImageUser = "image_user";
                String cpf_cliente = cpf.getText().toString();
                String ImageCpf = "image_cpf";

                ImageProcessClass imageProcessClass = new ImageProcessClass();

                HashMap<String,String> HashMapParams = new HashMap<>();

                HashMapParams.put(ImageTag, GetImageNameFromEditText);

                HashMapParams.put(ImageData, ConvertImage);

                HashMapParams.put(ImageUser,usr);

                HashMapParams.put(ImageCpf,cpf_cliente);

                System.out.println("Initial Mappings are: " + HashMapParams);

                return imageProcessClass.ImageHttpRequest("http://192.168.0.105/rdv/uploadExample/upload-image-to-server.php", HashMapParams);
                //return imageProcessClass.ImageHttpRequest("http://189.1.174.107:8080/app/upload/upload-image-to-server.php", HashMapParams);
            }

        }

        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
        AsyncTaskUploadClassOBJ.execute();


    }

    public class ImageProcessClass{

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public String ImageHttpRequest(String requestURL, HashMap<String,String> PData) {

            StringBuilder stringBuilder = new StringBuilder();

            try {
                url = new URL(requestURL);

                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(20000);

                httpURLConnection.setConnectTimeout(20000);

                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoInput(true);

                httpURLConnection.setDoOutput(true);

                outputStream = httpURLConnection.getOutputStream();

                bufferedWriter = new BufferedWriter(

                        new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

                bufferedWriter.write(bufferedWriterDataFN(PData));

                bufferedWriter.flush();

                bufferedWriter.close();

                outputStream.close();

                RC = httpURLConnection.getResponseCode();

                if (RC == HttpsURLConnection.HTTP_OK) {

                    bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                    stringBuilder = new StringBuilder();

                    String RC2;

                    while ((RC2 = bufferedReader.readLine()) != null){

                        stringBuilder.append(RC2);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

            stringBuilder = new StringBuilder();

            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {
                if (check)
                    check = false;
                else
                    stringBuilder.append("&");

                stringBuilder.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));

                stringBuilder.append("=");

                stringBuilder.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }

            return stringBuilder.toString();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 5) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to use camera

            }
            else {

                makeText(MainActivity.this, "Não foi possível abrir a camera", LENGTH_LONG).show();

            }
        }
    }
}
