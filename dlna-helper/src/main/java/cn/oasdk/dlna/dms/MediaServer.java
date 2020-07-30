
package cn.oasdk.dlna.dms;

import android.app.Activity;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.Icon;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.ManufacturerDetails;
import org.fourthline.cling.model.meta.ModelDetails;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.support.model.PersonWithRole;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.item.AudioBook;
import org.fourthline.cling.support.model.item.ImageItem;
import org.fourthline.cling.support.model.item.MusicTrack;
import org.fourthline.cling.support.model.item.VideoItem;
import org.seamless.util.MimeType;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cn.oasdk.dlna.util.UpnpUtil;
import cn.oasdk.dlna.util.Utils;
import cn.oaui.L;
import cn.oaui.data.Row;
import cn.oaui.utils.AppUtils;
import cn.oaui.utils.FileUtils;
import cn.oaui.utils.SPUtils;

import static android.R.attr.duration;

public class MediaServer {

    private UDN udn;

    private LocalDevice localDevice;

    private final static String deviceType = "MediaServer";

    //file_type
    public static interface FILE_TYPE{
        String VIDEO="video_file_";
        String RADIO="radio_file_";
        String IMAGE="image_file_";
        String NET="net_file";
    };


    private final static int version = 1;

    private final static String LOGTAG = "MediaServer";

    public final static int PORT = 8192;
    private Activity activity;

    public static LinkedList<Row> rowsAllFile = new LinkedList<>();

    public static LinkedList<Row> rowsVideo = new LinkedList<>();


    public static LinkedList<Row> rowsRadio = new LinkedList<>();

    public static LinkedList<Row> rowsImage = new LinkedList<>();

    public static LinkedList<Row> rowsNet = new LinkedList<>();


    //http临时服务地址映射
    public static Map<String,String> serverInflat=new HashMap<>();




    public MediaServer(Activity activity) throws ValidationException {
        this.activity = activity;
        DeviceType type = new UDADeviceType(deviceType, version);
        //TODO 自定义服务名称
        DeviceDetails details = new DeviceDetails(
                 android.os.Build.MODEL, new ManufacturerDetails(
                android.os.Build.MANUFACTURER), new ModelDetails(android.os.Build.MODEL,
                Utils.DMS_DESC, "v1"));

        LocalService service = new AnnotationLocalServiceBinder()
                .read(ContentDirectoryService.class);
        //
        //service.setManager(new DefaultServiceManager<ContentDirectoryService>(service,
        //        ContentDirectoryService.class));

        udn = UpnpUtil.uniqueSystemIdentifier("msidms");

        localDevice = new LocalDevice(new DeviceIdentity(udn), type, details, createDefaultDeviceIcon(), service);


        // start http server
        try {
            new HttpServer(PORT);
        } catch (IOException ioe) {
            System.err.println("Couldn't start server:\n" + ioe);
            System.exit(-1);
        }

        Log.v(LOGTAG, "Started Http Server on port " + PORT);
    }

    public LocalDevice getLocalDevice() {
        return localDevice;
    }

    public void setLocalDevice(LocalDevice localDevice) {
        this.localDevice = localDevice;
    }

    protected Icon createDefaultDeviceIcon() {
        try {
            return new Icon("image/png", 48, 48, 32, "msi.png", activity.getResources().getAssets()
                    .open("ic_launcher.png"));
        } catch (IOException e) {
            Log.w(LOGTAG, "createDefaultDeviceIcon IOException");
            return null;
        }
    }





    public static void initMediaData(Activity activity) {
        rowsVideo.clear();
        //List<RowObject> videoList = getVideoList(activity);
        //if(videoList!=null) {
        //    rowsVideo.addAll(videoList);
        //}
        rowsVideo.addAll(FileServer.rowsVideo);


        rowsRadio.clear();
        List<Row> radioList = getRadioList(activity);
        if(radioList!=null) {
            rowsRadio.addAll(radioList);
        }

        rowsImage.clear();
        List<Row> imageList = getImageList(activity);
        if(imageList!=null) {
            rowsImage.addAll(imageList);
        }

        rowsNet.clear();
        List<Row> netList = getNetList(activity);
        if(netList!=null){
            rowsNet.addAll(netList);
        }


    }


