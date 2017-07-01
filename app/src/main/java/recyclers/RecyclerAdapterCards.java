package recyclers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mechanic.appmechanic.R;

import java.util.List;

/**
 * Created by valen on 12/12/2016.
 */

public class RecyclerAdapterCards extends RecyclerView.Adapter<RecyclerAdapterCards.RecyclerViewHolder>
{
    private List<RecyclerAdapter> list;
    private RecyclerViewOnClickListener recyclerViewOnClickListener;

    public RecyclerAdapterCards(List<RecyclerAdapter> list, RecyclerViewOnClickListener recyclerViewOnClickListener) {
        this.list = list;
        this.recyclerViewOnClickListener = recyclerViewOnClickListener;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        return new RecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position)
    {
        holder.tip.setText(list.get(position).getTipName());
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        private TextView tip;

        public RecyclerViewHolder(View v)
        {
            super(v);
            tip = (TextView)v.findViewById(R.id.namesTips);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            recyclerViewOnClickListener.onClick(v,getAdapterPosition());
        }
    }
}
