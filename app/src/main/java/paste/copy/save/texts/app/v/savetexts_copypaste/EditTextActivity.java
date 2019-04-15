package paste.copy.save.texts.app.v.savetexts_copypaste;

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

/**
 * Edit the Text
 */

public class EditTextActivity extends AppCompatActivity {


    private FirebaseAnalytics mFirebaseAnalytics;

    private Toolbar mToolbar;
    private EditText mEnteredText;
    private Button mEditText, mClearText;
    private ImageView mStar;

    boolean isImportant = false;

    Realm realm;

    int key = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_text_activity);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mEnteredText = (EditText) findViewById(R.id.entered_text);
        mEditText = (Button) findViewById(R.id.edit_text);
        mStar = (ImageView) findViewById(R.id.star);
        mClearText = (Button) findViewById(R.id.clear_text);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setTitle(R.string.edit_text);
            getSupportActionBar().setTitle(R.string.edit_text);
            mToolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
        }

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Edit Text Screen!");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "User is editing text");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "EDIT");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        realm = Realm.getDefaultInstance();


        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            Toast.makeText(this, "Error in intents! (Contact Developer)", Toast.LENGTH_SHORT).show();
        } else {
            key = extras.getInt("id");

            Text tempText = realm.where(Text.class).equalTo("id", key).findFirst();
            realm.beginTransaction();
            mEnteredText.setText(tempText.getText());

            if (tempText.isImportant()) {
                mStar.setImageResource(R.drawable.star);
                isImportant = true;
            } else {
                mStar.setImageResource(R.drawable.un_star);
                isImportant = false;
            }

            realm.commitTransaction();

        }
        
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

        mEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String enteredText = mEnteredText.getText().toString();

                if(enteredText.isEmpty()) {
                    Toast.makeText(EditTextActivity.this, R.string.please_enter_text, Toast.LENGTH_SHORT).show();
                } else {

                    realm.beginTransaction();

                    Text newText = realm.where(Text.class).equalTo("id", key).findFirst();

                    newText.setId(key);
                    newText.setText(enteredText);
                    newText.setImportant(isImportant);
                    realm.commitTransaction();

                    // Toast.makeText(EditTextActivity.this, "New Text Added!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditTextActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                }

            }
        });

    }
}
