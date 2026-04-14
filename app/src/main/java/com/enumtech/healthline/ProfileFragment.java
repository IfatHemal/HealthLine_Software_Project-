package com.enumtech.healthline;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;


public class ProfileFragment extends Fragment {

    TextView  tvname, tvrole, tvemail;
    LinearLayout logout, editprofile;
    String userid;
    CardView editcard;
    Button btncancel, btnupdate, savePhoto;
    EditText editname,editemail;
    ShapeableImageView profileimage;
    ImageView editimage;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View profileview = inflater.inflate(R.layout.fragment_profile, container, false);

        editprofile = profileview.findViewById(R.id.editprofile);
        logout = profileview.findViewById(R.id.logout);
        tvname = profileview.findViewById(R.id.tvname);
        tvrole = profileview.findViewById(R.id.tvrole);
        tvemail = profileview.findViewById(R.id.tvemail);
        editcard = profileview.findViewById(R.id.editcard);
        editname = profileview.findViewById(R.id.editname);
        editemail = profileview.findViewById(R.id.editemail);
        btncancel = profileview.findViewById(R.id.btncancel);
        btnupdate = profileview.findViewById(R.id.btnupdate);
        savePhoto = profileview.findViewById(R.id.savePhoto);
        profileimage = profileview.findViewById(R.id.profileimage);
        editimage = profileview.findViewById(R.id.editimage);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("myApp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String name = (sharedPreferences.getString("name",""));
        String email = (sharedPreferences.getString("email",""));
        String role = (sharedPreferences.getString("role",""));
        String image = sharedPreferences.getString("image","");
        userid = sharedPreferences.getString("id","");
         role = role.substring(0,1).toUpperCase()+role.substring(1).toLowerCase();

        tvname.setText(name);
        tvemail.setText(email);
        tvrole.setText(role);

        if (image != null && !image.isEmpty() && !image.equals("null")){

            Picasso.get()
                    .load(image)
                    .placeholder(R.drawable.default_profile_picture)
                    .error(R.drawable.default_profile_picture)
                    .into(profileimage);
        }


        ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                if(result.getResultCode()== Activity.RESULT_OK){
                    Intent intent = result.getData();
                    Uri uri = intent.getData();

                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(),uri);
                        profileimage.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                else {
                    Toast.makeText(getActivity(),"No image selected",Toast.LENGTH_LONG).show();
                }
            }
        });


        editimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(getActivity())
                        .crop()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .createIntent(new Function1<Intent, Unit>() {
                            @Override
                            public Unit invoke(Intent intent) {

                                imagePickerLauncher.launch(intent);

                                return null;
                            }
                        });
            }
        });







        savePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BitmapDrawable bitmapDrawable = (BitmapDrawable) profileimage.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, outputStream);

                byte[] imageBytes = outputStream.toByteArray();
                String image64 = Base64.encodeToString(imageBytes,Base64.DEFAULT);

                StringRequest(image64);
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(getActivity())
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Logout",((dialog, which) ->{
                            editor.clear();
                            editor.apply();
                            startActivity(new Intent(getActivity(),MainActivity.class));
                            getActivity().finish();
                            } ))
                        .setNegativeButton("No",(dialog, which) -> {
                            dialog.dismiss();
                            } )
                        .show();

            }
        });


        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editcard.setVisibility(View.VISIBLE);
                editname.setText(sharedPreferences.getString("name",""));
                editemail.setText(sharedPreferences.getString("email",""));
                btncancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editcard.setVisibility(View.GONE);
                    }
                });

                btnupdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String updatename = editname.getText().toString();
                        String updateemail = editemail.getText().toString();
                        if (updateemail.isEmpty() || updateemail.isEmpty()) {
                            Toast.makeText(getActivity(), "Name or email can't be null.", Toast.LENGTH_LONG).show();
                        } else {
                            String url = "https://ifathemalapp.com/apps/healthline/updateprofile.php";
                            updateProfileRequest(url, updatename, updateemail, userid);
                        }
                    }
                });





            }
        });


        return profileview;
    }

    public void updateProfileRequest(String url,String name, String email ,String userid) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

               if(s.contains("Updated successfully")){
                    Toast.makeText(getActivity(),"Updated Successfully",Toast.LENGTH_LONG).show();
                    tvname.setText(name);
                    tvemail.setText(email);

                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("myApp", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("name", name);
                    editor.putString("email", email);
                    editor.apply();


                }
                else {
                    Toast.makeText(getActivity(),"Can't update, there is something wrong",Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getActivity(),"Error: ",Toast.LENGTH_LONG).show();
                if(volleyError.networkResponse != null){
                    Log.e("VOLLEY_ERROR", new String(volleyError.networkResponse.data));
                } else {
                    Log.e("VOLLEY_ERROR", volleyError.toString());
                }

            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> myMap = new HashMap<String,String>();
                myMap.put("name",name);
                myMap.put("email",email);
                myMap.put("id",userid);


                return myMap;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(stringRequest);

    }



    public void StringRequest(String image64) {



        RequestQueue queue = Volley.newRequestQueue(getActivity());

        String url = "https://ifathemalapp.com/apps/healthline/file.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            if(obj.getString("status").equals("updated")){
                                String image = obj.getString("image");
                                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("myApp", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("image",image);
                                editor.apply();

                                if (image != null && !image.isEmpty() && !image.equals("null")){

                                    Picasso.get()
                                            .load(image)
                                            .placeholder(R.drawable.default_profile_picture)
                                            .error(R.drawable.default_profile_picture)
                                            .into(profileimage);
                                }
                                Toast.makeText(getActivity(),"Image Updated Successfully",Toast.LENGTH_LONG).show();
                            }
                            else if(response.contains("Image upload failed")){
                                Toast.makeText(getActivity(),"Failed in sql command",Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(getActivity(),"Image upload failed",Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }



                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getActivity(),"Error: ",Toast.LENGTH_LONG).show();
                if(error.networkResponse != null){
                    Log.e("VOLLEY_ERROR", new String(error.networkResponse.data));
                } else {
                    Log.e("VOLLEY_ERROR", error.toString());
                }

            }
        }) {


            @Nullable
            @Override
            protected Map<String,String > getParams() {

                Map<String ,String> myMap = new HashMap<String ,String>();
                myMap.put("image",image64);
                myMap.put("id",userid);

                return myMap;

            }

        };



        queue.add(stringRequest);


    }

}