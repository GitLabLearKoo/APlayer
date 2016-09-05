package remix.myplayer.observer;

import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;

import remix.myplayer.model.MP3Item;
import remix.myplayer.util.Constants;
import remix.myplayer.util.DBUtil;
import remix.myplayer.util.Global;
import remix.myplayer.util.XmlUtil;

/**
 * Created by taeja on 16-3-30.
 */
public class MediaStoreObserver extends ContentObserver {
    private Handler mHandler;
    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public MediaStoreObserver(Handler handler) {
        super(handler);
        mHandler = handler;
    }

    @Override
    public void onChange(boolean selfChange) {
        Log.d("ThreadId","id in observer: " + Thread.currentThread().getId());
        if(!selfChange){
            Global.mAllSongList = DBUtil.getAllSongsId();

            mHandler.sendEmptyMessage(Constants.UPDATE_FOLDER);
            //检查正在播放列表中是否有歌曲删除
            new Thread(){
                @Override
                public void run() {
                    try {
                        boolean needupdate = false;
                        if(Global.mPlayingList != null){
//                            for(int i = Global.mPlayingList.size() - 1; i >= 0 ; i--){
//                                MP3Item temp = DBUtil.getMP3InfoById(Global.mPlayingList.get(i));
//                                if(temp == null) {
//                                    Global.mPlayingList.remove(i);
//                                    needupdate = true;
//                                }
//                            }
                            if(needupdate){
                                XmlUtil.updatePlayingList();
                                mHandler.sendEmptyMessage(Constants.UPDATE_PLAYINGLIST);
                            }
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();

            //检查播放列表中是否有歌曲删除
//            new Thread(){
//                @Override
//                public void run() {
//                    try {
//                        Iterator it = PlayListActivity.getPlayList().keySet().iterator();
//                        ArrayList<PlayListItem> list = new ArrayList<>();
//
//                        boolean needupdate = false;
//                        while (it.hasNext()){
//                            list = PlayListActivity.getPlayList().get(it.next());
//                            if(list != null){
//                                for(int i = list.size() - 1 ; i >= 0  ; i--){
//                                    MP3Item temp = DBUtil.getMP3InfoById(list.get(i).getId());
//                                    if(temp == null || temp.equals(new MP3Item())){
//                                        list.remove(i);
//                                        needupdate = true;
//                                    }
//                                }
//                            }
//                        }
//                        if(needupdate){
//                            XmlUtil.updatePlaylist();
//                            mHandler.sendEmptyMessage(Constants.UPDATE_PLAYLIST);
//                        }
//
//                    } catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }
//            }.start();
        }
    }

}
