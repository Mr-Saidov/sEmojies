package developer.semojis.Helper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import developer.semojis.R;
import developer.semojis.emoji.Emojicon;

class EmojiAdapter extends ArrayAdapter<Emojicon> {
    private EmojiconGridView.OnEmojiconClickedListener emojiClickListener;


    EmojiAdapter(Context context, List<Emojicon> data) {
        super(context, R.layout.emojicon_item, data);
    }

    EmojiAdapter(Context context, Emojicon[] data) {
        super(context, R.layout.emojicon_item, data);
    }


    void setEmojiClickListener(EmojiconGridView.OnEmojiconClickedListener listener) {
        this.emojiClickListener = listener;
    }

    @NonNull
    @Override
    public View getView(final int position, final View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = View.inflate(getContext(), R.layout.emojicon_item, null);
            ViewHolder holder = new ViewHolder();
            holder.icon = v.findViewById(R.id.emojicon_icon);
            holder.iv = v.findViewById(R.id.emojicon_icon_iv);

            v.setTag(holder);
        }

        Emojicon emoji = getItem(position);
        final ViewHolder holder = (ViewHolder) v.getTag();
        assert emoji != null;


        SpannableStringBuilder builder = new SpannableStringBuilder(emoji.getEmoji());
        int emoji1 = EmojiconHandler.addEmojis(getContext(), builder, 0, builder.length(), false);
        if (emoji1 == 0) {
            Log.e("if (emoji1 == 0) { ", "if (emoji1 == 0) { " + builder);
            holder.iv.setImageResource(0);
            holder.icon.setText(emoji.getEmoji());
        } else
            Glide.with(holder.iv).load(emoji1).override(64).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    ViewHolder holder = (ViewHolder) convertView.getTag();
                    Emojicon emoji = getItem(position);
                    Log.e("onLoadFailed ", "onLoadFailed " + emoji.getEmoji());
                    holder.iv.setImageResource(0);
                    holder.icon.setText(emoji.getEmoji());
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    holder.icon.setText("");
                    holder.iv.setImageDrawable(resource);
                    return false;
                }
            }).into(holder.iv);
        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emojiClickListener.onEmojiconClicked(getItem(position));
            }
        });

        return v;
    }

    class ViewHolder {
        EmojiconTextView icon;
        ImageView iv;
    }
}