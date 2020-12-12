package developer.semojis.Helper;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.style.DynamicDrawableSpan;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.os.BuildCompat;
import androidx.core.view.inputmethod.EditorInfoCompat;
import androidx.core.view.inputmethod.InputConnectionCompat;
import androidx.core.view.inputmethod.InputContentInfoCompat;

public class EmojiconEditText extends AppCompatEditText {
    private static String TAG = "#EmojiconEditText ";
    public MediaSelectionFromKeyboard mediaSelectionFromKeyboard;
    final InputConnectionCompat.OnCommitContentListener callback =
            new InputConnectionCompat.OnCommitContentListener() {
                @Override
                public boolean onCommitContent(InputContentInfoCompat inputContentInfo,
                                               int flags, Bundle opts) {
                    // read and display inputContentInfo asynchronously
                    if (BuildCompat.isAtLeastNMR1() && (flags &
                            InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION) != 0) {
                        try {
                            inputContentInfo.requestPermission();
                        } catch (Exception e) {
                            return false; // return false if failed
                        }
                        if (mediaSelectionFromKeyboard != null) {
                            mediaSelectionFromKeyboard.onMediaSelect(inputContentInfo.getContentUri());
                        }
                    }

                    // read and display inputContentInfo asynchronously.
                    // call inputContentInfo.releasePermission() as needed.

                    return true;  // return true if succeeded
                }

            };

    private int mEmojiconSize;
    private int mEmojiconAlignment;
    private int mEmojiconTextSize;
    private boolean mUseSystemDefault = false;

    public EmojiconEditText(Context context) {
        super(context);
        mEmojiconSize = (int) getTextSize();
        mEmojiconTextSize = (int) getTextSize();
    }

    public EmojiconEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public EmojiconEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        mEmojiconSize = dip(getContext(), 30);
        mEmojiconAlignment = DynamicDrawableSpan.ALIGN_BASELINE;
        mUseSystemDefault = false;
        mEmojiconTextSize = (int) getTextSize();
        setText(getText());
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        updateText();
    }

    /**
     * Set the size of emojicon in pixels.
     */
    public void setEmojiconSize(int pixels) {
        mEmojiconSize = pixels;

        updateText();
    }

    private void updateText() {
        EmojiconHandler.addEmojis(getContext(), getText(), mEmojiconSize, mEmojiconAlignment, mEmojiconTextSize, mUseSystemDefault);
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        String text = getText().toString();
        boolean b = super.onTextContextMenuItem(id);
        switch (id) {
            case android.R.id.cut:
            case android.R.id.copy:
                copyEncryptedTextToClipboard(text);
                break;
            case android.R.id.paste:
                pasteDecryptedText(text);
                break;
        }
        return b;
    }

    private void pasteDecryptedText(String text) {
        ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        String clipData = clipboardManager.getPrimaryClip().getItemAt(0).getText().toString();
        setText(text + Security.getInstance().getDecryptedText(getContext(), clipData));
        setSelection(getText().toString().length());
    }

    private void copyEncryptedTextToClipboard(String text) {
        text = text.trim();
        if (text.isEmpty()) return;
        ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        String encryptedText = Security.getInstance().getEncryptedText(getContext(), text);
        clipboardManager.setPrimaryClip(ClipData.newPlainText("label", encryptedText));
    }

    @Override
    public InputConnection onCreateInputConnection(final EditorInfo editorInfo) {
        final InputConnection ic = super.onCreateInputConnection(editorInfo);
        EditorInfoCompat.setContentMimeTypes(editorInfo,
                new String[]{"image/png", "image/gif"});
        return InputConnectionCompat.createWrapper(ic, editorInfo, callback);
    }

    public int dip(Context context, int value) {
        return (int) (value * context.getResources().getDisplayMetrics().density);
    }

    public float dp(Context context, int value) {
        return (value * context.getResources().getDisplayMetrics().density);
    }

    public interface MediaSelectionFromKeyboard {
        void onMediaSelect(Uri uri);
    }
}