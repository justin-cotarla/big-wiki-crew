package org.wikipedia.page.chat;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.wikipedia.R;
import org.wikipedia.page.PageActivity;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ChatFragment extends DialogFragment {
    private ChatClient chatClient;
    private ArrayList<Message> messageList = new ArrayList();
    private ChatAdapter chatAdapter;

    private Unbinder unbinder;

    @BindView(R.id.chat_message_view) RecyclerView chatMessageView;
    @BindView(R.id.chat_send_btn) AppCompatImageButton sendButton;
    @BindView(R.id.chat_text_input) AppCompatEditText textInput;
    @BindView(R.id.chat_close_btn) AppCompatImageButton closeButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatClient = ((PageActivity)getActivity()).getChatClient();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        unbinder = ButterKnife.bind(this, view);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        chatAdapter = new ChatAdapter();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        chatMessageView.setLayoutManager(layoutManager);
        chatMessageView.setAdapter(chatAdapter);
        chatMessageView.setHasFixedSize(true);

        sendButton.setOnClickListener(listener -> {
            chatClient.writeMessage(textInput.getText().toString());
            textInput.setText("");
        });

        closeButton.setOnClickListener(listener -> {
            dismiss();
        });

        chatClient.subscribe(new MessageCallback());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        super.onResume();
        Window window = getDialog().getWindow();
        Point size = new Point();

        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);

        int width = size.x;
        int height = size.y;

        window.setLayout((int) (width * 0.90), (int) (height * 0.90));
        window.setGravity(Gravity.CENTER);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        unbinder = null;
        chatClient.unsubscribe();
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_feed, menu);
    }

    private class MessageCallback implements ChatClient.MessageCallback {
        public void messageReceived(Message message) {
            messageList.add(message);
            chatAdapter.notifyDataSetChanged();
            chatMessageView.smoothScrollToPosition(messageList.size() - 1);
        }
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

        @Override
        public int getItemViewType(int position) {
            String messageUser = messageList.get(position).getUser();
            String clientUser = chatClient.getUser();
            if (messageUser.equals(clientUser)) {
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
                            .inflate(R.layout.fragment_chat_message_received, parent, false);
                    return new ReceivedMessageViewHolder(receivedView);
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
                    ((TextView)receivedMessageViewHolder.receivedMessageView.findViewById(R.id.chat_message_received_username))
                            .setText(message.getUser());
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return messageList.size();
        }
    }
}