    public static VideoItem buildVideoItem(Row row) {
        String id =  row.getString(MediaStore.Video.Media._ID);
        String title = row.getString(MediaStore.Video.Media.DISPLAY_NAME);
        String creator = row.getString(MediaStore.Video.Media.ARTIST);
        String filePath = row.getString("filePath");
        String mimeType = row.getString(MediaStore.Video.Media.MIME_TYPE);
        Long size = row.getLong(MediaStore.Video.Media.SIZE);
        Long duration = row.getLong(MediaStore.Video.Media.DURATION);
        String resolution = row.getString(MediaStore.Video.Media.RESOLUTION);
        String description = row.getString(MediaStore.Video.Media.DESCRIPTION);
        String adrress;
        if(filePath.startsWith(FileUtils.getSDCardPath())){
            adrress = "http:/" + getAddress() + "/" + FILE_TYPE.VIDEO +id;
        }else{
            adrress=filePath;
        }
        L.i("============buildVideoItem==========="+"    "+mimeType);
        MimeType mimeType1 = null;
        if(mimeType!=null){
            mimeType1 = new MimeType(mimeType.substring(0,
                    mimeType.indexOf('/')), mimeType.substring(mimeType
                    .indexOf('/') + 1));
        }else{
            mimeType1=new MimeType("video","*");
        }
        Res res = new Res(mimeType1, size, adrress);
        if(duration!=null){
            res.setDuration(Utils.formatDuration(duration));
        }
        if(resolution!=null){
            res.setResolution(resolution);
        }
        VideoItem videoItem = new VideoItem(id, "1",
                title, title, res);
        videoItem.setDescription(description);
        return videoItem;
    }

    public static VideoItem buildNetItem(Row row) {
        //String id = FILE_TYPE.VIDEO + rowObject.getString(MediaStore.Video.Media._ID);
        //String title = rowObject.getString(MediaStore.Video.Media.DISPLAY_NAME);
        //String creator = rowObject.getString(MediaStore.Video.Media.ARTIST);
        //String filePath = rowObject.getString(MediaStore.Video.Media.DATA);
        //String mimeType = rowObject.getString(MediaStore.Video.Media.MIME_TYPE);
        //Long size = rowObject.getLong(MediaStore.Video.Media.SIZE);
        //Long duration = rowObject.getLong(MediaStore.Video.Media.DURATION);
        //String resolution = rowObject.getString(MediaStore.Video.Media.RESOLUTION);
        //String description = rowObject.getString(MediaStore.Video.Media.DESCRIPTION);
        //String adrress = "http:/" + getAddress() + "/" + id;
        String name = row.getString("name");
        Res res = new Res(new MimeType("",""), 0l, row.getString("filePath"));

        res.setDuration(duration / (1000 * 60 * 60) + ":"
                + (duration % (1000 * 60 * 60)) / (1000 * 60) + ":"
                + (duration % (1000 * 60)) / 1000);

        VideoItem videoItem = new VideoItem( UUID.randomUUID().toString(), "1",
                name, name, res);
        return videoItem;
    }



    public static MusicTrack buildRadioItem(Row row) {
        String id =FILE_TYPE.RADIO+ row.getString(MediaStore.Audio.Media._ID);
        String title = row.getString(MediaStore.Audio.Media.TITLE);
        String creator = row.getString(MediaStore.Audio.Media.ARTIST);
        String filePath = row.getString(MediaStore.Audio.Media.DATA);
        String mimeType = row.getString(MediaStore.Audio.Media.MIME_TYPE);
        Long size = row.getLong(MediaStore.Audio.Media.SIZE);
        String album = row.getString(MediaStore.Audio.Media.ALBUM);
        Res res = null;
        try {
            String adrress = "http:/" + getAddress() + "/" + id;
            L.i("============buildRadioItem===========" + adrress);
            res = new Res(new MimeType(mimeType.substring(0,
                    mimeType.indexOf('/')), mimeType.substring(mimeType
                    .indexOf('/') + 1)), size, adrress);
        } catch (Exception e) {
            Log.w(LOGTAG, "Exception1", e);
        }
        res.setDuration(duration / (1000 * 60 * 60) + ":"
                + (duration % (1000 * 60 * 60)) / (1000 * 60) + ":"
                + (duration % (1000 * 60)) / 1000);
        // Music Track must have `artist' with role field, or
        // DIDLParser().generate(didl) will throw nullpointException
        MusicTrack musicTrack = new MusicTrack(id,
                "2", title, creator, album,
                new PersonWithRole(creator, "Performer"), res);
        return musicTrack;
    }

