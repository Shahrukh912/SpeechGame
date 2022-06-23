package com.srk.speechgame;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private static final String TAG = "tts";
    private TextToSpeech tts;
    private final int MY_DATA_CHECK_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
                tts = new TextToSpeech(this, this);
            } else {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
        else if(requestCode == 10){
            if(resultCode == RESULT_OK && data != null){
                ArrayList<String> arrayList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String ans = arrayList.get(0);
                if(ans.equals("4") || ans.equals("for") || ans.equals("four")){
                    ttsSpeak("Correct","finish");
                }
                else{
                    ttsSpeak("Incorrect","finish");
                }

            }
        }

    }

    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS) {
            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onDone(String utteranceId) {
                    if(utteranceId.equals("Initial")){
                        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                        startActivityForResult(i,10);
                    }
                    //Log.d(TAG, "onDone: asd");
                }

                @Override
                public void onError(String utteranceId) {
                }

                @Override
                public void onStart(String utteranceId) {
                }
            });

            int result = tts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(MainActivity.this, "Language not supported", Toast.LENGTH_SHORT).show();
            } else {
                ttsSpeak("what is 2 multiply by 2","Initial");
            }
        } else {
            Toast.makeText(MainActivity.this, "Initialization Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void ttsSpeak(String s,String id){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(s, TextToSpeech.QUEUE_FLUSH, null, id);
        } else {
            tts.speak(s, TextToSpeech.QUEUE_FLUSH, null , id);
        }
    }
    @Override
    protected void onStop() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onStop();
    }
}