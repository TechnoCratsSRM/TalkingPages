package Adapter;
/*
Talking Pages
Free Audiobook App
Created By: Soumya Chowdhury
            Makineedi Sai Harsh
            Nagani Vrudant Gopalbhai
            Mukul Bhambari
 */
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recycleview.MainActivity;
import com.example.recycleview.MusicPlayer;
import com.example.recycleview.R;
import com.example.recycleview.ad;

import java.util.ArrayList;
import java.util.List;

import Model.ListItem;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<ListItem> listitems;
    private List<ListItem> listItemsfull;
    private List<ListItem> listFixed;

    public MyAdapter(Context context, List listitem)
    {
        this.context=context;
        this.listitems=listitem;
        listItemsfull=new ArrayList<>(listitem);
        listFixed = new ArrayList<>(listitem);
    }



    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder, int position) {


            ListItem item = listitems.get(position);
            holder.title.setText(item.getName());
            holder.description.setText(item.getDescription());


    }

    @Override
    public int getItemCount() {
        return listitems.size();
    }

    @Override
    public Filter getFilter() {

        return listFilter;
    }

    private Filter listFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ListItem> fiteredList=new ArrayList<>();
            if(constraint==null||constraint.length()==0)
            {
                fiteredList.addAll(listItemsfull);
            }
            else
            {
                String filterPatten = constraint.toString().toLowerCase().trim();
                ListItem i= new ListItem("Search Results","","","");
                fiteredList.add(i);

                for(ListItem item: listItemsfull)
                {
                    if(item.getName().toLowerCase().contains(filterPatten)||item.getDescription().toLowerCase().contains(filterPatten)||item.getSeries().toLowerCase().contains(filterPatten))
                    {
                        fiteredList.add(item);
                    }
                }
            }

            FilterResults results=new FilterResults();
            results.values = fiteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            listitems.clear();;
            listitems.addAll((List)results.values);
            notifyDataSetChanged();

        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView title;
        public TextView description;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            title = itemView.findViewById(R.id.title);
            description =itemView.findViewById(R.id.description);
        }

        @Override
        public void onClick(View v) {
            MainActivity.k++;
            if(MainActivity.k==1){
            int pos = getAdapterPosition();
            ListItem item = listitems.get(pos);
            //Toast.makeText(context, item.getName(),Toast.LENGTH_LONG).show();
            if(item.getName()!="Search Results") {
                Intent next = new Intent(context, ad.class);
                next.putExtra("title", item.getName());
                next.putExtra("author", item.getDescription());
                next.putExtra("series", item.getSeries());
                next.putExtra("genre", item.getGe());
                context.startActivity(next);
            }
            }
        }
    }

    public void itm(String s)
    {
        List<ListItem> p = new ArrayList<>();
        if(s.equals("all"))
        {
            p.addAll(listFixed);
        }
        else
        {
            for(ListItem i : listFixed)
            {
                if(i.getGe().toLowerCase().contains(s))
                {
                    p.add(i);
                }
            }
        }
        listitems.clear();
        listItemsfull.clear();
        listItemsfull.addAll(p);
        listitems.addAll(p);
        notifyDataSetChanged();
    }
}
