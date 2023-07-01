package org.eamonn.wfc;

import android.content.Context;
import android.os.Bundle;
import barsoosayque.libgdxoboe.OboeAudio;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidAudio;

// This is a giant embarrassment. But getting this to build as scala proves challenging.
// https://github.com/wireapp/gradle-android-scala-plugin seems to be a contemporaryish
// version of this plugin but it was released to sonatype which is now dead, so...

public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useAccelerometer = true;
        config.useCompass = true;
        initialize(new Wfc(), config);
    }

    // Android audio seemed to have perceptible latency so do this to maybe help.
    // https://libgdx.com/wiki/audio/audio#audio-on-android
    // https://github.com/barsoosayque/libgdx-oboe/blob/master/docs/Usage.md
    @Override
    public AndroidAudio createAudio(Context context, AndroidApplicationConfiguration config) {
        return new OboeAudio(context.getAssets());
    }

}
