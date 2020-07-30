package cn.oasdk.webview.view;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import cn.oasdk.fileview.R;
import cn.oaui.annotation.InjectReader;
import cn.oaui.annotation.ViewInject;
import cn.oaui.view.CustomLayout;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2020-07-20  11:39
 * @Descrition
 */
public class VideoPalyView extends CustomLayout {

    @ViewInject
    PlayerView playerView;
    Uri uri;

    public SimpleExoPlayer player;

    public VideoPalyView(Context context) {
        super(context);
    }

    public VideoPalyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onCreateView() {

        InjectReader.injectAllFields(this);
        // Instantiate the player.
        //缓冲控制
        LoadControl loadControl = new LoadControl();
        player = new SimpleExoPlayer.Builder(context).setLoadControl(loadControl).build();
        // Attach player to the view.
        playerView.setPlayer(player);

        // Prepare the player with the media source.
        //File file=new File(FileUtils.getSDCardPath()+"/phoneSaver/aa.mp3");
        //uri = Uri.parse(FileUtils.getSDCardPath()+"/phoneSaver/aa.mp3");


    }


    public void prepare(Uri uri) {
        player.prepare(createMediaSource(uri));
    }


    public void stop(){
        player.stop();
    }


    private MediaSource createMediaSource(Uri uri) {
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, "yourApplicationName"));
// This is the MediaSource representing the media to be played.
        MediaSource videoSource =
                new ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(uri);
        return videoSource;
    }

    @Override
    public int setXmlLayout() {
        return R.layout.exo_videopaly_view;
    }



    class LoadControl extends DefaultLoadControl{

        public LoadControl(){
            super();
        }

        @Override
        public boolean shouldContinueLoading(long bufferedDurationUs, float playbackSpeed) {
            return true;
        }

    }
}
