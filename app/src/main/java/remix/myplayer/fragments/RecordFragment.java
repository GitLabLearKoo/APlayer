package remix.myplayer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import remix.myplayer.R;

/**
 * Created by Remix on 2015/12/28.
 */
public class RecordFragment extends Fragment{
    private EditText mEdit;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.record,null);
//        mEdit = (EditText)rootView.findViewById(R.id.edit_record);
        return rootView;
    }

}

class EditText_1 extends EditText
{
    public EditText_1(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }
}