package com.samvel.matchinggame;

import android.content.Context;
import android.media.MediaPlayer;

public class Methods {

    public static void clickSound(Context context){
        final MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.click);
        mediaPlayer.start();
    }
}
