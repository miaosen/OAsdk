package com.osui.view;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-03-07  15:28
 * @Descrition
 */

public class TempFragment extends Fragment {

    public static TempFragment addHideFragment(Activity activity){
        return addHideFragment(activity, "tempFragment");
    }

    public static TempFragment addHideFragment(Activity activity,String tag){
        TempFragment   tempFragmentView=new TempFragment();
        FragmentTransaction fragmentTransaction = activity.getFragmentManager().beginTransaction();
        fragmentTransaction.add(tempFragmentView,tag);
        fragmentTransaction.commit();
        fragmentTransaction.hide(tempFragmentView);
        return tempFragmentView;
    }

    public TempFragment(){

    }

    OnActivityResultListener onActivityResultListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return new View(inflater.getContext());

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(onActivityResultListener!=null){
            onActivityResultListener.onActivityResult(requestCode, resultCode, data);
        }
    }

    public interface OnActivityResultListener{
        void onActivityResult(int requestCode, int resultCode, Intent data);
    }

    public OnActivityResultListener getOnActivityResultListener() {
        return onActivityResultListener;
    }

    public void setOnActivityResultListener(OnActivityResultListener onActivityResultListener) {
        this.onActivityResultListener = onActivityResultListener;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
