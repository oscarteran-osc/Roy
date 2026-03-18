package com.example.roy.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Helper para subir imágenes a ImgBB y obtener URLs
 *
 * INSTRUCCIONES:
 * 1. Obtén tu API Key gratis en: https://api.imgbb.com/
 * 2. Reemplaza "TU_API_KEY_AQUI" con tu clave real
 */
public class ImageUploadHelper {

    private static final String TAG = "ImageUploadHelper";

    // ⚠️ IMPORTANTE: Reemplaza esto con tu API Key de ImgBB
    private static final String IMGBB_API_KEY = "741d397f5c5af20f5309096f44726499";
    private static final String IMGBB_UPLOAD_URL = "https://api.imgbb.com/1/upload";

    private final OkHttpClient client;
    private final Context context;

    public ImageUploadHelper(Context context) {
        this.context = context;
        this.client = new OkHttpClient();
    }

    /**
     * Sube una imagen desde URI y devuelve la URL mediante callback
     */
    public void uploadImage(Uri imageUri, final OnUploadListener listener) {
        new Thread(() -> {
            try {
                // Convertir URI a Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                        context.getContentResolver(), imageUri);

                // Redimensionar para optimizar
                Bitmap resized = redimensionarBitmap(bitmap, 1024, 1024);

                // Convertir a Base64
                String base64 = bitmapToBase64(resized);

                // Subir a ImgBB
                uploadToImgBB(base64, listener);

            } catch (IOException e) {
                Log.e(TAG, "Error al procesar imagen", e);
                listener.onError("Error al procesar la imagen: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Sube una imagen en Base64 y devuelve la URL mediante callback
     */
    public void uploadBase64Image(String base64Image, final OnUploadListener listener) {
        new Thread(() -> uploadToImgBB(base64Image, listener)).start();
    }

    private void uploadToImgBB(String base64Image, final OnUploadListener listener) {
        // Crear el body de la petición
        RequestBody formBody = new FormBody.Builder()
                .add("key", IMGBB_API_KEY)
                .add("image", base64Image)
                .build();

        // Crear la petición
        Request request = new Request.Builder()
                .url(IMGBB_UPLOAD_URL)
                .post(formBody)
                .build();

        // Ejecutar la petición
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Error de conexión con ImgBB", e);
                listener.onError("Error de conexión: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    listener.onError("Error del servidor: " + response.code());
                    return;
                }

                try {
                    String responseBody = response.body().string();
                    JSONObject json = new JSONObject(responseBody);

                    if (json.getBoolean("success")) {
                        JSONObject data = json.getJSONObject("data");
                        String imageUrl = data.getString("url");

                        Log.d(TAG, "Imagen subida exitosamente: " + imageUrl);
                        listener.onSuccess(imageUrl);
                    } else {
                        listener.onError("ImgBB devolvió error");
                    }

                } catch (Exception e) {
                    Log.e(TAG, "Error al parsear respuesta", e);
                    listener.onError("Error al procesar respuesta: " + e.getMessage());
                }
            }
        });
    }

    private Bitmap redimensionarBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float ratio = Math.min((float) maxWidth / width, (float) maxHeight / height);

        int newWidth = Math.round(width * ratio);
        int newHeight = Math.round(height * ratio);

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }

    /**
     * Interface para callbacks de subida
     */
    public interface OnUploadListener {
        void onSuccess(String imageUrl);
        void onError(String error);
    }
}