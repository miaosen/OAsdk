package cn.oasdk.dlna.main;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.support.avtransport.callback.GetPositionInfo;
import org.fourthline.cling.support.avtransport.callback.Seek;
import org.fourthline.cling.support.model.PositionInfo;

import cn.oahttp.HandlerQueue;
import cn.oasdk.dlna.R;
import cn.oasdk.dlna.dmc.DMCControl;
import cn.oasdk.dlna.dmc.PauseCallback;
import cn.oasdk.dlna.dmc.PlayerCallback;
import cn.oasdk.dlna.dmc.StopCallback;
import cn.oasdk.dlna.dms.DLNAService;
import cn.oasdk.dlna.util.Utils;
import cn.oaui.annotation.InjectReader;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.Row;
import cn.oaui.view.CustomLayout;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2019-08-20  15:20
 * @Descrition
 */

public class ControlView extends CustomLayout {


    boolean isPalying = false;

    @ViewInject
    SeekBar seekBar;

    @ViewInject
    TextView tv_time_start,tv_time_end;

    @ViewInject
    ImageView img_stop,img_pause;

    public ControlView(Context context) {
        super(context);
    }

    public ControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onCreateView() {
        InjectReader.injectAllFields(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int prog = seekBar.getProgress();
                String relTime = Utils.secToTime(prog);
                DMCControl.seek(new Seek( DLNAService.playerDevice
                        .findService(new UDAServiceType("AVTransport")),relTime) {
                    @Override
                    public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String s) {

                    }
                });
                //String totalTime = tvTotalTime.getText().toString();
                //controlBiz.seek(totalTime, prog);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
            }
        });
        img_stop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                DMCControl.stop(new StopCallback() {
                    @Override
                    public void onResult(String msg) {
                        HandlerQueue.onResultCallBack(new Runnable() {
                            @Override
                            public void run() {
                                isPalying=false;
                                seekBar.setProgress(0);
                                img_pause.setImageDrawable(getResources().getDrawable(R.mipmap.icon_play));
                                img_pause.setTag("play");
                            }
                        });
                    }
                });
            }
        });
        img_pause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if("pause".equals(img_pause.getTag())){
                    DMCControl.pause(new PauseCallback() {
                        @Override
                        public void onResult(String msg) {
                            isPalying=false;
                        }
                    });
                    img_pause.setImageDrawable(getResources().getDrawable(R.mipmap.icon_play));
                    img_pause.setTag("play");
                }else{
                    DMCControl.play(new PlayerCallback() {
                        @Override
                        public void onResult(String msg) {
                            isPalying=true;
                            new GetPositionThread().start();
                        }
                    });
                    img_pause.setImageDrawable(getResources().getDrawable(R.mipmap.icon_pause));
                    img_pause.setTag("pause");
                }
            }
        });
    }

    @Override
    public int setXmlLayout() {
        return R.layout.view_control;
    }


    public void start(Row row) {
         if ("image".equals(row.getString("type"))) {
             setVisibility(INVISIBLE);
         }else{
             setVisibility(VISIBLE);
             isPalying = true;
             Integer duration = row.getInteger("duration");
             String strTime = Utils.formatDuration(duration);
             tv_time_end.setText(strTime);
             int realTime = Utils.getRealTime(strTime);
             seekBar.setMax(realTime);
             new GetPositionThread().start();
         }
    }

    class GetPositionThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                while (isPalying) {
                    sleep(1000);
                    DMCControl.getPositionInfo(new GetPositionInfo(DLNAService.playerDevice
                            .findService(new UDAServiceType("AVTransport"))) {
                        @Override
                        public void received(ActionInvocation actionInvocation, final PositionInfo positionInfo) {
                            HandlerQueue.onResultCallBack(new Runnable() {
                                @Override
                                public void run() {
                                    String relTime = positionInfo.getRelTime();
                                    tv_time_start.setText(relTime);
                                    int realTime = Utils.getRealTime(relTime);
                                    seekBar.setProgress(realTime);
                                }
                            });
                        }

                        @Override
                        public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String s) {

                        }
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