    public static AudioBook buildPlaylistItem(LinkedList<Row> rows) {
        Res[] argRes = new Res[rows.size()];
        for (int i = 0; i < rows.size(); i++) {
            Row row = rows.get(i);
            String id = row.getString(MediaStore.Audio.Media._ID);
            String title = row.getString(MediaStore.Audio.Media.TITLE);
            String creator = row.getString(MediaStore.Audio.Media.ARTIST);
            String filePath = row.getString(MediaStore.Audio.Media.DATA);
            String mimeType = row.getString(MediaStore.Audio.Media.MIME_TYPE);
            Long size = row.getLong(MediaStore.Audio.Media.SIZE);
            String album = row.getString(MediaStore.Audio.Media.ALBUM);
            Res res = null;
            try {
                String adrress = "http:/" + getAddress() + "/" + id;
                L.i("============buildRadioItem===========" + adrress);
                res = new Res(new MimeType(mimeType.substring(0,
                        mimeType.indexOf('/')), mimeType.substring(mimeType
                        .indexOf('/') + 1)), size, adrress);
            } catch (Exception e) {
                Log.w(LOGTAG, "Exception1", e);
            }
            res.setDuration(duration / (1000 * 60 * 60) + ":"
                    + (duration % (1000 * 60 * 60)) / (1000 * 60) + ":"
                    + (duration % (1000 * 60)) / 1000);
            // Music Track must have `artist' with role field, or
            // DIDLParser().generate(didl) will throw nullpointException
            argRes[i] = res;
        }
        AudioBook playlistItem = new AudioBook("1", "3",
                "11", "ms", null, null, null, argRes);
        return playlistItem;
    }


    public static ImageItem buildImageItem(Row row) {
        String id = FILE_TYPE.IMAGE+ row.getString(MediaStore.Images.Media._ID);
        String title = row.getString(MediaStore.Images.Media.TITLE);
        String creator = "unkown";
        String mimeType = row.getString(MediaStore.Images.Media.MIME_TYPE);
        Long size = row.getLong(MediaStore.Images.Media.SIZE);
        String description = row.getString(MediaStore.Images.Media.DESCRIPTION);

        String url = "http:/" + getAddress() + "/"
                + id;
        L.i("============buildImageItem==========="+mimeType.substring(0,
                mimeType.indexOf('/')));
        L.i("============buildImageItem==========="+mimeType.substring(mimeType
                .indexOf('/') + 1));
        L.i("============buildImageItem==========="+size);
        Res res = new Res(new MimeType(mimeType.substring(0,
                mimeType.indexOf('/')), mimeType.substring(mimeType
                .indexOf('/') + 1)), size, url);

        ImageItem imageItem = new ImageItem(id,
                "4", title,
                creator, res);
        //if (imageThumbs.containsKey(imageId)) {
        //    String thumb = imageThumbs.get(imageId);
        //    Log.i(LOGTAG, " image thumb:" + thumb);
        //    // set albumArt Property
        //    DIDLObject.Property albumArtURI = new DIDLObject.Property.UPNP.ALBUM_ART_URI(
        //            URI.create("http://"
        //                    + getAddress() + thumb));
        //    DIDLObject.Property[] properties = { albumArtURI };
        //    imageItem.addProperties(properties);
        //}
        imageItem.setDescription(description);


        return imageItem;
    }


