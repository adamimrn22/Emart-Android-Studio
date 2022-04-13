package com.example.emart.ui.profile;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.emart.R;
import com.example.emart.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

public class ProfileFragment extends Fragment {

    Button update;
    EditText nameET, emailET, passwordET, curEmail, curPassword;

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseUser firebaseUser;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

       getUserInfo();

        nameET = root.findViewById(R.id.profile_name);
        emailET = root.findViewById(R.id.profile_email);
        passwordET = root.findViewById(R.id.profile_password);
        update = root.findViewById(R.id.update);

        curEmail = root.findViewById(R.id.email_login);
        curPassword = root.findViewById(R.id.password_login);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

        return root;
    }

    public  void updateProfile(){
        String name = nameET.getText().toString();
        reference.child("Users").child(firebaseUser.getUid()).child("name").setValue(name);

        String email = emailET.getText().toString();
        reference.child("Users").child(firebaseUser.getUid()).child("email").setValue(email);

        String password = passwordET.getText().toString();
        reference.child("Users").child(firebaseUser.getUid()).child("password").setValue(password);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Get auth credentials from the user for re-authentication
        AuthCredential credential = EmailAuthProvider
                .getCredential(String.valueOf(curEmail), String.valueOf(curPassword)); // Current Login Credentials \\
        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Now change your email address \\
                        //----------------Code for Changing Email Address----------\\
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.updateEmail(email)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            updatePassword();
                                            Toast.makeText(getContext(), "Updated", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        //----------------------------------------------------------\\
                    }
                });

    }

    private void updatePassword() {

        String password = passwordET.getText().toString();
        reference.child("Users").child(firebaseUser.getUid()).child("password").setValue(password);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Get auth credentials from the user for re-authentication
        AuthCredential credential = EmailAuthProvider
                .getCredential(String.valueOf(curEmail), String.valueOf(curPassword)); // Current Login Credentials \\
        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Now change your email address \\
                        //----------------Code for Changing Email Address----------\\
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.updatePassword(password)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                        }
                                    }
                                });
                        //----------------------------------------------------------\\
                    }
                });

    }

    public void getUserInfo(){
        reference.child("Users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue().toString();
                String email = snapshot.child("email").getValue().toString();
                String password = snapshot.child("password").getValue().toString();

                nameET.setText(name);
                emailET.setText(email);
                passwordET.setText(password);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}

