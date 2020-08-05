package developer.semojis.Helper;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.style.DynamicDrawableSpan;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

import developer.semojis.R;

public class EmojiconEditText extends AppCompatEditText {
    private static String TAG = "#EmojiconEditText ";
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
        @SuppressLint("CustomViewStyleable") TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.Emojicon);
        mEmojiconSize = (int) a.getDimension(R.styleable.Emojicon_emojiconSize, getTextSize());
        mEmojiconAlignment = a.getInt(R.styleable.Emojicon_emojiconAlignment, DynamicDrawableSpan.ALIGN_BASELINE);
        mUseSystemDefault = a.getBoolean(R.styleable.Emojicon_emojiconUseSystemDefault, false);
        a.recycle();
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
}
