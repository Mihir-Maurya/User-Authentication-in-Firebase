package com.example.myweather;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NotesActivity extends AppCompatActivity {
  FloatingActionButton mcreatenotesfab;
  private FirebaseAuth firebaseAuth;

  RecyclerView mrecyclerView;
  StaggeredGridLayoutManager staggeredGridLayoutManager;
  LinearLayoutManager linearLayoutManager;
  FirebaseUser firebaseUser;
  FirebaseFirestore firebaseFirestore;

  //for showing data from cloudfirestore to recyclerview
    FirestoreRecyclerAdapter<firebasemodel,NotesViewHolder> notesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        mcreatenotesfab = findViewById(R.id.createnotefab);
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();



        getSupportActionBar().setTitle("All Notes");

        mcreatenotesfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NotesActivity.this,CreateNote.class));
            }
        });

        Query query  = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").orderBy("title",Query.Direction.ASCENDING);

        //all data is store in allusernote variable
        FirestoreRecyclerOptions<firebasemodel> allusernotes = new FirestoreRecyclerOptions.Builder<firebasemodel>().setQuery(query,firebasemodel.class).build();

        notesAdapter = new FirestoreRecyclerAdapter<firebasemodel, NotesViewHolder>(allusernotes) {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            protected void onBindViewHolder(@NonNull NotesViewHolder notesViewHolder, int i, @NonNull firebasemodel firebasemodel) {
                ImageView popupbutton = notesViewHolder.itemView.findViewById(R.id.menupopbutton);


              int colorcode = getRandomColor();
              notesViewHolder.mnote.setBackgroundColor(notesViewHolder.itemView.getResources().getColor(colorcode,null));
                notesViewHolder.notetitle.setText(firebasemodel.getTitle());
                notesViewHolder.notecontent.setText(firebasemodel.getContent());
               String docId= notesAdapter.getSnapshots().getSnapshot(i).getId();

                notesViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override

                    public void onClick(View v) {
                        //we have to open note detail activity

                        Intent intent = new Intent(v.getContext(),notedetails.class);
                      intent.putExtra("title",firebasemodel.getTitle());
                      intent.putExtra("content",firebasemodel.getContent());
                      intent.putExtra("noteId",docId);
                        v.getContext().startActivity(intent);
//                        Toast.makeText(getApplicationContext(),"This is CLicked",Toast.LENGTH_SHORT).show();
                    }
                });

                popupbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(v.getContext(),v);
                        popupMenu.setGravity(Gravity.END);
                        popupMenu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                            Intent intent = new Intent(v.getContext(),editnotesactivity.class);
                                intent.putExtra("title",firebasemodel.getTitle());
                                intent.putExtra("content",firebasemodel.getContent());
                                intent.putExtra("noteId",docId);
                            v.getContext().startActivity(intent);
                                return false;
                            }
                        });

                        popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
//                                Toast.makeText(v.getContext(),"This note is deleted",Toast.LENGTH_SHORT).show();
                                DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(docId);
                                 documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                     @Override
                                     public void onSuccess(Void aVoid){
                                         Toast.makeText(v.getContext(),"This note is deleted",Toast.LENGTH_SHORT).show();
                                     }
                                 }).addOnFailureListener(new OnFailureListener() {
                                     @Override
                                     public void onFailure(@NonNull Exception e) {
                                         Toast.makeText(v.getContext(),"Failed to deleted",Toast.LENGTH_SHORT).show();
                                     }
                                 });
                                return false;
                            }
                        });
                        popupMenu.show();
                    }
                });




            }

            @NonNull
            @Override
            public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_layout,parent,false);

                return new NotesViewHolder(view);
            }
        };

        mrecyclerView = findViewById(R.id.recyclerview);
        mrecyclerView.setHasFixedSize(true);
//        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
//        mrecyclerView.setLayoutManager(staggeredGridLayoutManager);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mrecyclerView.setLayoutManager(linearLayoutManager);

        mrecyclerView.setAdapter(notesAdapter);
    }



    public class NotesViewHolder extends  RecyclerView.ViewHolder{
        private TextView notetitle,notecontent;
        LinearLayout mnote;
        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            notetitle = itemView.findViewById(R.id.notetitle);
            notecontent = itemView.findViewById(R.id.notecontent);
            mnote = itemView.findViewById(R.id.note);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(NotesActivity.this,MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        notesAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(notesAdapter != null){
            notesAdapter.stopListening();
        }
    }
    private int getRandomColor() {
        List<Integer> colorcode = new ArrayList<>();
        colorcode.add(R.color.green);
        colorcode.add(R.color.grey);
        colorcode.add(R.color.pink);
        colorcode.add(R.color.lightgreen);
        colorcode.add(R.color.skyblue);
        colorcode.add(R.color.color1);
        colorcode.add(R.color.color2);
        colorcode.add(R.color.color3);
        Random random = new Random();
        int number = random.nextInt(colorcode.size());
        return colorcode.get(number);
    }
}