package manigautam.app.fcmdatabaseexample;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
EditText name,email,contact,password;
Button register,display,search,delete,update;
TextView txt;
DatabaseReference dbreference;
GridView mygrid;
ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializing fcmdatabase
        mygrid=(GridView)findViewById(R.id.mygrid);
        adapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1);
        mygrid.setAdapter(adapter);

        dbreference=FirebaseDatabase.getInstance().getReference("StudentInfo");
        password=(EditText)findViewById(R.id.edtpassword);
       email=(EditText)findViewById(R.id.edtemail);
        txt=(TextView)findViewById(R.id.mytxt);
        update=(Button)findViewById(R.id.btnupdate);
        update.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
                String useremail=email.getText().toString();
                final String userpassword=password.getText().toString();
                dbreference.orderByChild("email").equalTo(useremail)
                    .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                            DataSnapshot ds=dataSnapshot.getChildren().iterator().next();
                            String nodekey="/"+ds.getKey();
                        HashMap<String,Object> param=new HashMap<String,Object>();
                        param.put("password",userpassword);
                             dbreference.child(nodekey).updateChildren(param);
                        Toast.makeText(MainActivity.this, "Record Updated successfuly", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

          }
      });

        delete=(Button)findViewById(R.id.btndelete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String useremail=email.getText().toString();
                dbreference.orderByChild("email").equalTo(useremail)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                DataSnapshot node=dataSnapshot.getChildren().iterator().next();
                                dbreference.child(node.getKey()).removeValue();
                                txt.setText("Record Deleted successfully");
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.v("display_error",databaseError.getMessage());
                                Toast.makeText(MainActivity.this,
                                        ""+databaseError.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        search=(Button)findViewById(R.id.btnsearch);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String useremail=email.getText().toString();
                dbreference.orderByChild("email").equalTo(useremail)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                adapter.clear();
                                for(DataSnapshot dsp:dataSnapshot.getChildren()){
                                    Log.v("data_found",dsp.getValue().toString());
                                    Toast.makeText(MainActivity.this,
                                            ""+dsp.getValue().toString(),
                                            Toast.LENGTH_SHORT).show();
                                    adapter.add("Name");
                                    adapter.add(dsp.child("name").getValue());
                                    adapter.add("Contact");
                                    adapter.add(dsp.child("phone").getValue());
                                    adapter.add("Email");
                                    adapter.add(dsp.child("email").getValue());

                                }
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.v("display_error",databaseError.getMessage());
                                Toast.makeText(MainActivity.this,
                                        ""+databaseError.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });



        //
        display=(Button)findViewById(R.id.btndisplay);
        display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               dbreference.orderByChild("email")
                   .addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {
                       adapter.clear();
                       for(DataSnapshot dsp:dataSnapshot.getChildren()){
                           Log.v("data_found",dsp.getValue().toString());
                           Toast.makeText(MainActivity.this,
                                   ""+dsp.getValue().toString(),
                                   Toast.LENGTH_SHORT).show();
                           adapter.add("Name");
                           adapter.add(dsp.child("name").getValue());
                           adapter.add("Contact");
                           adapter.add(dsp.child("phone").getValue());
                           adapter.add("Email");
                           adapter.add(dsp.child("email").getValue());

                       }
                       adapter.notifyDataSetChanged();
                   }

                   @Override
                   public void onCancelled(DatabaseError databaseError) {
                        Log.v("display_error",databaseError.getMessage());
                       Toast.makeText(MainActivity.this,
                               ""+databaseError.getMessage(),
                               Toast.LENGTH_SHORT).show();
                   }
               });


            }
        });




        name=(EditText)findViewById(R.id.edtname);

        contact=(EditText)findViewById(R.id.phone);

        register=(Button)findViewById(R.id.btnregister);
        register.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
           Student student=new Student();
           student.setEmail(email.getText().toString());
           student.setName(name.getText().toString());
           student.setPassword(password.getText().toString());
           student.setPhone(contact.getText().toString());

           //adding key to record
            String key=dbreference.push().getKey();
           dbreference.child(key).setValue(student)
                      .addOnCompleteListener(new OnCompleteListener<Void>() {
                          @Override
                          public void onComplete(@NonNull Task<Void> task) {
                          if(task.isSuccessful()){
                              txt.setText("User Registered successfully");
                          }
                          }
                      })
                       .addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               txt.setText(e.getMessage());
                           }
                       }) ;
           }
       });

    }
}
