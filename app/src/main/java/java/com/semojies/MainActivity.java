package java.com.semojies;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import developer.semojis.Helper.EmojiconEditText;
import developer.semojis.actions.EmojIconActions;

public class MainActivity extends AppCompatActivity {
    EmojIconActions emojIconActions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        emojIconActions = new EmojIconActions(getApplicationContext(), getWindow().getDecorView().findViewById(android.R.id.content), (EmojiconEditText) findViewById(R.id.edt), (ImageView) findViewById(R.id.btn));
        emojIconActions.addEmojiconEditTextList((EmojiconEditText) findViewById(R.id.edt));
        emojIconActions.ShowEmojIcon();
        ((EmojiconEditText) findViewById(R.id.edt)).mediaSelectionFromKeyboard = new EmojiconEditText.MediaSelectionFromKeyboard() {
            @Override
            public void onMediaSelect(Uri uri) {
                Log.e("onMediaSelect", "onMediaSelect: "+uri+", "+uri.getPath()+", " );
            }
        };

    }
}