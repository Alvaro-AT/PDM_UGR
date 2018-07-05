package atlc.mmaquiz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import atlc.mmaquiz.R;
import atlc.mmaquiz.model.Items;

/**
 * Created by Alvaro on 26/4/17.
 */

public class CustomListAdapter extends BaseAdapter
{
    private Context context;
    private LayoutInflater inflater;
    private List<Items> items;

    public CustomListAdapter (Context context, List<Items> items)
    {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount()
    {
        return items.size();
    }

    @Override
    public Object getItem(int i)
    {
        return items.get(i);
    }

    @Override
    public long getItemId(int i)
    {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        /*ViewHolder vh;

        if (inflater == null)
        {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (view == null)
        {
            view = inflater.inflate(R.layout.item_score, viewGroup, false);
            vh = new ViewHolder();
            vh.nombre = (TextView) view.findViewById(R.id.nombre);
            vh.puntuacion = (TextView) view.findViewById(R.id.puntuacion);
            view.setTag(vh);
        }
        else
        {
            vh = (ViewHolder) view.getTag();
        }

        final Items item = items.get(i);
        vh.nombre.setText(item.getNombre());
        vh.puntuacion.setText(item.getPuntuacion());

        return view;*/
        View v = view;
        if (v == null) {
            //Aquí cogemos nuestra vista personalizada para la listView
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.item_score, null);
        }

        Items cat = items.get(i);

        TextView title = (TextView) v.findViewById(R.id.nombre);
        title.setText(cat.getNombre());

        /*ImageView icon = (ImageView) v.findViewById(R.id.imageView);
        icon.setImageDrawable(cat.getIcon());*/
        TextView puntuacion = (TextView) v.findViewById(R.id.puntuacion);
        puntuacion.setText(cat.getPuntuacion());

        return v;
    }

    static class ViewHolder
    {
        TextView nombre;
        TextView puntuacion;
    }
}
