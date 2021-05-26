package com.controll_rdv.rdv01;

import android.annotation.SuppressLint;
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
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import static android.widget.Toast.LENGTH_LONG;

@SuppressWarnings("SpellCheckingInspection")
public class Imagem extends AppCompatActivity {

    Button GetImageFromGalleryButton, UploadImageOnServerButton;

    ImageView ShowSelectedImage;

    EditText user, desp, auxDesp;

    Bitmap FixBitmap;

    String ImageTag = "image_tag";
    String ImageData = "image_data";
    String ConvertImage;
    String GetImageNameFromEditText;

    ProgressDialog progressDialog;

    ByteArrayOutputStream byteArrayOutputStream;

    byte[] byteArray;

    HttpURLConnection httpURLConnection;

    URL url;

    OutputStream outputStream;

    BufferedWriter bufferedWriter;

    int RC;

    BufferedReader bufferedReader;

    StringBuilder stringBuilder;

    boolean check = true;

    String currentPhotoPath, auxRand, currentDateTimeString;

    String equipe, gerencia, direcao;

    TextView valorD;
    TextView valorC;
    TextView obs;
    TextView data;

    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagem);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        /*declarações de variaveis */

        user = findViewById(R.id.usuario);
        desp = findViewById(R.id.desp);
        auxDesp = findViewById(R.id.despEmString);
        valorD = findViewById(R.id.pegaValorDesp);
        valorC = findViewById(R.id.pegaValorComb);
        obs = findViewById(R.id.pegaObs);
        data = findViewById(R.id.pegaData);

        GetImageFromGalleryButton = findViewById(R.id.buttonSelect);

        UploadImageOnServerButton = findViewById(R.id.buttonUpload);

        ShowSelectedImage = findViewById(R.id.imageView);

        byteArrayOutputStream = new ByteArrayOutputStream();

        /*Pegando nome do usuario digitado na tela de login*/

        pegarDados();

        GetImageFromGalleryButton.setOnClickListener(view -> takePhotoFromCamera());
        //Toast.makeText(this, "passou pela camera", Toast.LENGTH_SHORT).show();

        currentDateTimeString = java.text.DateFormat.getDateInstance().format(new Date());

        getRandom();

        UploadImageOnServerButton.setOnClickListener(view -> {
            GetImageNameFromEditText = auxRand + currentDateTimeString;
            setPic();
            if (FixBitmap != null) {

                UploadImageToServer();
            } else {
                Toast.makeText(this, "Erro ao encontrar imagem!", Toast.LENGTH_SHORT).show();

            }
        });

        getPermissoes();

    }

    @Override
    public void onBackPressed() {
    }

    private void getPermissoes() {
        if (ContextCompat.checkSelfPermission(Imagem.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                        5);
            }
        }
    }

    private void getRandom() {
        Random r = new Random();
        float i1 = (float) (r.nextInt(1000 - 1));
        auxRand = Float.toString(i1);
    }

    @SuppressLint({"SetTextI18n", "ShowToast"})
    private void pegarDados() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle params = intent.getExtras();
            if (params != null) {
                String login = params.getString("User");
                user.setText(login);
                String strdesp = params.getString("Desp");
                desp.setText(strdesp);
                //aqui pega o valor que veio de Movimento e transforma em String de acordo com os valores para melhor visualizacao
                if (desp.getText().toString().equals("1")) {
                    auxDesp.setText("Alimentação");
                } else if (desp.getText().toString().equals("2")) {
                    auxDesp.setText("Combustivel");
                } else if (desp.getText().toString().equals("3")) {
                    auxDesp.setText("Estacionamento");
                } else if (desp.getText().toString().equals("4")) {
                    auxDesp.setText("Hospedagem");
                } else if (desp.getText().toString().equals("5")) {
                    auxDesp.setText("Outros");
                } else if (desp.getText().toString().equals("6")) {
                    auxDesp.setText("Pedagio");
                }

                String valorDesp = params.getString("ValorDesp");
                valorD.setText(valorDesp);
                String valorComb = params.getString("ValorComb");
                valorC.setText(valorComb);
                String sobs = params.getString("Obs");
                obs.setText(sobs);
                String sdata = params.getString("Data");
                data.setText(sdata);
                gerencia = params.getString("gerencia");
                equipe = params.getString("equipe");
                direcao = params.getString("direcao");

            }
        }
    }

    public void OnVoltar(View view) {
        Intent v = new Intent(Imagem.this, Movimento.class);
        v.putExtra("UserVolta", user.getText().toString());
        v.putExtra("gerencia", gerencia);
        v.putExtra("equipe", equipe);
        v.putExtra("direcao", direcao);
        finish();
        startActivity(v);
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void takePhotoFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // tendo certeza de que existe uma camera
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // criando o arquivo para onde a peça deve ir
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // caso ocorra erro na criação do arquivo
            }
            // continua apenas se o arquivo foi criado
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.controll_rdv.rdv01",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                int CAMERA = 0;
                startActivityForResult(takePictureIntent, CAMERA);
            }
        }
    }

    private void galleryAddPic() {
        // pegando a imagem e salvando
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private File createImageFile() throws IOException {
        // criando uma imagem com o nome do arquivo
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefixo */
                ".jpg",   /* sufixo */
                storageDir     /* diretório */
        );

        // Salva o arquivo: caminho para para usar com intent ACTION_VIEW
        currentPhotoPath = image.getAbsolutePath();
        galleryAddPic();
        return image;
    }

    private void setPic() {
        // pega as dimensoes do imageview
        int targetW = ShowSelectedImage.getWidth();
        int targetH = ShowSelectedImage.getHeight();

        // pega as dimensoes do bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determinando qual a proporção escalar da imagem
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode do arquivo da imagem em um bitmap do tamanho do imageview
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        FixBitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        ShowSelectedImage.setImageBitmap(FixBitmap);
    }

    public void UploadImageToServer() {
        // compress da imagem para o upload

        FixBitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        byteArray = byteArrayOutputStream.toByteArray();
        ConvertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);


        @SuppressLint("StaticFieldLeak")
        class AsyncTaskUploadClass extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();
                progressDialog = ProgressDialog.show(Imagem.this, "A imagem está sendo enviada...", "Por favor, aguarde...", false, false);

            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                progressDialog.dismiss();

                Toast.makeText(Imagem.this, string1, Toast.LENGTH_SHORT).show();

                UploadImageOnServerButton.setVisibility(View.INVISIBLE);
                UploadImageOnServerButton.setEnabled(false);
                GetImageFromGalleryButton.setVisibility((View.INVISIBLE));
                GetImageFromGalleryButton.setEnabled(false);
                FixBitmap.recycle();
                // caso não , deixa a activity e habilita um botão
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            protected String doInBackground(Void... params) {

                String Cd_user = "cd_user";
                String Direcao = "direcao";
                String Gerencia = "gerencia";
                String Equipe = "equipe";
                String IdDesp = "id_desp";
                String ValorDesp = "valor_desp";
                String Km = "km";
                String Obs = "obs";
                String Data = "dataM";
                String Status = "statusM";
                String ObsRep = "obsRep";

                String cd_user = user.getText().toString();

                String id_desp = desp.getText().toString();

                String valor_desp = valorD.getText().toString();

                String km = valorC.getText().toString();

                String sobs = obs.getText().toString();

                String dataM = data.getText().toString();

                String strDir = direcao;

                String strEquipe = equipe;

                String strGerenc = gerencia;

                ImageProcessClass imageProcessClass = new ImageProcessClass();

                HashMap<String, String> HashMapParams = new HashMap<>();

                HashMapParams.put(ImageTag, GetImageNameFromEditText);

                HashMapParams.put(ImageData, ConvertImage);

                HashMapParams.put(Cd_user, cd_user);

                HashMapParams.put(Direcao, strDir);

                HashMapParams.put(Gerencia, strGerenc);

                HashMapParams.put(Equipe, strEquipe);

                HashMapParams.put(IdDesp, id_desp);

                HashMapParams.put(ValorDesp, valor_desp);

                HashMapParams.put(Km, km);

                HashMapParams.put(Obs, sobs);

                HashMapParams.put(Data, dataM);

                HashMapParams.put(Status, "indefinido");

                HashMapParams.put(ObsRep, "");

                System.out.println("Initial Mappings are: " + HashMapParams);

                //return imageProcessClass.ImageHttpRequest("http://192.168.0.105/rdv/uploadExample/upload-image-to-server.php", HashMapParams);
                return imageProcessClass.ImageHttpRequest(getString(R.string.stringImagem), HashMapParams);

            }

        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
        AsyncTaskUploadClassOBJ.execute();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 5) if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        } else Toast.makeText(Imagem.this, "Não foi possível abrir a camera", LENGTH_LONG).show();
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

                bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

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