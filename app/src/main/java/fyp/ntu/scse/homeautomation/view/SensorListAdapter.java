package fyp.ntu.scse.homeautomation.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import fyp.ntu.scse.homeautomation.R;
import fyp.ntu.scse.homeautomation.model.SensorTag;


public class SensorListAdapter extends RecyclerView.Adapter<SensorListAdapter.ViewHolder> {

    private List<SensorTag> sensorTag;
    private Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView mySensorTextView;
        private Button mySensorButton;

        public ViewHolder(View v) {
            super(v);
            mySensorTextView = (TextView) v.findViewById(R.id.sensor_textView);
            mySensorButton = (Button) v.findViewById(R.id.settings_Button);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public SensorListAdapter(List<SensorTag> sensorTag, Context context) {
        this.context = context;
        this.sensorTag = sensorTag;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return sensorTag.size();
    }
    // Create new views (invoked by the layout manager)
    @Override
    public SensorListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sensor_cardview, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mySensorTextView.setText("a");

        holder.mySensorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent displayResult = new Intent(context, DisplayResultActivity.class);
//                displayResult.putExtra("filePath", history.getFilePath());
//                displayResult.putExtra("result", history.getResult());
//                context.startActivity(displayResult);
            }
        });
    }


}
