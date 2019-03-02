package com.example.student_carpooling;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.student_carpooling.findTripsRecyclerView.FindTrip;
import com.example.student_carpooling.findTripsRecyclerView.FindTripAdapter;
import com.example.student_carpooling.seatRecyclerView.Seat;
import com.example.student_carpooling.tripRecyclerView.TripAdapter;
import com.example.student_carpooling.usersRecyclerView.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;

public class FilteredTrips extends AppCompatActivity {


    private RecyclerView tripRecyclerView;
    private RecyclerView.Adapter FiltertripAdapter;
    private RecyclerView.LayoutManager tripLayoutManager;

    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private TextView NUsername, Nemail, txt;
    private String ProfilePicUrl, UserID, Day, Destination, Seats, hours, mins, Starting, LuggageCheck, First, Surname, Fullname, Note, Time, UserName, DriverProfilePicUrl, DeclineResult, PassengerResult;
    private FirebaseAuth mAuth;
    private String DBUsername;
    private DatabaseReference UserDb, reference;
    Date tripdate;
    private String DriverKey;
    FirebaseUser CurrentUser;
    private int counter = 0;

    String Un="";

    String Id="";

    int check=0;
    Toolbar toolbar;

    TextView textView1, textView2;
    Button createRequest;
    ArrayList<String> declined;
    ArrayList<String> passengers;


