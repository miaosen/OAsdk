
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
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import cn.oasdk.dlna.util.UpnpUtil;
import cn.oasdk.dlna.util.Utils;
import cn.oaui.L;
import cn.oaui.data.RowObject;
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


    public static LinkedList<RowObject> rowsVideo = new LinkedList<>();


    public static LinkedList<RowObject> rowsRadio = new LinkedList<>();

    public static LinkedList<RowObject> rowsImage = new LinkedList<>();

    public static LinkedList<RowObject> rowsNet = new LinkedList<>();

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


    //public void prepareMediaServer(Handler handler) {
    //
    //    ContentNode rootNode = ContentTree.getRootNode();
    //    Cursor cursor;
    //    // Video Container
    //
    //    rowsVideo.clear();
    //    rowsVideo.addAll(getVideoList(activity));
    //
    //
    //    rowsRadio.clear();
    //    rowsRadio.addAll(getRadioList(activity));
    //
    //    //Container videoContainer = new Container();
    //    //videoContainer.setClazz(new DIDLObject.Class("object.container"));
    //    //videoContainer.setId(ContentTree.VIDEO_ID);
    //    //videoContainer.setParentID(ContentTree.ROOT_ID);
    //    //videoContainer.setTitle("Videos");
    //    //videoContainer.setRestricted(true);
    //    //videoContainer.setWriteStatus(WriteStatus.NOT_WRITABLE);
    //    //videoContainer.setChildCount(0);
    //    //
    //    //rootNode.getContainer().addContainer(videoContainer);
    //    //rootNode.getContainer().setChildCount(
    //    //        rootNode.getContainer().getChildCount() + 1);
    //    //ContentTree.addNode(ContentTree.VIDEO_ID, new ContentNode(
    //    //        ContentTree.VIDEO_ID, videoContainer));
    //    //
    //    //Cursor cursor;
    //    //String[] videoColumns = { MediaStore.Video.Media._ID,
    //    //        MediaStore.Video.Media.TITLE, MediaStore.Video.Media.DATA,
    //    //        MediaStore.Video.Media.ARTIST,
    //    //        MediaStore.Video.Media.MIME_TYPE, MediaStore.Video.Media.SIZE,
    //    //        MediaStore.Video.Media.DURATION,
    //    //        MediaStore.Video.Media.RESOLUTION,
    //    //        MediaStore.Video.Media.DESCRIPTION };
    //    //cursor = activity.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
    //    //        videoColumns, null, null, null);
    //
    //    //if (cursor.moveToFirst()) {
    //    //    do {
    //    //        String id = ContentTree.VIDEO_PREFIX
    //    //                + cursor.getInt(cursor
    //    //                .getColumnIndex(MediaStore.Video.Media._ID));
    //    //        String title = cursor.getString(cursor
    //    //                .getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
    //    //        String creator = cursor.getString(cursor
    //    //                .getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST));
    //    //        String filePath = cursor.getString(cursor
    //    //                .getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
    //    //
    //    //        String mimeType = cursor
    //    //                .getString(cursor
    //    //                        .getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
    //    //        long size = cursor.getLong(cursor
    //    //                .getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
    //    //        long duration = cursor
    //    //                .getLong(cursor
    //    //                        .getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
    //    //        String resolution = cursor
    //    //                .getString(cursor
    //    //                        .getColumnIndexOrThrow(MediaStore.Video.Media.RESOLUTION));
    //    //
    //    //        String description = cursor
    //    //                .getString(cursor
    //    //                        .getColumnIndexOrThrow(MediaStore.Video.Media.DESCRIPTION));
    //    //
    //    //        Res res = new Res(new MimeType(mimeType.substring(0,
    //    //                mimeType.indexOf('/')), mimeType.substring(mimeType
    //    //                .indexOf('/') + 1)), size, "http://"
    //    //                + getAddress() + "/" + id);
    //    //
    //    //        res.setDuration(duration / (1000 * 60 * 60) + ":"
    //    //                + (duration % (1000 * 60 * 60)) / (1000 * 60) + ":"
    //    //                + (duration % (1000 * 60)) / 1000);
    //    //        res.setResolution(resolution);
    //    //
    //    //        VideoItem videoItem = new VideoItem(id, ContentTree.VIDEO_ID,
    //    //                title, creator, res);
    //    //
    //    //        // add video thumb Property
    //    //        String videoSavePath = getSaveVideoFilePath(filePath,
    //    //                id);
    //    //        DIDLObject.Property albumArtURI = new DIDLObject.Property.UPNP.ALBUM_ART_URI(
    //    //                URI.create("http://" + getAddress()
    //    //                        + videoSavePath));
    //    //        DIDLObject.Property[] properties = { albumArtURI };
    //    //        videoItem.addProperties(properties);
    //    //        videoItem.setDescription(description);
    //    //        videoContainer.addItem(videoItem);
    //    //        videoContainer
    //    //                .setChildCount(videoContainer.getChildCount() + 1);
    //    //        ContentTree.addNode(id,
    //    //                new ContentNode(id, videoItem, filePath));
    //    //        RowObject row=new RowObject();
    //    //        row.put("filePath",filePath);
    //    //        row.put("name",title);
    //    //        row.put("item",videoItem);
    //    //        row.put("videoContainer",videoContainer);
    //    //
    //    //        rowsVideo.add(row);
    //    //        Log.v(LOGTAG, "added video item " + title + " from " +
    //    //                filePath);
    //    //    } while (cursor.moveToNext());
    //    //}
    //
    //    // Audio Container
    //    Container audioContainer = new Container(ContentTree.AUDIO_ID,
    //            ContentTree.ROOT_ID, "Audios", "GNaP MediaServer",
    //            new DIDLObject.Class("object.container"), 0);
    //    audioContainer.setRestricted(true);
    //    audioContainer.setWriteStatus(WriteStatus.NOT_WRITABLE);
    //    rootNode.getContainer().addContainer(audioContainer);
    //    rootNode.getContainer().setChildCount(
    //            rootNode.getContainer().getChildCount() + 1);
    //    ContentTree.addNode(ContentTree.AUDIO_ID, new ContentNode(
    //            ContentTree.AUDIO_ID, audioContainer));
    //
    //    String[] audioColumns = { MediaStore.Audio.Media._ID,
    //            MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
    //            MediaStore.Audio.Media.ARTIST,
    //            MediaStore.Audio.Media.MIME_TYPE, MediaStore.Audio.Media.SIZE,
    //            MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ALBUM };
    //    cursor = activity.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
    //            audioColumns, null, null, null);
    //    if (cursor.moveToFirst()) {
    //        do {
    //            String id = ContentTree.AUDIO_PREFIX
    //                    + cursor.getInt(cursor
    //                    .getColumnIndex(MediaStore.Audio.Media._ID));
    //            String title = cursor.getString(cursor
    //                    .getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
    //            String creator = cursor.getString(cursor
    //                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
    //            String filePath = cursor.getString(cursor
    //                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
    //            String mimeType = cursor
    //                    .getString(cursor
    //                            .getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE));
    //            long size = cursor.getLong(cursor
    //                    .getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
    //            long duration = cursor
    //                    .getLong(cursor
    //                            .getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
    //            String album = cursor.getString(cursor
    //                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
    //            Res res = null;
    //            try {
    //                res = new Res(new MimeType(mimeType.substring(0,
    //                        mimeType.indexOf('/')), mimeType.substring(mimeType
    //                        .indexOf('/') + 1)), size, "http://"
    //                        + getAddress() + "/" + id);
    //            } catch (Exception e) {
    //                Log.w(LOGTAG, "Exception1", e);
    //            }
    //
    //            if (null == res) {
    //                break;
    //            }
    //            res.setDuration(duration / (1000 * 60 * 60) + ":"
    //                    + (duration % (1000 * 60 * 60)) / (1000 * 60) + ":"
    //                    + (duration % (1000 * 60)) / 1000);
    //
    //            // Music Track must have `artist' with role field, or
    //            // DIDLParser().generate(didl) will throw nullpointException
    //            MusicTrack musicTrack = new MusicTrack(id,
    //                    ContentTree.AUDIO_ID, title, creator, album,
    //                    new PersonWithRole(creator, "Performer"), res);
    //            audioContainer.addItem(musicTrack);
    //            audioContainer
    //                    .setChildCount(audioContainer.getChildCount() + 1);
    //            ContentTree.addNode(id, new ContentNode(id, musicTrack,
    //                    filePath));
    //
    //            // Log.v(LOGTAG, "added audio item " + title + "from " +
    //            // filePath);
    //        } while (cursor.moveToNext());
    //    }
    //
    //    String[] imageThumbColumns = new String[] {
    //            MediaStore.Images.Thumbnails.IMAGE_ID,
    //            MediaStore.Images.Thumbnails.DATA };
    //    // get image thumbnail
    //    Cursor thumbCursor = activity.getContentResolver().query(
    //            MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
    //            imageThumbColumns, null, null, null);
    //    HashMap<Integer, String> imageThumbs = new HashMap<Integer, String>();
    //    if (null != thumbCursor && thumbCursor.moveToFirst()) {
    //        do {
    //            imageThumbs
    //                    .put(thumbCursor.getInt(0), thumbCursor.getString(1));
    //        } while (thumbCursor.moveToNext());
    //
    //        if (Integer.parseInt(Build.VERSION.SDK) < 14) {
    //            thumbCursor.close();
    //        }
    //    }
    //
    //    // Image Container
    //    Container imageContainer = new Container(ContentTree.IMAGE_ID,
    //            ContentTree.ROOT_ID, "Images", "GNaP MediaServer",
    //            new DIDLObject.Class("object.container"), 0);
    //    imageContainer.setRestricted(true);
    //    imageContainer.setWriteStatus(WriteStatus.NOT_WRITABLE);
    //    rootNode.getContainer().addContainer(imageContainer);
    //    rootNode.getContainer().setChildCount(
    //            rootNode.getContainer().getChildCount() + 1);
    //    ContentTree.addNode(ContentTree.IMAGE_ID, new ContentNode(
    //            ContentTree.IMAGE_ID, imageContainer));
    //
    //    String[] imageColumns = { MediaStore.Images.Media._ID,
    //            MediaStore.Images.Media.TITLE, MediaStore.Images.Media.DATA,
    //            MediaStore.Images.Media.MIME_TYPE,
    //            MediaStore.Images.Media.SIZE,
    //            MediaStore.Images.Media.DESCRIPTION };
    //    cursor = activity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
    //            imageColumns, null, null, MediaStore.Images.Media.DATA);
    //
    //    Container typeContainer = null;
    //    if (cursor.moveToFirst()) {
    //        do {
    //            int imageId = cursor.getInt(cursor
    //                    .getColumnIndex(MediaStore.Images.Media._ID));
    //            String id = ContentTree.IMAGE_PREFIX
    //                    + cursor.getInt(cursor
    //                    .getColumnIndex(MediaStore.Images.Media._ID));
    //            String title = cursor.getString(cursor
    //                    .getColumnIndexOrThrow(MediaStore.Images.Media.TITLE));
    //            String creator = "unkown";
    //            String filePath = cursor.getString(cursor
    //                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
    //            String mimeType = cursor
    //                    .getString(cursor
    //                            .getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE));
    //            long size = cursor.getLong(cursor
    //                    .getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));
    //
    //            String description = cursor
    //                    .getString(cursor
    //                            .getColumnIndexOrThrow(MediaStore.Images.Media.DESCRIPTION));
    //
    //            String url = "http://" +getAddress() + "/"
    //                    + filePath;
    //            Res res = new Res(new MimeType(mimeType.substring(0,
    //                    mimeType.indexOf('/')), mimeType.substring(mimeType
    //                    .indexOf('/') + 1)), size, url);
    //
    //            Container tempTypeContainer = null;
    //            if (null != typeContainer) {
    //                tempTypeContainer = typeContainer;
    //            }
    //            String fileName = null;
    //            int mImageContaierId = Integer.valueOf(ContentTree.IMAGE_ID) + 1;
    //
    //            if (TextUtils.isEmpty(fileName)) {
    //                fileName = FileUtils.getFoldName(filePath);
    //                typeContainer = new Container(
    //                        String.valueOf(mImageContaierId),
    //                        ContentTree.IMAGE_ID, fileName, "GNaP MediaServer",
    //                        new DIDLObject.Class("object.container"), 0);
    //                typeContainer.setRestricted(true);
    //                typeContainer.setWriteStatus(WriteStatus.NOT_WRITABLE);
    //
    //                tempTypeContainer = typeContainer;
    //                imageContainer.addContainer(tempTypeContainer);
    //                imageContainer
    //                        .setChildCount(imageContainer.getChildCount() + 1);
    //                ContentTree.addNode(String.valueOf(mImageContaierId),
    //                        new ContentNode(String.valueOf(mImageContaierId),
    //                                tempTypeContainer));
    //
    //                ImageItem imageItem = new ImageItem(id,
    //                        String.valueOf(mImageContaierId), title, creator,
    //                        res);
    //
    //                if (imageThumbs.containsKey(imageId)) {
    //                    String thumb = imageThumbs.get(imageId);
    //                    Log.i(LOGTAG, " image thumb:" + thumb);
    //                    // set albumArt Property
    //                    DIDLObject.Property albumArtURI = new DIDLObject.Property.UPNP.ALBUM_ART_URI(
    //                            URI.create("http://" + getAddress()
    //                                    + thumb));
    //                    DIDLObject.Property[] properties = { albumArtURI };
    //                    imageItem.addProperties(properties);
    //                }
    //                imageItem.setDescription(description);
    //
    //                tempTypeContainer.addItem(imageItem);
    //                tempTypeContainer.setChildCount(tempTypeContainer
    //                        .getChildCount() + 1);
    //                ContentTree.addNode(id, new ContentNode(id, imageItem,
    //                        filePath));
    //            } else {
    //                if (!fileName.equalsIgnoreCase(FileUtils
    //                        .getFoldName(filePath))) {
    //                    mImageContaierId++;
    //                    fileName = FileUtils.getFoldName(filePath);
    //
    //                    typeContainer = new Container(
    //                            String.valueOf(mImageContaierId),
    //                            ContentTree.IMAGE_ID, fileName,
    //                            "GNaP MediaServer", new DIDLObject.Class(
    //                            "object.container"), 0);
    //                    typeContainer.setRestricted(true);
    //                    typeContainer.setWriteStatus(WriteStatus.NOT_WRITABLE);
    //
    //                    tempTypeContainer = typeContainer;
    //                    imageContainer.addContainer(tempTypeContainer);
    //                    imageContainer.setChildCount(imageContainer
    //                            .getChildCount() + 1);
    //                    ContentTree.addNode(
    //                            String.valueOf(mImageContaierId),
    //                            new ContentNode(String
    //                                    .valueOf(mImageContaierId),
    //                                    tempTypeContainer));
    //
    //                    ImageItem imageItem = new ImageItem(id,
    //                            String.valueOf(mImageContaierId), title,
    //                            creator, res);
    //
    //                    if (imageThumbs.containsKey(imageId)) {
    //                        String thumb = imageThumbs.get(imageId);
    //                        Log.i(LOGTAG, " image thumb:" + thumb);
    //                        // set albumArt Property
    //                        DIDLObject.Property albumArtURI = new DIDLObject.Property.UPNP.ALBUM_ART_URI(
    //                                URI.create("http://"
    //                                        + getAddress() + thumb));
    //                        DIDLObject.Property[] properties = { albumArtURI };
    //                        imageItem.addProperties(properties);
    //                    }
    //                    imageItem.setDescription(description);
    //                    tempTypeContainer.addItem(imageItem);
    //                    tempTypeContainer.setChildCount(typeContainer
    //                            .getChildCount() + 1);
    //                    ContentTree.addNode(id, new ContentNode(id, imageItem,
    //                            filePath));
    //                } else {
    //                    ImageItem imageItem = new ImageItem(id,
    //                            String.valueOf(mImageContaierId), title,
    //                            creator, res);
    //
    //                    if (imageThumbs.containsKey(imageId)) {
    //                        String thumb = imageThumbs.get(imageId);
    //                        Log.i(LOGTAG, " image thumb:" + thumb);
    //                        // set albumArt Property
    //                        DIDLObject.Property albumArtURI = new DIDLObject.Property.UPNP.ALBUM_ART_URI(
    //                                URI.create("http://"
    //                                        + getAddress() + thumb));
    //                        DIDLObject.Property[] properties = { albumArtURI };
    //                        imageItem.addProperties(properties);
    //                    }
    //                    imageItem.setDescription(description);
    //                    tempTypeContainer.addItem(imageItem);
    //                    tempTypeContainer.setChildCount(typeContainer
    //                            .getChildCount() + 1);
    //                    ContentTree.addNode(id, new ContentNode(id, imageItem,
    //                            filePath));
    //                }
    //            }
    //
    //            // imageContainer.addItem(imageItem);
    //            // imageContainer
    //            // .setChildCount(imageContainer.getChildCount() + 1);
    //            // ContentTree.addNode(id,
    //            // new ContentNode(id, imageItem, filePath));
    //
    //            Log.v(LOGTAG, "added image item " + title + "from " + filePath);
    //        } while (cursor.moveToNext());
    //    }
    //    handler.sendEmptyMessage(1);
    //}

    public static void initMediaData(Activity activity) {
        rowsVideo.clear();
        List<RowObject> videoList = getVideoList(activity);
        if(videoList!=null) {
            rowsVideo.addAll(videoList);
        }

        rowsRadio.clear();
        List<RowObject> radioList = getRadioList(activity);
        if(radioList!=null) {
            rowsRadio.addAll(radioList);
        }

        rowsImage.clear();
        List<RowObject> imageList = getImageList(activity);
        if(imageList!=null) {
            rowsImage.addAll(imageList);
        }

        rowsNet.clear();
        List<RowObject> netList = getNetList(activity);
        if(netList!=null){
            rowsNet.addAll(netList);
        }


    }


    public static VideoItem buildVideoItem(RowObject rowObject) {
        String id = FILE_TYPE.VIDEO + rowObject.getString(MediaStore.Video.Media._ID);
        String title = rowObject.getString(MediaStore.Video.Media.DISPLAY_NAME);
        String creator = rowObject.getString(MediaStore.Video.Media.ARTIST);
        String filePath = rowObject.getString(MediaStore.Video.Media.DATA);
        String mimeType = rowObject.getString(MediaStore.Video.Media.MIME_TYPE);
        long size = rowObject.getLong(MediaStore.Video.Media.SIZE);
        long duration = rowObject.getLong(MediaStore.Video.Media.DURATION);
        String resolution = rowObject.getString(MediaStore.Video.Media.RESOLUTION);
        String description = rowObject.getString(MediaStore.Video.Media.DESCRIPTION);
        String adrress = "http:/" + getAddress() + "/" + id;
        L.i("============buildRadioItem===========" + adrress);
        Res res = new Res(new MimeType(mimeType.substring(0,
                mimeType.indexOf('/')), mimeType.substring(mimeType
                .indexOf('/') + 1)), size, adrress);

        res.setDuration(Utils.formatDuration(duration));
        res.setResolution(resolution);
        VideoItem videoItem = new VideoItem(id, "1",
                title, creator, res);
        videoItem.setDescription(description);
        return videoItem;
    }

    public static VideoItem buildNetItem(RowObject rowObject) {
        //String id = FILE_TYPE.VIDEO + rowObject.getString(MediaStore.Video.Media._ID);
        //String title = rowObject.getString(MediaStore.Video.Media.DISPLAY_NAME);
        //String creator = rowObject.getString(MediaStore.Video.Media.ARTIST);
        //String filePath = rowObject.getString(MediaStore.Video.Media.DATA);
        //String mimeType = rowObject.getString(MediaStore.Video.Media.MIME_TYPE);
        //long size = rowObject.getLong(MediaStore.Video.Media.SIZE);
        //long duration = rowObject.getLong(MediaStore.Video.Media.DURATION);
        //String resolution = rowObject.getString(MediaStore.Video.Media.RESOLUTION);
        //String description = rowObject.getString(MediaStore.Video.Media.DESCRIPTION);
        //String adrress = "http:/" + getAddress() + "/" + id;
        String name = rowObject.getString("name");
        Res res = new Res(new MimeType("",""), 0l, rowObject.getString("filePath"));

        res.setDuration(duration / (1000 * 60 * 60) + ":"
                + (duration % (1000 * 60 * 60)) / (1000 * 60) + ":"
                + (duration % (1000 * 60)) / 1000);

        VideoItem videoItem = new VideoItem( UUID.randomUUID().toString(), "1",
                name, name, res);
        return videoItem;
    }



    public static MusicTrack buildRadioItem(RowObject rowObject) {
        String id =FILE_TYPE.RADIO+ rowObject.getString(MediaStore.Audio.Media._ID);
        String title = rowObject.getString(MediaStore.Audio.Media.TITLE);
        String creator = rowObject.getString(MediaStore.Audio.Media.ARTIST);
        String filePath = rowObject.getString(MediaStore.Audio.Media.DATA);
        String mimeType = rowObject.getString(MediaStore.Audio.Media.MIME_TYPE);
        long size = rowObject.getLong(MediaStore.Audio.Media.SIZE);
        String album = rowObject.getString(MediaStore.Audio.Media.ALBUM);
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

    public static AudioBook buildPlaylistItem(LinkedList<RowObject> rows) {
        Res[] argRes = new Res[rows.size()];
        for (int i = 0; i < rows.size(); i++) {
            RowObject rowObject = rows.get(i);
            String id = rowObject.getString(MediaStore.Audio.Media._ID);
            String title = rowObject.getString(MediaStore.Audio.Media.TITLE);
            String creator = rowObject.getString(MediaStore.Audio.Media.ARTIST);
            String filePath = rowObject.getString(MediaStore.Audio.Media.DATA);
            String mimeType = rowObject.getString(MediaStore.Audio.Media.MIME_TYPE);
            long size = rowObject.getLong(MediaStore.Audio.Media.SIZE);
            String album = rowObject.getString(MediaStore.Audio.Media.ALBUM);
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


    public static ImageItem buildImageItem(RowObject rowObject) {
        String id = FILE_TYPE.IMAGE+ rowObject.getString(MediaStore.Images.Media._ID);
        String title = rowObject.getString(MediaStore.Images.Media.TITLE);
        String creator = "unkown";
        String mimeType = rowObject.getString(MediaStore.Images.Media.MIME_TYPE);
        long size = rowObject.getInteger(MediaStore.Images.Media.SIZE);

        String description = rowObject.getString(MediaStore.Images.Media.DESCRIPTION);

        String url = "http:/" + getAddress() + "/"
                + id;
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
    public static List<RowObject> getVideoList(Activity activity) {
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
        List<RowObject> rows = new LinkedList<>();
        if (cursor.moveToFirst()) {//判断数据表里有数据
            while (cursor.moveToNext()) {//遍历数据表中的数据
                RowObject row = new RowObject();
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
    public static List<RowObject> getRadioList(Activity activity) {
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
        List<RowObject> rows = new LinkedList<>();
        if (cursor.moveToFirst()) {//判断数据表里有数据
            while (cursor.moveToNext()) {//遍历数据表中的数据
                RowObject row = new RowObject();
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
    public static List<RowObject> getImageList(Activity activity) {
        String[] imageColumns = {MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DESCRIPTION};
        Cursor cursor = activity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                imageColumns, null, null, MediaStore.Images.Media.DATA);
        LinkedList<RowObject> rows = new LinkedList<>();
        if (cursor.moveToFirst()) {//判断数据表里有数据
            while (cursor.moveToNext()) {//遍历数据表中的数据
                RowObject row = new RowObject();
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


    public static List<RowObject> getNetList(Activity activity) {
        List<RowObject> url_list = SPUtils.getRows(FILE_TYPE.NET, "url_list");
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
                RowObject rowObject = rowsVideo.get(i);
                if (rowObject.containsValue(mediaId)) {
                    filePath = rowObject.getString("filePath");
                    i = rowsVideo.size();
                }
            }
        }else if (mediaId.startsWith(FILE_TYPE.RADIO)) {
            mediaId=mediaId.replace(FILE_TYPE.RADIO,"");
            for (int i = 0; i < rowsRadio.size(); i++) {
                RowObject rowObject = rowsRadio.get(i);
                if (rowObject.containsValue(mediaId)) {
                    filePath = rowObject.getString("filePath");
                    i = rowsRadio.size();
                }
            }
        }else if (mediaId.startsWith(FILE_TYPE.IMAGE)) {
            mediaId=mediaId.replace(FILE_TYPE.IMAGE,"");
            for (int i = 0; i < rowsImage.size(); i++) {
                RowObject rowObject = rowsImage.get(i);
                if (rowObject.containsValue(mediaId)) {
                    filePath = rowObject.getString("filePath");
                    i = rowsImage.size();
                }
            }
        }
        return filePath;
    }
}
