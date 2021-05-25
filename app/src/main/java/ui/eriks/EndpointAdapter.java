package ui.eriks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import api.eriks.ConnectionServiceImpl;


public class EndpointAdapter extends RecyclerView.Adapter<EndpointAdapter.EndpointHolder> {
    private final List<ConnectionServiceImpl.Endpoint> endpoints;
    private final LayoutInflater inflater;
    private View.OnClickListener onClickListener;

    public EndpointAdapter(Context context, List<ConnectionServiceImpl.Endpoint> endpoints) {
        inflater = LayoutInflater.from(context);
        this.endpoints = endpoints;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void addEndpoint(ConnectionServiceImpl.Endpoint endpoint) {
        endpoints.add(endpoint);
        notifyDataSetChanged();
    }

    public void remove(ConnectionServiceImpl.Endpoint endpoint) {
        for (ConnectionServiceImpl.Endpoint e : endpoints) {
            if (e.getId().equals(endpoint.getId())) {
                endpoints.remove(e);
                break;
            }
        }
        notifyDataSetChanged();
    }

    public void notifyUnreadCount(ConnectionServiceImpl.Endpoint endpoint) {
        int count = 0;
        for (ConnectionServiceImpl.Endpoint e : endpoints) {
            if (e.equals(endpoint)) {
                count = e.getUnreadCount() + 1;
                endpoints.remove(e);
                break;
            }
        }
        endpoint.setUnreadCount(count);
        endpoints.add(0, endpoint);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EndpointHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EndpointHolder(inflater.inflate(R.layout.client_row_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull EndpointHolder holder, int position) {
        holder.bind(endpoints.get(position));
    }

    @Override
    public int getItemCount() {
        return endpoints.size();
    }

    public class EndpointHolder extends RecyclerView.ViewHolder {
        private final TextView endpointName;
        private final TextView endpointID;
        private final TextView unread;

        public EndpointHolder(View itemView) {
            super(itemView);
            endpointID = itemView.findViewById(R.id.txtEndpoint);
            endpointName = itemView.findViewById(R.id.txtName);
            unread = itemView.findViewById(R.id.txtUnreadCount);
        }

        public void bind(ConnectionServiceImpl.Endpoint endpoint) {
            endpointName.setText(endpoint.getName());
            endpointID.setText(endpoint.getId());
            unread.setVisibility(View.GONE);
            if (endpoint.getUnreadCount() != 0) {
                unread.setVisibility(View.VISIBLE);
                unread.setText(endpoint.getUnreadCount());
            }
            itemView.setTag(endpoint);
            itemView.setOnClickListener(onClickListener);
        }
    }
}
