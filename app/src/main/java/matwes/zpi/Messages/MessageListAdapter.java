package matwes.zpi.Messages;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import matwes.zpi.Classes.Message;
import matwes.zpi.R;

/**
 * Created by mateu on 30.05.2017.
 */

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.Holder>
{
    private static final int MESSAGE_LEFT = 0;
    private static final int MESSAGE_RIGHT = 1;
    private long userId;

    private final ArrayList<Message> messages;

    MessageListAdapter(final ArrayList<Message> messages, long userId) {
        this.userId = userId;
        this.messages = messages;
    }

    @Override
    public MessageListAdapter.Holder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View itemView;
        if (viewType == MESSAGE_LEFT) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_my_message, parent, false);
        }
        return new Holder(itemView);
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        Message message = messages.get(position);
        holder.messageTextView.setText(message.getMessage());
        holder.messageInfo.setText(message.getTime());
        if(getItemViewType(position)==MESSAGE_LEFT)
            holder.messageAuthor.setText(message.getAuthor());
    }


    @Override
    public int getItemViewType(final int position) {
        boolean isLeftMessage = messages.get(position).getAuthorId()!=userId;
        if (isLeftMessage) {
            return MESSAGE_LEFT;
        } else {
            return MESSAGE_RIGHT;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        TextView messageTextView, messageInfo, messageAuthor;

        Holder(final View itemView)
        {
            super(itemView);
            messageTextView = (TextView) itemView.findViewById(R.id.message);
            messageInfo = (TextView) itemView.findViewById(R.id.messageInfo);
            messageAuthor = (TextView) itemView.findViewById(R.id.messageAuthor);
        }
    }
}
