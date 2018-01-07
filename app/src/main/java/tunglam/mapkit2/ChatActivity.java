package tunglam.mapkit2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import android.support.design.widget.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {


    // Constant
    private static final String TAG = "ChatActivity";

    // Views
    private EditText mNewMessage;
    private ImageButton mSendButton;
    private ImageButton mPhotoPickerButton;
    private RecyclerView mRecyclerView;
    private MessageAdapter mMessageAdapter;

    //PhotoPicker
    private static final int RC_PHOTO_PICKER = 1;

    // Firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference mMessagesReference;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    //Photo Storage
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mChatPhotosReference;

    // Properties
    private String mUsername = "";
    private List<Message> mMessageList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Bottom Navigation
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //Views init
        mNewMessage = findViewById(R.id.new_chat_text);
        mSendButton = findViewById(R.id.send_button);
        mPhotoPickerButton = findViewById(R.id.photoPickerButton);

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mMessagesReference = mDatabase.getReference("Messages");
        mCurrentUser = mAuth.getCurrentUser();
        //TODO: BUG: crash when load this
//        mFirebaseStorage = FirebaseStorage.getInstance();
//        mChatPhotosReference = mFirebaseStorage.getReference().child("chat_photos");

        // Properties
        mUsername = mCurrentUser.getDisplayName();

        mMessageList = new ArrayList<>();

        // Check if user already log in, if not,
        // change user to log in page (MainActivity)
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(ChatActivity.this, MainActivity.class));
            finish();
            return;
        }

        //Recycler View
        mRecyclerView = findViewById(R.id.chat_recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //TODO: Still having bug when loading Message
        mMessagesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    Log.d(TAG, "inside dataChange");
                    List<Message> tempMessages = new ArrayList<>();

                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                        Message message = postSnapShot.getValue(Message.class);
                        Log.d(TAG, "load Message: " + message.getText());
                        tempMessages.add(message);
                    }

                    mMessageList = tempMessages;

                    updateMessages(mMessageList);


                    // Notification
                    Intent intent = new Intent();
                    PendingIntent pendingIntent = PendingIntent.getActivity(ChatActivity.this, 0, intent, 0);
                    Notification noti = new Notification.Builder(ChatActivity.this)
                            .setTicker("MapKit - New Message in Chat Forum")
                            .setContentTitle("MapKit - New Message in Chat Forum")
                            .setContentText("You received 1 new message in Chat Forum. Check it now !")
                            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                            .setContentIntent(pendingIntent).getNotification();


                    noti.flags = Notification.FLAG_AUTO_CANCEL;
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(0, noti);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ChatActivity.this, "Failed to read data", Toast.LENGTH_SHORT).show();
            }
        });


        // Message implementation
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mNewMessage.getText().toString().equals("")) { // Message cannot be empty
                    Message message = new Message(mNewMessage.getText().toString(), mUsername, null);
                    mMessagesReference.push().setValue(message); // The messages is uploaded to database

                    // Delete the sent message in TextView
                    mNewMessage.setText("");
//                    Toast.makeText(ChatActivity.this,
//                            "Your message has been sent !", Toast.LENGTH_LONG).show();
                }
            }
        });

        // ImagePickerButton shows an image picker to upload a image for a message
        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/jpeg");
                i.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(i, "Complete action using"), RC_PHOTO_PICKER);
            }
        });



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();

            //get the reference to stored file at database
            StorageReference photoReference = mChatPhotosReference.child(imageUri.getLastPathSegment());

            //upload file to firebase
            photoReference.putFile(imageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Message message = new Message(null, mUsername, downloadUrl.toString());
                    mMessagesReference.push().setValue(message);
                    Toast.makeText(ChatActivity.this,
                            "Your Image Sent", Toast.LENGTH_LONG).show();

                }
            });
        }
    }


    private void updateMessages(List<Message> messages) {
        mMessageAdapter = new MessageAdapter(messages);
        mRecyclerView.setAdapter(mMessageAdapter);
    }

    // ViewHolder class for Recycler View
    public class MessageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Message mMessage;
        private TextView mLeftChat;
        private TextView mLeftSender;
//        private TextView mRightChat;
//        private TextView mRightSender;

        public MessageHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mLeftChat = itemView.findViewById(R.id.chat_left_textView);
            mLeftSender = itemView.findViewById(R.id.chat_sender_left_textView);
//            mRightChat = itemView.findViewById(R.id.chat_right_textView);
//            mRightSender = itemView.findViewById(R.id.chat_sender_right_textView);
        }

        public void bind(Message message) {
            mMessage = message;
            mLeftChat.setText(mMessage.getText());
            mLeftSender.setText(mMessage.getSender());
        }

        @Override
        public void onClick(View v) {

        }
    }


    // Adapter class for Recycler View
    public class MessageAdapter extends RecyclerView.Adapter<MessageHolder> {

        List<Message> mMessages;

        public MessageAdapter(List<Message> messages) {
            mMessages = messages;
        }

        @Override
        public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_row, parent, false);
            return new MessageHolder(view);
        }

        @Override
        public void onBindViewHolder(MessageHolder holder, int position) {
            Message message = mMessages.get(position);
            holder.bind(message);
        }

        @Override
        public int getItemCount() {
            return mMessages.size();
        }
    }

    //BOTTOM NAVIGATION BAR
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_account:
                    startActivity(new Intent(ChatActivity.this,DashBoard.class));
                    return true;
                case R.id.navigation_map:
                    startActivity(new Intent(ChatActivity.this,MapsActivity.class));
                    return true;
                case R.id.navigation_chat:
                    startActivity(new Intent(ChatActivity.this,ChatActivity.class));
                    return true;
            }
            return false;
        }
    };

    //
}
