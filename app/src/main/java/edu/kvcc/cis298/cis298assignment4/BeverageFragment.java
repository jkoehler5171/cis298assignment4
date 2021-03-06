//Jordan Koehler
//MW 2:30 - 4:45
//December 11th, 2016

package edu.kvcc.cis298.cis298assignment4;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by David Barnes on 11/3/2015.
 */
public class BeverageFragment extends Fragment {

    //String key that will be used to send data between fragments
    private static final String ARG_BEVERAGE_ID = "crime_id";

    private static final int REQUEST_CONTACT = 1;


    //private class level vars for the model properties
    private EditText mId;
    private EditText mName;
    private EditText mPack;
    private EditText mPrice;
    private CheckBox mActive;
    private Button mContactsButton;
    private Button mEmailButton;
    private String mContactName;
    private String mContactEmail;

    //Private var for storing the beverage that will be displayed with this fragment
    private Beverage mBeverage;

    //Public method to get a properly formatted version of this fragment
    public static BeverageFragment newInstance(String id) {
        //Make a bungle for fragment args
        Bundle args = new Bundle();
        //Put the args using the key defined above
        args.putString(ARG_BEVERAGE_ID, id);

        //Make the new fragment, attach the args, and return the fragment
        BeverageFragment fragment = new BeverageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //When created, get the beverage id from the fragment args.
        String beverageId = getArguments().getString(ARG_BEVERAGE_ID);
        //use the id to get the beverage from the singleton
        mBeverage = BeverageCollection.get(getActivity()).getBeverage(beverageId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Use the inflator to get the view from the layout
        View view = inflater.inflate(R.layout.fragment_beverage, container, false);

        //Get handles to the widget controls in the view
        mId = (EditText) view.findViewById(R.id.beverage_id);
        mName = (EditText) view.findViewById(R.id.beverage_name);
        mPack = (EditText) view.findViewById(R.id.beverage_pack);
        mPrice = (EditText) view.findViewById(R.id.beverage_price);
        mActive = (CheckBox) view.findViewById(R.id.beverage_active);
        mContactsButton = (Button) view.findViewById(R.id.beverage_contact_button);
        mEmailButton = (Button) view.findViewById(R.id.beverage_email_button);

        //Set the widgets to the properties of the beverage
        mId.setText(mBeverage.getId());
        mId.setEnabled(false);
        mName.setText(mBeverage.getName());
        mPack.setText(mBeverage.getPack());
        mPrice.setText(Double.toString(mBeverage.getPrice()));
        mActive.setChecked(mBeverage.isActive());

        //Text changed listenter for the id. It will not be used since the id will be always be disabled.
        //It can be used later if we want to be able to edit the id.
        mId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBeverage.setId(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //Text listener for the name. Updates the model as the name is changed
        mName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBeverage.setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //Text listener for the Pack. Updates the model as the text is changed
        mPack.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBeverage.setPack(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        //Text listener for the price. Updates the model as the text is typed.
        mPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //If the count of characters is greater than 0, we will update the model with the
                //parsed number that is input.
                if (count > 0) {
                    mBeverage.setPrice(Double.parseDouble(s.toString()));
                //else there is no text in the box and therefore can't be parsed. Just set the price to zero.
                } else {
                    mBeverage.setPrice(0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        //Set a checked changed listener on the checkbox
        mActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBeverage.setActive(isChecked);
            }
        });


        //This is an intent to start up the contacts app so we can choose a contact.
        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);

        PackageManager packageManager = getActivity().getPackageManager();

        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mContactsButton.setEnabled(false);
        }

        mEmailButton.setEnabled(false);

        if (!mContactsButton.isEnabled())
        {
            mEmailButton.setEnabled(true);
        }


        //When we press the contacts button, it starts up the Contact app using the intent created above.
        mContactsButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
           startActivityForResult(pickContact, REQUEST_CONTACT);
               mEmailButton.setEnabled(true);
           }
       });


        //The Email button auto generates an email to be sent to the selected contact.
        mEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent emailIntent = new Intent(Intent.ACTION_SEND);

                emailIntent.setType("text/plain");

                if(mContactName != null) {

                    mContactEmail = getEmails().get(0);

                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{mContactEmail});
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.beverage_email_subject));
                    emailIntent.putExtra(Intent.EXTRA_TEXT, wineInfo());

                    emailIntent = Intent.createChooser(emailIntent, getString(R.string.email_chooser_text));

                    startActivity(emailIntent);
                }
                else
                {
                    startActivity(emailIntent);
                }

            }
        });



        //Lastly return the view with all of this stuff attached and set on it.
        return view;
    }


    //This method basically checks to see what result we got from going to pick our contact.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //If it didn't work, why bother moving on?
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        //if It worked and we actually got some Data back, let's process it.
        if(data != null)
        {

            Uri contactUri = data.getData();

            String[] contactNames= new String[] {ContactsContract.Contacts.DISPLAY_NAME};

            Cursor cursor = getActivity().getContentResolver().query(contactUri, contactNames, null, null, null);

            try {
                if (cursor.getCount() == 0) {
                    return;
                }

                cursor.moveToFirst();

                mContactName= cursor.getString(0);




            }
            finally {
                cursor.close();
            }

      }

    }


    //This method basically just throws all of the different bits and bobs of wine information into a coherent email format.
    private String wineInfo() {


        String request = getString(R.string.beverage_email_header);

        String availability = "";

        if(mBeverage.isActive() == true)
        {
            availability = getString(R.string.beverage_available_true);
        }
        else
        {
            availability= getString(R.string.beverage_available_false);
        }

        String wInfo = mContactName + ",\n" + "\n" +
                request +  "\n" + "\n" + mBeverage.getId() + "\n" + mBeverage.getName() + "\n" + mBeverage.getPack()
                + "\n" +  Double.toString(mBeverage.getPrice()) + "\n" + availability;



        return wInfo;
    }


    //Alright, I'll be honest, the whole getting the email information thing is a bit confusing to me. So I think the way that it works is that we use one cursor to pick out
    //a contact, and then another to pick out their email information? I mostly just adapted the link you provided to work with what we're doing. It greatly irritates me that I don't
    //fully understand it.

      public ArrayList<String> getEmails(){

        ArrayList<String> emails = new ArrayList<String>();

        ContentResolver resolver = getActivity().getContentResolver();

        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);

        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {

                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                Cursor emailCursor =resolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);

                while (emailCursor.moveToNext()) {

                    String email = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));

                    if(email!=null)
                    {
                        emails.add(email);
                    }
                }
                emailCursor.close();
            }
        }
        cursor.close();
        return emails;
    }







}

