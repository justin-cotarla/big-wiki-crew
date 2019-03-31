package org.wikipedia.page.chat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.wikipedia.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ChatFragment extends DialogFragment {
    private final static String CHAT_CLIENT_KEY_KEY = "chat_client";

    private ChatClient chatClient;
    private ArrayList<Message> messageList = new ArrayList();

    private Unbinder unbinder;

    @BindView(R.id.chat_message_view) RecyclerView chatMessageView;

    public static ChatFragment newInstance(ChatClient chatClient) {
        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(CHAT_CLIENT_KEY_KEY, chatClient);
        chatFragment.setArguments(bundle);

        return chatFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        unbinder = ButterKnife.bind(this, view);

        chatMessageView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatClient = (ChatClient) getArguments().getSerializable(CHAT_CLIENT_KEY_KEY);
        chatMessageView.setAdapter(new ChatAdapter());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
        params.height = ConstraintLayout.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        unbinder = null;
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_feed, menu);
    }

    private class ChatAdapter extends RecyclerView.Adapter {
        private static final int SENT_TYPE = 0;
        private static final int RECEIVED_TYPE = 1;

        class SentMessageViewHolder extends RecyclerView.ViewHolder {
            public View sentMessageView;
            public SentMessageViewHolder(View sentMessageView) {
                super(sentMessageView);
                this.sentMessageView = sentMessageView;
            }
        }

        class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
            public View receivedMessageView;
            public ReceivedMessageViewHolder(View receivedMessageView) {
                super(receivedMessageView);
                this.receivedMessageView = receivedMessageView;
            }
        }

        @NonNull
        @Override
        public int getItemViewType(int position) {
            if (messageList.get(position).getUser() == chatClient.getUser()) {
                return SENT_TYPE;
            }
            return RECEIVED_TYPE;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case SENT_TYPE:
                    View sentView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.fragment_chat_message_sent, parent, false);
                    return new SentMessageViewHolder(sentView);

                case RECEIVED_TYPE:
                    View receivedView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.fragment_chat_message_sent, parent, false);
                    return new SentMessageViewHolder(receivedView);
                default:
                    // Should not happen
                    return null;
            }
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            Message message = messageList.get(position);
            switch (holder.getItemViewType()) {
                case SENT_TYPE:
                    SentMessageViewHolder sentMessageViewHolder = (SentMessageViewHolder)holder;
                    ((TextView)sentMessageViewHolder.sentMessageView.findViewById(R.id.chat_message_sent_text))
                            .setText(message.getMessage());
                    break;

                case RECEIVED_TYPE:
                    ReceivedMessageViewHolder receivedMessageViewHolder = (ReceivedMessageViewHolder)holder;
                    ((TextView)receivedMessageViewHolder.receivedMessageView.findViewById(R.id.chat_message_received_text))
                            .setText(message.getMessage());
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return messageList.size();
        }
    }
}