    /**
     * 获取手机上的全部视频
     *
     * @param activity
     * @return
     */
    public static List<Row> getVideoList(Activity activity) {
        String[] videoColumns = {MediaStore.Video.Media._ID,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.ARTIST,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.RESOLUTION,
                MediaStore.Video.Media.DESCRIPTION};
        Cursor cursor = activity.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                videoColumns, null, null, null);
        List<Row> rows = new LinkedList<>();
        if (cursor.moveToFirst()) {//判断数据表里有数据
            while (cursor.moveToNext()) {//遍历数据表中的数据
                Row row = new Row();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    String columnName = cursor.getColumnName(i);
                    row.put(columnName, cursor.getString(i));
                    if (MediaStore.Video.Media.DATA.equals(columnName)) {
                        row.put("filePath", cursor.getString(i));
                        //row.put("filePath","");
                    } else if (MediaStore.Video.Media.DISPLAY_NAME.equals(columnName)) {
                        row.put("name", cursor.getString(i));
                    }
                }
                row.put("type", "video");
                rows.add(row);
            }
            cursor.close();
        }
        return rows;
    }


    /**
     * 获取手机上的全部音乐
     *
     * @param activity
     * @return
     */
    public static List<Row> getRadioList(Activity activity) {
        String[] audioColumns = {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.MIME_TYPE, MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ALBUM};
        Cursor cursor = activity.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                audioColumns, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        List<Row> rows = new LinkedList<>();
        if (cursor.moveToFirst()) {//判断数据表里有数据
            while (cursor.moveToNext()) {//遍历数据表中的数据
                Row row = new Row();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    String columnName = cursor.getColumnName(i);
                    row.put(columnName, cursor.getString(i));
                    if (MediaStore.Video.Media.DATA.equals(columnName)) {
                        row.put("filePath", cursor.getString(i));
                    } else if (MediaStore.Audio.Media.DISPLAY_NAME.equals(columnName)) {
                        row.put("name", cursor.getString(i));
                    }

                }
                row.put("type", "radio");
                rows.add(row);
            }
            cursor.close();
        }
        L.i("============getRadioList===========" + rows);
        return rows;
    }

    /**
     * 获取手机上的全部音乐
     *
     * @param activity
     * @return
     */
    public static List<Row> getImageList(Activity activity) {
        String[] imageColumns = {MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DESCRIPTION};
        Cursor cursor = activity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                imageColumns, null, null, MediaStore.Images.Media.DATA);
        LinkedList<Row> rows = new LinkedList<>();
        if (cursor.moveToFirst()) {//判断数据表里有数据
            while (cursor.moveToNext()) {//遍历数据表中的数据
                Row row = new Row();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    String columnName = cursor.getColumnName(i);
                    row.put(columnName, cursor.getString(i));
                    if (MediaStore.Video.Media.DATA.equals(columnName)) {
                        row.put("filePath", cursor.getString(i));
                    } else if (MediaStore.Audio.Media.DISPLAY_NAME.equals(columnName)) {
                        row.put("name", cursor.getString(i));
                    }

                }
                row.put("type", "image");
                rows.add(row);
            }
            cursor.close();
        }
        return rows;
    }


    public static List<Row> getNetList(Activity activity) {
        List<Row> url_list = SPUtils.getRows(FILE_TYPE.NET, "url_list");
        return url_list;
    }


    /**
     * 拼装保存的videoThumb的路径
     *
     * @return
     */
    public static String getSaveVideoFilePath(String path, String id) {
        String PNG_SUFFIX = ".png";
        String VIDEO_THUMBNAIL_PREFIX = "video_thumb_";
        String VIDEO_THUMB_PATH = "/msi/.videothumb";
        return FileUtils.getSDCardPath() + VIDEO_THUMB_PATH
                + File.separator + VIDEO_THUMBNAIL_PREFIX + id + PNG_SUFFIX;
    }

    /**
     * 获取本机ip地址
     * @return
     */
    public static String getAddress() {
        return AppUtils.getLocalIpAddress().toString() + ":" + PORT;
    }

    public static String getFilePathById(String mediaId) {
        String filePath = null;
        if (mediaId.startsWith(FILE_TYPE.VIDEO)){
            mediaId=mediaId.replace(FILE_TYPE.VIDEO,"");
            for (int i = 0; i < rowsVideo.size(); i++) {
                Row row = rowsVideo.get(i);
                if (row.containsValue(mediaId)) {
                    filePath = row.getString("filePath");
                    i = rowsVideo.size();
                }
            }
        }else if (mediaId.startsWith(FILE_TYPE.RADIO)) {
            mediaId=mediaId.replace(FILE_TYPE.RADIO,"");
            for (int i = 0; i < rowsRadio.size(); i++) {
                Row row = rowsRadio.get(i);
                if (row.containsValue(mediaId)) {
                    filePath = row.getString("filePath");
                    i = rowsRadio.size();
                }
            }
        }else if (mediaId.startsWith(FILE_TYPE.IMAGE)) {
            mediaId=mediaId.replace(FILE_TYPE.IMAGE,"");
            for (int i = 0; i < rowsImage.size(); i++) {
                Row row = rowsImage.get(i);
                if (row.containsValue(mediaId)) {
                    filePath = row.getString("filePath");
                    i = rowsImage.size();
                }
            }
        }
        return filePath;
    }







}
