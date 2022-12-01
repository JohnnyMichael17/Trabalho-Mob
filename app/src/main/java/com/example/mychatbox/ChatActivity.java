package com.example.mychatbox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import org.w3c.dom.Text;

import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private GroupAdapter adapter;
    private User user;
    private EditText editChat;
    private User eu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        user = getIntent().getExtras().getParcelable("user");
        getSupportActionBar().setTitle(user.getName());

        RecyclerView recyclerView = findViewById(R.id.recycler_chat);
        editChat = findViewById(R.id.editChat);
        Button btnChat = findViewById(R.id.button_chat);

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        adapter = new GroupAdapter();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        FirebaseFirestore.getInstance().collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                eu = documentSnapshot.toObject(User.class);
                                buscarMensagens();
                            }
                        });

    }

    private void buscarMensagens() {
        if(eu != null){
            String fromId = eu.getId();
            String toId = user.getId();

            FirebaseFirestore.getInstance().collection("/conversations")
                    .document(fromId)
                    .collection(toId)
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            List<DocumentChange> documentChanges = value.getDocumentChanges();

                            if(documentChanges != null){
                                for (DocumentChange doc : documentChanges
                                     ) {
                                    if(doc.getType() == DocumentChange.Type.ADDED){
                                        Mensagem msg = doc.getDocument().toObject(Mensagem.class);
                                        adapter.add(new MessageItem(msg));
                                    }
                                }
                            }
                        }
                    });
        }
    }

    private void sendMessage() {
        String text = editChat.getText().toString();

        editChat.setText(null);

        String fromId = FirebaseAuth.getInstance().getUid();
        String toId = user.getId();
        long timeStamp = System.currentTimeMillis();

        Mensagem msg = new Mensagem();

        msg.setFromId(fromId);
        msg.setToId(toId);
        msg.setTimesStamp(timeStamp);
        msg.setTexto(text);

        if(!msg.getTexto().isEmpty()){
            FirebaseFirestore.getInstance().collection("/conversations")
                    .document(fromId)
                    .collection(toId)
                    .add(msg)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("test", documentReference.getId());

                            Contato contato = new Contato();
                            contato.setUid(toId);
                            contato.setPhotoUrl(user.getProfileUrl());
                            contato.setTimeStamp(msg.getTimesStamp());
                            contato.setLastMessage(msg.getTexto());

                            FirebaseFirestore.getInstance().collection("/last-messages")
                                    .document(fromId)
                                    .collection("contacts")
                                    .document(toId)
                                    .set(contato);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Erro", e.getMessage(), e);
                        }
                    });

            FirebaseFirestore.getInstance().collection("/conversations")
                    .document(toId)
                    .collection(fromId)
                    .add(msg)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("test", documentReference.getId());

                            Contato contato = new Contato();
                            contato.setUid(toId);
                            contato.setUsername(user.getName());
                            contato.setPhotoUrl(user.getProfileUrl());
                            contato.setTimeStamp(msg.getTimesStamp());
                            contato.setLastMessage(msg.getTexto());

                            FirebaseFirestore.getInstance().collection("/last-messages")
                                    .document(toId)
                                    .collection("contacts")
                                    .document(fromId)
                                    .set(contato);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Erro", e.getMessage(), e);
                        }
                    });
        }
    }

    private class MessageItem extends Item<ViewHolder>{

        private final Mensagem message;

        private MessageItem(Mensagem message) {
            this.message = message;
        }


        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {
            TextView txtChat = viewHolder.itemView.findViewById(R.id.textChat);
            ImageView imgMsg = viewHolder.itemView.findViewById(R.id.img_user_chat);

            txtChat.setText(message.getTexto());

            Picasso.get().load(user.getProfileUrl())
                    .into(imgMsg);
        }

        @Override
        public int getLayout() {
            return message.getFromId().equals(FirebaseAuth.getInstance().getUid())
                    ? R.layout.item_from_message : R.layout.item_to_message;
        }
    }
}