    NavigationView navigationView;
    private ImageView navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtered_trips2);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Trip Results");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //be able to go back out of the activity
        finish();
    }
});

        Toast.makeText(this, "p"+Un.length(), Toast.LENGTH_SHORT).show();
        declined = new ArrayList<>();

        passengers = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        CurrentUser = mAuth.getCurrentUser();
        UserID = mAuth.getCurrentUser().getUid();
        UserDb = FirebaseDatabase.getInstance().getReference().child("users").child(UserID);
        UserDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("Username") != null) {
                        Un = map.get("Username").toString();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        textView1 = findViewById(R.id.Text);
        textView2 = findViewById(R.id.Text2);
        createRequest = findViewById(R.id.Request);

        tripRecyclerView = findViewById(R.id.FilterTripsRecycler);
        tripRecyclerView.setNestedScrollingEnabled(true); //not true?
        tripRecyclerView.setHasFixedSize(true);
        FiltertripAdapter = new FindTripAdapter(getDataFilterTrips(), FilteredTrips.this);
        tripLayoutManager = new LinearLayoutManager(FilteredTrips.this);
        tripRecyclerView.setLayoutManager(tripLayoutManager);

        tripRecyclerView.setAdapter(FiltertripAdapter);


        getDriverId();

        //for trip in results:.. if in declined... remove.. notify change


        //if (counter == 0) {

            //recycler view is empty, set the visibility of button and text view
           // tripRecyclerView.setVisibility(View.GONE);
           // textView1.setVisibility(View.VISIBLE);
           // textView2.setVisibility(View.VISIBLE);
            //createRequest.setVisibility(View.VISIBLE);

           // createRequest.setOnClickListener(new View.OnClickListener() {
           //     @Override
           //     public void onClick(View v) {
           //         Intent intent = new Intent(FilteredTrips.this, PassengerCreateRequests.class);
           //         startActivity(intent);
          //      }
          //  });

      //  }


        //not working yet..
        sortList();
    }


    //first set up onclick for the button in the recycler view


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case R.id.action_settings:
                AlertDialog.Builder dialog = new AlertDialog.Builder(FilteredTrips.this);
                dialog.setTitle("Are you sure you want to delete your account?");
                dialog.setMessage("By Doing this.....");
                dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CurrentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    //is deleted
                                    Toast.makeText(FilteredTrips.this, "Account Successfully deleted", Toast.LENGTH_LONG).show();
                                    UserDb.removeValue();
                                    Intent intent = new Intent(FilteredTrips.this, MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(FilteredTrips.this, "Account couldn't be deleted at this time", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    }
                });

                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void getDriverId() {
        final DatabaseReference DriverID = FirebaseDatabase.getInstance().getReference().child("TripForms");
        DriverID.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot id : dataSnapshot.getChildren()) {
                        DriverKey = id.getKey();
                        getTripIds(DriverKey);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getTripIds(final String Key) {
        DatabaseReference TripIDs = FirebaseDatabase.getInstance().getReference().child("TripForms").child(Key);
        TripIDs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //if there is any info there
                    for (DataSnapshot id : dataSnapshot.getChildren()) {
                        //then get the info under that unique ID
                        String TripKey = id.getKey();
                        if(declined.size()>0){
                            declined.clear();
                        }
                        if(passengers.size()>0){
                            passengers.clear();
                        }
                        getDeclinedList(Key,TripKey);
                        getPassengerList(Key,TripKey);
                        UserTripDB(Key, TripKey);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void UserTripDB(final String Key, final String ID) {
        //push().getKey();

        //get all driver trips
        DatabaseReference TripsDB = FirebaseDatabase.getInstance().getReference().child("TripForms").child(Key).child(ID);
        //Drivers full name is stored within "users"
        DatabaseReference UserDB = FirebaseDatabase.getInstance().getReference().child("users").child(Key);
        UserDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("Name") != null) {
                        String name = map.get("Name").toString();
                        First = name.substring(0, 1).toUpperCase() + name.substring(1);
                    }
                    if (map.get("Surname") != null) {
                        String surname = map.get("Surname").toString();
                        Surname = surname.substring(0, 1).toUpperCase() + surname.substring(1);
                    }

                    if (map.get("profileImageUrl") != null) {
                        DriverProfilePicUrl = map.get("profileImageUrl").toString();
                    }

                    if (map.get("Username") != null) {
                        UserName = map.get("Username").toString();
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        TripsDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    if (map.get("Time") != null) {
                        Time = map.get("Time").toString();
                        StringTokenizer tokens = new StringTokenizer(Time, ":");
                        hours = tokens.nextToken();
                        mins = tokens.nextToken();
                    }

                    //check that none of them are null
                    if (map.get("Date") != null) {
                        Day = map.get("Date").toString();
                        StringTokenizer tokens = new StringTokenizer(Day, "/");
                        Integer day = Integer.parseInt(tokens.nextToken());
                        Integer month = Integer.parseInt(tokens.nextToken());
                        Integer year = Integer.parseInt(tokens.nextToken());
                        //year month date
                        // year in date is saying 3919 rather than 2019
                        //TripDate = new Date(year - 1900, month - 1, day, 10, 0);
                        // String date_n = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(TripDate);
                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.UK);
                        try {

                            String dateStr = Day + " " + Time;
                            Date Datee = format.parse(dateStr);
                            long mili = Datee.getTime();
                            tripdate = new Date(mili);
                            //Toast.makeText(FilteredTrips.this, "t:"+tripdate, Toast.LENGTH_SHORT).show();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                    }

                    if (map.get("Seats") != null) {
                        Seats = map.get("Seats").toString();
                    }

                    if (map.get("Luggage") != null) {
                        LuggageCheck = map.get("Luggage").toString().toUpperCase();
                    }
                    if (map.get("Note") != null) {
                        Note = map.get("Note").toString();
                    }
                    if (map.get("Starting") != null) {
                        Starting = map.get("Starting").toString().toUpperCase();
                    }

                    if (map.get("Destination") != null) {
                        Destination = map.get("Destination").toString().toUpperCase();
                    }


                    if (!Key.equals(CurrentUser.getUid())) {
                        //make sure its not a past trips or isnt one that was created by that user.
                        // if date greater, tripdate is a past d
                        String[] timeSplit = Time.split(":");
                        Integer hours = Integer.parseInt(timeSplit[0]);
                        Integer mins = Integer.parseInt(timeSplit[1]);
                        Integer totalMins = (hours * 60) + mins;

                        // TripDate = new Date(year-1900, month - 1, day);

                        //create an array list?
                        Date rightNow = Calendar.getInstance().getTime();


                        if (rightNow.before(tripdate)) {


                           // Toast.makeText(FilteredTrips.this, "", Toast.LENGTH_SHORT).show();
                            //if (!declined.contains(CurrentUser.getUid())){

                                //if(!passengers.contains(CurrentUser.getUid())){
                                //continue
                                //not declined, add the trip


                                Fullname = First + " " + Surname;
                                if(passengers.size() >0){
                                   Id = passengers.get(0);
                                }
                            if(declined.size() >0){
                                Un = declined.get(0);
                            }



                            Toast.makeText(FilteredTrips.this, ""+Id, Toast.LENGTH_SHORT).show();
                                if ((Integer.parseInt(Seats) != 0)) {
                                       //if(Id.length() == 0 && Un.length() == 0){
                                        FindTrip object = new FindTrip(UserID, ID, Fullname, UserName, DriverProfilePicUrl, Time, Day, Starting, Destination, Seats, LuggageCheck, Note, Key);

                                        results.add(object);
                                        FiltertripAdapter.notifyDataSetChanged();
                                        //declined.clear();
                                        // passengers.clear();
                                        //Toast.makeText(FilteredTrips.this, ""+results.size(), Toast.LENGTH_SHORT).show();
                            }}
                                }
                        }}




                                //Toast.makeText(FilteredTrips.this,KeyCheck, Toast.LENGTH_SHORT).show();


                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        }



    private ArrayList results = new ArrayList<FindTrip>();
    //private ArrayList resultsTrips;

    private ArrayList<FindTrip> getDataFilterTrips() {

        //isnt working
        // if(resultsTrips.size() == 0){
        //  txt.setText("There are no matching trips..");
        //}
        return results;

    }


    //not working
    private void sortList() {

        Collections.sort(results, new Comparator<FindTrip>() {
            @Override
            public int compare(FindTrip o1, FindTrip o2) {
                return o1.getTime().compareTo(o2.getTime());
            }
        });
        FiltertripAdapter.notifyDataSetChanged();

    }


    private void addToList(String id){
        declined.add(id);

    }

    private void addToPassList(String id){
        passengers.add(id);
    }


    @Override
    public void onResume() {
        super.onResume();
        //results.clear();

    }

    private void getDeclinedList(String Key,String TripKey) {
        //get those declined and add to list
        DatabaseReference declinedCheck = FirebaseDatabase.getInstance().getReference().child("TripForms").child(Key).child(TripKey).child("Declined");
        //requestCheck.
        declinedCheck.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot id : dataSnapshot.getChildren()) {
                        String KeyCheck = id.getKey();
                        if(KeyCheck!=null) {
                            if (KeyCheck.equals(CurrentUser.getUid())) {
                                addToList(KeyCheck);
                            }

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getPassengerList(String Key,String TripKey) {
        //get those declined and add to list
        DatabaseReference passengerCheck = FirebaseDatabase.getInstance().getReference().child("TripForms").child(Key).child(TripKey).child("Passengers");
        //requestCheck.
        passengerCheck.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot id : dataSnapshot.getChildren()) {
                        String KeyCheck = id.getKey();

                        if(KeyCheck!=null) {
                            if (KeyCheck.equals(CurrentUser.getUid())) {
                                addToPassList(KeyCheck);
                            }

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
