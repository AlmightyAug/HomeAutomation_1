package fyp.ntu.scse.homeautomation.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import fyp.ntu.scse.homeautomation.R;
import fyp.ntu.scse.homeautomation.controller.BtDeviceManager;
import fyp.ntu.scse.homeautomation.model.SensorTag;
import fyp.ntu.scse.homeautomation.model.ti.Sensor;

public class HomeView extends AppCompatActivity{
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<SensorTag> sensorTagList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mRecyclerView = (RecyclerView) findViewById(R.id.sensor_RecyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        sensorTagList = new ArrayList<>();
        for (SensorTag sensorTag : BtDeviceManager.getInstance().getDeviceList())
        {
            sensorTagList.add(sensorTag);
        }
        mAdapter = new SensorListAdapter(sensorTagList, this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }
}
