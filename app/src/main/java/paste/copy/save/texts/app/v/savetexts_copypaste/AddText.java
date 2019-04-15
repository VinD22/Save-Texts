package paste.copy.save.texts.app.v.savetexts_copypaste;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import io.realm.Realm;
import model.Text;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

/**
 * Activity to add new Text!
 */

public class AddText extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;

    private Toolbar mToolbar;
    private EditText mEnteredText;
    private Button mAddText, mClearText;
    private ImageView mStar;

    boolean isImportant = false;

    Realm realm;

    ClipboardManager clipboard;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_text);

        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mEnteredText = (EditText) findViewById(R.id.entered_text);
        mAddText = (Button) findViewById(R.id.add_text);
        mStar = (ImageView) findViewById(R.id.star);
        mClearText = (Button) findViewById(R.id.clear_text);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setTitle(R.string.add_text);
            getSupportActionBar().setTitle(R.string.add_text);
            mToolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
        }

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Add Text Screen!");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "User is adding texts");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "2nd Screen!");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        
        realm = Realm.getDefaultInstance();


        mStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isImportant) {
                    mStar.setImageResource(R.drawable.un_star);
                    isImportant = false;
                } else {
                    mStar.setImageResource(R.drawable.star);
                    isImportant = true;
                }

            }
        });

        mClearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mEnteredText.setText("");

            }
        });

        mAddText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String enteredText = mEnteredText.getText().toString();

                if(enteredText.isEmpty()) {
                    Toast.makeText(AddText.this, R.string.please_enter_text, Toast.LENGTH_SHORT).show();
                } else {

                    realm.beginTransaction();

                    Text newText = realm.createObject(Text.class);
                    int nextKey = getNextKey();
                    newText.setId(nextKey);
                    newText.setText(enteredText);
                    newText.setImportant(isImportant);
                    realm.commitTransaction();

                    // Toast.makeText(AddText.this, "New Text Added!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddText.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    
                    

                }

            }
        });


        mEnteredText.setText(capitalizeFirstLetter(getClipboardText()));

    }

    public String getClipboardText(){
//        try {
//
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
//                android.content.ClipboardManager clipboard =  (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
//                return clipboard.getText().toString();
//            } else{
//                android.text.ClipboardManager clipboard = (android.text.ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
//                return clipboard.getText().toString();
//            }
//        } catch (Exception e) {
//
//        }

        String pasteData = "";
        if (!(clipboard.hasPrimaryClip())) {



        } else if (!(clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))) {

            // since the clipboard has data but it is not plain text

        } else {

            //since the clipboard contains plain text.
            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);

            // Gets the clipboard as text.
            pasteData = item.getText().toString();

        }

        return pasteData;


    }

    public String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    public int getNextKey()
    {
        try {
            return realm.where(Text.class).max("id").intValue() + 1;
        } catch (ArrayIndexOutOfBoundsException e)
        { return 0; }
    }

}
