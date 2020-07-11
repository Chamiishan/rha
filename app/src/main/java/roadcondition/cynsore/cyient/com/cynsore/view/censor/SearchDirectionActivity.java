package roadcondition.cynsore.cyient.com.cynsore.view.censor;

import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;

import roadcondition.cynsore.cyient.com.cynsore.R;

public class SearchDirectionActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mSearchSrc;
    private EditText mSearchDest;
    private Button mSubmit;

    private static final int REQUEST_SRC = 0;
    private static final int REQUEST_DEST = 1;

//    private LatLng mSrcLoc, mDestLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_direction);

        mSearchSrc = (EditText) findViewById(R.id.ic_search_src);
        mSearchSrc.setOnClickListener(this);
        mSearchSrc.setInputType(InputType.TYPE_NULL);
        mSearchSrc.setFocusable(true);

        Location location = getIntent().getParcelableExtra("curr_loc");
        if (location != null) {
            mSearchSrc.setTag(new LatLng(location.getLatitude(), location.getLongitude()));
            mSearchSrc.setText(getString(R.string.your_loc));
        }


        mSearchDest = (EditText) findViewById(R.id.ic_search_dest);
        mSearchDest.setOnClickListener(this);
        mSearchDest.setInputType(InputType.TYPE_NULL);
        mSearchDest.setFocusable(false);

        mSubmit = (Button) findViewById(R.id.ic_btn_place_submit);
        mSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ic_search_src:
                mSearchSrc.setError(null);
                mSearchSrc.clearFocus();
                openAutocompleteActivity(REQUEST_SRC);
                break;
            case R.id.ic_search_dest:
                mSearchDest.setError(null);
                mSearchDest.clearFocus();
                openAutocompleteActivity(REQUEST_DEST);
                break;
            case R.id.ic_btn_place_submit:
                String srcPlaceAdd = mSearchSrc.getText().toString();
                String destPlaceAdd = mSearchDest.getText().toString();

                if (srcPlaceAdd != null && srcPlaceAdd.length() > 0 && destPlaceAdd != null && destPlaceAdd.length() > 0) {
                    LatLng srcPlace = (LatLng) mSearchSrc.getTag();
                    LatLng destPlace = (LatLng) mSearchDest.getTag();

                    Intent data = new Intent();
                    data.putExtra("srcloc", srcPlace);
                    data.putExtra("destloc", destPlace);

                    setResult(RESULT_OK, data);
                    finish();
                } else {
                    if (srcPlaceAdd == null || srcPlaceAdd.length() == 0) {
                        mSearchSrc.setError("Source can't be empty");
                    }
                    if (destPlaceAdd == null || destPlaceAdd.length() == 0) {
                        mSearchDest.setError("Destination can't be empty");
                    }
                }
                break;
        }
    }

    private void openAutocompleteActivity(int requestCode) {
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .build(this);
            startActivityForResult(intent, requestCode);
        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = getString(R.string.play_services_unavailable) +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_SRC:
                Place place1 = PlaceAutocomplete.getPlace(this, data);
                if (place1 != null && place1.getAddress().toString() != null) {
                    mSearchSrc.setText(place1.getAddress().toString());
                    mSearchSrc.setTag(place1.getLatLng());
                }
                break;
            case REQUEST_DEST:
                Place place2 = PlaceAutocomplete.getPlace(this, data);
                if (place2 != null && place2.getAddress().toString() != null) {
                    mSearchDest.setText(place2.getAddress().toString());
                    mSearchDest.setTag(place2.getLatLng());
                }
                break;
        }
    }
}