package matwes.zpi.EventDetails;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import matwes.zpi.AsyncTaskCompleteListener;
import matwes.zpi.Classes.Member;
import matwes.zpi.Common;
import matwes.zpi.R;
import matwes.zpi.RequestAPI;

/**
 * Created by mateu on 03.05.2017.
 */

class MembersListAdapter extends RecyclerView.Adapter<MembersListAdapter.Holder> {
    private Context context;
    private ArrayList<Member> members;
    private boolean isOwner;
    private AsyncTaskCompleteListener<String> listener;

    private static final String URL = "https://zpiapi.herokuapp.com/members/";
    private static final String ACCEPTED = "{\"status\": \"ACCEPTED\"}";
    private static final String REJECTED = "{\"status\": \"REJECTED\"}";

    MembersListAdapter(Context context, ArrayList<Member> members, boolean isOwner,
                       AsyncTaskCompleteListener<String> listener) {
        this.context = context;
        this.members = members;
        this.isOwner = isOwner;
        this.listener = listener;
    }

    @Override
    public MembersListAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MembersListAdapter.Holder(LayoutInflater.from(context).inflate(R.layout.member_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final MembersListAdapter.Holder holder, int position) {


        if (members != null) {
            final Member member = members.get(position);
            if (member != null) {
                if (member.getStatus().equals("PENDING")) {
                    if (isOwner) {
                        holder.accept.setVisibility(View.VISIBLE);
                        holder.accept.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                System.out.println(ACCEPTED);
                                new RequestAPI(context, "PATCH", ACCEPTED, listener, true).execute(URL + member.getId());
                                holder.accept.setVisibility(View.GONE);
                                holder.reject.setVisibility(View.GONE);
                            }
                        });
                        holder.reject.setVisibility(View.VISIBLE);
                        holder.reject.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                System.out.println(REJECTED);
                                new RequestAPI(context, "PATCH", REJECTED, listener, true).execute(URL + member.getId());
                                holder.accept.setVisibility(View.GONE);
                                holder.reject.setVisibility(View.GONE);
                                holder.member.setTextColor(Color.RED);
                            }
                        });
                    }
                    else
                        holder.pending.setVisibility(View.VISIBLE);
                }
                else if(!member.getStatus().equals("ACCEPTED"))
                    holder.member.setTextColor(Color.RED);
                try {
                    holder.member.setText(member.getUser().getName());
                } catch (Exception ignored) {
                }
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAlert(context.getString(R.string.profile),
                            member.getUser().getName() + "\n" +
                                    Common.getPolishSex(member.getUser().getSex()) + ", " +
                                    getAge(member.getUser().getBirthday()) + "\n\n" +
                                    member.getUser().getDescription());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView member;
        Button accept, reject, pending;

        Holder(View itemView) {
            super(itemView);
            member = (TextView) itemView.findViewById(R.id.tvMember);
            accept = (Button) itemView.findViewById(R.id.btnMemberAccept);
            reject = (Button) itemView.findViewById(R.id.btnMemberReject);
            pending = (Button) itemView.findViewById(R.id.btnMemberPending);
        }
    }

    private void showAlert(String title, String message) {
        new AlertDialog
                .Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private String getAge(String birthday) {
        try {
            System.out.println(birthday);
            System.out.println(birthday.substring(0, 4));
            int year = Integer.parseInt(birthday.substring(0, 4));
            return (2017 - year) + " lat";
        } catch (Exception ex) {
            return "";
        }
    }
}