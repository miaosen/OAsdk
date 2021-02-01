package cn.oasdk.webview.data;

import android.os.Build;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.RequiresApi;
import cn.oahttp.HandlerQueue;
import cn.oahttp.HttpRequest;
import cn.oahttp.HttpUtils;
import cn.oahttp.callback.FileCallBack;
import cn.oasdk.webview.view.MediaGallerView;
import cn.oasdk.webview.view.WebFrameView;
import cn.oaui.L;
import cn.oaui.data.Row;
import cn.oaui.utils.StringUtils;
import cn.oaui.utils.URIUtils;
import cn.oaui.view.listview.BaseFillAdapter;
import okhttp3.Headers;
import okhttp3.Response;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2020-08-03  16:46
 * @Descrition
 */
public class WebResoureDeal {


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static WebResourceResponse dealRes(WebView view, WebResourceRequest webResourceRequest, WebFrameView webFrameView,String pageUrl) {
        //同步执行会话请求
        final String url = webResourceRequest.getUrl().toString();
        if (StringUtils.isNotEmpty(url)) {
            HttpRequest httpRequest = HttpRequest.setUrl(url);
            Map<String, String> requestHeaders = webResourceRequest.getRequestHeaders();
            if (requestHeaders != null) {
                httpRequest.setHeaders(requestHeaders);
            }
            String method = webResourceRequest.getMethod();
            if (StringUtils.isNotEmpty(method)) {
                httpRequest.setMethod(method);
            }
            final Response response = httpRequest.send();
            if (response != null) {
                Headers headers = response.headers();
                Map<String, String> responseHeaders = new HashMap<>();
                long totalSize = response.body().contentLength();
                //if(totalSize<=0){
                //    String s = headers.get("Content-Length");
                //    L.i("============dealRes==========="+s);
                //    if(StringUtils.isNotEmpty(s)){
                //        totalSize= Long.parseLong(s);
                //    }
                //}
                for (int i = 0; i < headers.size(); i++) {
                    responseHeaders.put(headers.name(i), headers.value(i));
                }
                String mimeType = "";
                String contentType = headers.get("Content-Type") + "";
                if (StringUtils.isEmpty(contentType)) {
                    contentType = headers.get("content-type");
                }
                if (StringUtils.isEmpty(contentType)) {
                    contentType = "text/html";
                }
                if (contentType.contains(";")) {
                    mimeType = mimeType.split(";")[0];
                } else {
                    mimeType = contentType;
                }

                //L.i("============dealRes===========" + mimeType + "  " + url);
                //TODO Content-Type: application/octet-stream按文件名处理
                if (contentType.contains("video")) {
                    //new Thread(new Runnable() {
                    //    @Override
                    //    public void run() {
                    //
                    //    }
                    //}).start();
                    logicRes(webFrameView, url, "video", totalSize);
                    //return null;
                    return new WebResourceResponse(mimeType, "UTF-8", 206, "aa", responseHeaders, new InputStream() {
                        @Override
                        public int read() throws IOException {
                            return 0;
                        }
                    });
                } else if (contentType.contains("audio")) {
                    logicRes(webFrameView, url, "audio", totalSize);
                    //return null;
                    return new WebResourceResponse(mimeType, "UTF-8", 206, "aa", responseHeaders, new InputStream() {
                        @Override
                        public int read() throws IOException {
                            return 0;
                        }
                    });
                }
                else if (contentType.contains("image11")) {
                    String[] type = mimeType.split("/");
                    String dir= FileCallBack.saveDir + "/.mediaCache/"+ URIUtils.getIP(pageUrl).replaceAll("/","")+"/";
                    String fileName = HttpUtils.getFileName(response, url);
                    String filePath = dir+ fileName;
                    L.i("============dealRes===========" + filePath);
                    if (!fileName.contains(".") && mimeType.contains("image") && type.length > 1 && !fileName.endsWith(type[1])) {
                        filePath = filePath + "." + type[1];
                    }
                    final File file = new File(filePath);
                    try {
                        File file1 = new File(dir);
                        if (!file1.exists()) {
                            boolean mkdirs = file1.mkdirs();
                        }
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        final PipedOutputStream out = new PipedOutputStream();
                        final PipedInputStream[] pipedInputStream = {new PipedInputStream(out)};
                        String finalS = filePath;
                        long finalTotalSize = totalSize;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    InputStream is = response.body().byteStream();
                                    FileOutputStream fos = new FileOutputStream(file);
                                    byte[] b = new byte[1024];
                                    int n;
                                    while ((n = is.read(b)) != -1) {
                                        out.write(b, 0, n);
                                        fos.write(b, 0, n);
                                    }
                                    out.close();
                                    fos.close();
                                    is.close();
                                    logicRes(webFrameView,
                                            finalS,
                                            "image",
                                            file.length());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        return new WebResourceResponse(mimeType, "UTF-8", pipedInputStream[0]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    //logicRes(webFrameView, url, "image", totalSize);
                }
                //}
            }
        }
        return null;
    }

    private static void logicRes(WebFrameView webFrameView, String url, String mediaType, long fileSize) {
        if (fileSize >= 10 * 1024) {


            HandlerQueue.onResultCallBack(new Runnable() {
                @Override
                public void run() {
                    //webFrameView.resourceListView.horizontalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    //    @Override
                    //    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //
                    //    }
                    //});
                    webFrameView.resourceListView.listAdapter.setOnItemClickListener(new BaseFillAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View convertView, Row row, int position, BaseFillAdapter.ViewHolder holder) {
                            //webFrameView.playMedia(row.getString("url"), 50 + "");
                            MediaGallerView mediaGallerView = getGallery(webFrameView);
                            //if (position != mediaGallerView.viewPager.getCurrentItem()||mediaGallerView.adapter.getCount()!=webFrameView.resourceListView.rows.size()) {
                                mediaGallerView.setRowsMedia(webFrameView.resourceListView.rows);
                                mediaGallerView.adapter.notifyDataSetChanged();
                                //webFrameView.resourceListView.horizontalListView.setSelection(position);
                                L.i("============onItemClick===========" + row + " " + position + "  " + mediaGallerView.rowsMedia.get(position));
                                mediaGallerView.viewPager.setCurrentItem(position);
                            //}
                        }
                    });
                    webFrameView.resourceListView.addRes(url, mediaType, fileSize);
                    //refreshIfShowGallery(webFrameView);
                }
            });
        }
    }

    private static MediaGallerView getGallery(WebFrameView webFrameView) {
        MediaGallerView mediaGallerView;
        if (webFrameView.ln_content.getChildCount() == 2) {
            mediaGallerView = new MediaGallerView(webFrameView.getContext());
            webFrameView.ln_content.addView(mediaGallerView, webFrameView.ln_content.getChildCount());
        } else {
            mediaGallerView = (MediaGallerView) webFrameView.ln_content.getChildAt(webFrameView.ln_content.getChildCount() - 1);
        }

        return mediaGallerView;
    }


    private static MediaGallerView refreshIfShowGallery(WebFrameView webFrameView) {
        MediaGallerView mediaGallerView = null;
        if (webFrameView.ln_content.getChildCount() == 3) {
            mediaGallerView = (MediaGallerView) webFrameView.ln_content.getChildAt(webFrameView.ln_content.getChildCount() - 1);
            mediaGallerView.setRowsMedia(webFrameView.resourceListView.rows);
            mediaGallerView.adapter.notifyDataSetChanged();

        }
        return mediaGallerView;
    }

}
