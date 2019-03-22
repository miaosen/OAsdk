/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package gzpykj.tvshow;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

import com.oaui.L;
import com.oaui.utils.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;


/*
 * MainActivity class that loads MainFragment
 */
public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    Socket socket = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (socket == null) {
                        socket = new Socket("192.168.1.177", 8088);
                    }
                    L.i("=========run==============" + socket.getLocalSocketAddress());
                    if (socket.isConnected()) {
                        L.i("=========run==========连接服务器成功====");
                    }
                    while (true) {
                        long runTime = System.currentTimeMillis();
                        //5秒接收和发送一次数据
                        //if ((runTime - startTime) / 1000 > 5) {
                        //startTime = runTime;
                        InputStream is = socket.getInputStream();
                        if (is.available() > 0) {
                            L.i("=====run======" + Build.VERSION.SDK + socket.isConnected() + FileUtils.toString(is));
                        }
                        String uuid = UUID.randomUUID().toString();
                        OutputStream outputStream = socket.getOutputStream();
                        outputStream.write(uuid.getBytes());
                        outputStream.flush();
                        Thread.sleep(5000);
                        //}
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }
}
