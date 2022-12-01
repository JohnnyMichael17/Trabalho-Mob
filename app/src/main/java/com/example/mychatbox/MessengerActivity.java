package com.example.mychatbox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.Group;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import org.w3c.dom.Document;

import java.util.List;
import java.util.concurrent.RecursiveAction;

public class MessengerActivity extends AppCompatActivity {

    private GroupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);

        RecyclerView recyclerView = findViewById(R.id.recycler_contact);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new GroupAdapter();
        recyclerView.setAdapter(adapter);

        verificarAutenticacao();

        buscarLastMessages();
    }

    private void buscarLastMessages(){
        String id = FirebaseAuth.getInstance().getUid();

        FirebaseFirestore.getInstance().collection("/last-messages")
                .document(id)
                .collection("contatos")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        List<DocumentChange> documentChanges = value.getDocumentChanges();

                        if(documentChanges != null){
                            for ( DocumentChange doc: documentChanges
                                 ) {
                                if(doc.getType() == DocumentChange.Type.ADDED){
                                    Contato contato = doc.getDocument().toObject(Contato.class);
                                    adapter.add(new ContatoItem(contato));
                                }
                            }
                        }
                    }
                });
    }

    private void verificarAutenticacao(){
        if(FirebaseAuth.getInstance().getUid() == null){
            Intent intent = new Intent(MessengerActivity.this, MainActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.contatos:
                Intent intent = new Intent(MessengerActivity.this, ContatosActivity.class);
                startActivity(intent);
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                verificarAutenticacao();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class ContatoItem extends Item<ViewHolder>{

        private final Contato contato;

        private ContatoItem(Contato contato){
            this.contato = contato;
        }

        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {
            TextView textView = viewHolder.itemView.findViewById(R.id.textView);
            TextView textView2 = viewHolder.itemView.findViewById(R.id.textView2);
            ImageView imgView = viewHolder.itemView.findViewById(R.id.imageView);

            textView.setText(contato.getUsername());
            textView2.setText(contato.getUsername());
            Picasso.get()
                    .load(contato.getPhotoUrl())
                    .into(imgView);
        }

        @Override
        public int getLayout() {
            return R.layout.item_user_msg;
        }
    }
}