package com.example.rdv01;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class Imagem extends AppCompatActivity {

    Button GetImageFromGalleryButton, UploadImageOnServerButton;

    ImageView ShowSelectedImage;

    TextView user, desp;

    EditText imageName;

    Bitmap FixBitmap;

    String ImageTag =   "image_tag";

    String ImageName = "image_data";

    ProgressDialog progressDialog;

    ByteArrayOutputStream byteArrayOutputStream;

    byte[] byteArray;

    String ConvertImage;

    String GetImageNameFromEditText;

    HttpURLConnection httpURLConnection;

    URL url;

    OutputStream outputStream;

    BufferedWriter bufferedWriter;

    int RC;

    BufferedReader bufferedReader;

    StringBuilder stringBuilder;

    boolean check = true;

    private final int GALLERY = 1;
    private final int CAMERA = 2;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagem);

        user = findViewById(R.id.usuario);
        desp = findViewById(R.id.desp);

        GetImageFromGalleryButton = findViewById(R.id.buttonSelect);

        UploadImageOnServerButton = findViewById(R.id.buttonUpload);

        ShowSelectedImage = findViewById(R.id.imageView);

        imageName = findViewById(R.id.imageName);

        byteArrayOutputStream = new ByteArrayOutputStream();

        /* Pegando nome do usuario digitado na tela de login */
        Intent intent = getIntent();
        if (intent != null) {
            Bundle params = intent.getExtras();
            if (params != null) {
                String login = params.getString("User");
                user.setText(login);
                String despesa = params.getString("Desp");
                desp.setText(despesa);

            }
        }

        GetImageFromGalleryButton.setOnClickListener(view -> showPictureDialog());

        UploadImageOnServerButton.setOnClickListener(view -> {

            GetImageNameFromEditText = imageName.getText().toString();

            UploadImageToServer();

        });

    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Selecione uma imagem da galeria clicando logo abaixo:");
        String[] pictureDialogItems = { "Galeria de fotos" };
        pictureDialog.setItems(pictureDialogItems, (dialog, which) -> {
            if (which == 0) {
                choosePhotoFromGallary();
            }
        });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    FixBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    Log.e("Gallery dimensions", FixBitmap.getWidth() + " " + FixBitmap.getHeight());

                    ShowSelectedImage.setImageBitmap(FixBitmap);
                    UploadImageOnServerButton.setVisibility(View.VISIBLE);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(Imagem.this, "Erro!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            FixBitmap = (Bitmap) data.getExtras().get("data");
            ShowSelectedImage.setImageBitmap(FixBitmap);
            Log.e("Camera dimensions", FixBitmap.getWidth() + " " + FixBitmap.getHeight());

            UploadImageOnServerButton.setVisibility(View.VISIBLE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public void UploadImageToServer() {

        FixBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        Log.e("Compressed dimensions", FixBitmap.getWidth() + " " + FixBitmap.getHeight());

        byteArray = byteArrayOutputStream.toByteArray();

        ConvertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
        if (imageName.getText().toString().equals("")) {
            Toast.makeText(Imagem.this, "Coloque um nome para a imagem!", Toast.LENGTH_SHORT).show();
            imageName.getFocusable();
        } else if (user.getText().toString().equals("") || desp.getText().toString().equals("")) {
            Toast.makeText(Imagem.this, "Não é possível enviar imagem sem estar logado!", Toast.LENGTH_SHORT).show();
            Toast.makeText(Imagem.this, "Voltando para logar...", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> {
                Intent i = new Intent(this, Movimento.class);
                startActivity(i);
            }, 3500);

        } else {
            @SuppressLint("StaticFieldLeak")
            class AsyncTaskUploadClass extends AsyncTask<Void, Void, String> {

                @Override
                protected void onPreExecute() {

                    super.onPreExecute();

                    progressDialog = ProgressDialog.show(Imagem.this, "A imagem está sendo enviada...",
                            "Por favor, aguarde...", false, false);
                }

                @Override
                protected void onPostExecute(String string1) {

                    super.onPostExecute(string1);

                    progressDialog.dismiss();

                    Toast.makeText(Imagem.this, string1, Toast.LENGTH_SHORT).show();

                }

                @SuppressLint("NewApi")
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                protected String doInBackground(Void... params) {

                    String usr = user.getText().toString();
                    String ImageUser = "image_user";
                    String dsp = desp.getText().toString();
                    String ImageDesp = "image_desp";

                    ImageProcessClass imageProcessClass = new ImageProcessClass();

                    HashMap<String, String> HashMapParams = new HashMap<>();

                    HashMapParams.put(ImageTag, GetImageNameFromEditText);

                    HashMapParams.put(ImageName, ConvertImage);

                    HashMapParams.put(ImageUser, usr);

                    HashMapParams.put(ImageDesp, dsp);

                    System.out.println("Initial Mappings are: " + HashMapParams);

                    return imageProcessClass.ImageHttpRequest(
                            "http://189.1.174.107:8080/app/upload/upload-image-to-server.php", HashMapParams);

                }
            }
            new Handler().postDelayed(() -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Voltar para a página anterior?");

                builder.setNegativeButton("Não", (dialog, id) -> {
                    dialog.cancel();
                    imageName.getText().clear();
                });

                builder.setPositiveButton("Sim", (dialog, id) -> {
                    Toast.makeText(Imagem.this, "Voltando para a página anterior...", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(() -> {
                        Intent i = new Intent(this, Movimento.class);
                        startActivity(i);
                    }, 2000);
                });
                builder.create().show();
            }, 2000);

            AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
            AsyncTaskUploadClassOBJ.execute();
        }

    }

    public class ImageProcessClass {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public String ImageHttpRequest(String requestURL, HashMap<String, String> PData) {

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

                    while ((RC2 = bufferedReader.readLine()) != null) {

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

}
