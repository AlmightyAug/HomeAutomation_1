package fyp.ntu.scse.homeautomation.controller;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import.android.view.ViewGroup;
import.android.widget.BaseAdapter;
import.android.widget.ProgressBar;
import.android.widget.TextView import fyp.ntu.scse.homeautomation.model.ProgramInfo;

public class Bluetooth_Program_Adapter extends BaseAdapter{

    private List<programInfo> mList;
    private final LayoutInflater mLayoutInflater;

    public Bluetooth_Program_Adapter(Context context){
        this(context, newArrayList< ProgramInfo >());

    }

    public Bluetooth_Program_Adapter(Context context, List<ProgramInfo> list){
        mList = list;
        mLayoutInflater = LayoutInflater.from(context);

    }

    public boolean add (ProgramInfo programInfo) {
        boolean result = mList.add(programInfo);
        notifyDataSetChanged();
        return result;
    }

    public boolean remove(String address) {
        ProgramInfo programInfo = getItem(address);
        if(programInfo != null){
            mList.remove(programInfo);
            notifyDataSetChanged();

        }

        return false;
    }
}

/*Start programming all devices*/

    public void startProgramming(byte[] mImage, int distance){
        for(ProgramInfo programInfo: mList){
            programInfo.startProgramming(mImage, distance, mList.size());

        }
    }

/*Pause programming all devices*/

    public void stopProgramming(){
        for (ProgramInfo programInfo : mList){
            programInfo.stopProgramming();
            if(!programInfo.isComplete()){
                programInfo.disconnect();
            }
        }
    }

    public boolean isComplete(){
        for (ProgramInfo programInfo : mList){
            if(!programInfo.isComplete()){
                return false;
            }
        }
    }

    @Override
    public int getCount(){
        return mList.size();

    }

    @Override
    public ProgramInfo getItem(int position){
        return mList.get(position);

    }

    public ProgramInfo getItem(String address){
        for(ProgramInfo programInfo : mList){
            if(programInfo.equals(address)){
                return programInfo;
            }
        }

        return null;
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder viewHolder;

        if(convertView == null){

            convertView = mLayoutInflater.inflate(R.layout.programInfo_listitem, parent, false);
            viewHolder = new viewHolder(convertView);
            convertView.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
    }