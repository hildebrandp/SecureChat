package items;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.pascal.securechat.R;
import java.util.List;


public class contactListViewAdapter extends ArrayAdapter<contactItem> {

    Context context;

    public contactListViewAdapter(Context context, int resourceId,List<contactItem> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        TextView txtName;
        TextView txtEmail;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        contactItem rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.contact_item, null);
            holder = new ViewHolder();
            holder.txtName = (TextView) convertView.findViewById(R.id.txtitemusername);
            holder.txtEmail = (TextView) convertView.findViewById(R.id.txtitemuseremail);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.txtName.setText(rowItem.getName());
        holder.txtEmail.setText(rowItem.getemail());

        return convertView;
    }
}
