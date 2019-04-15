package paste.copy.save.texts.app.v.savetexts_copypaste;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

import adapter.TextAdapter;
import io.realm.Realm;
import io.realm.RealmResults;
import model.Text;

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;
    Realm realm;
    ArrayList<Text> listOfTexts = new ArrayList<>();

    private Toolbar mToolbar;
    FloatingActionButton mAddText;

    private RecyclerView recList;
    private TextAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(getIntent().getExtras() == null) {
            // Toast.makeText(this, "Error in intents! (Contact Developer)", Toast.LENGTH_SHORT).show();
        } else {

            Bundle extras = getIntent().getExtras();
            String url = extras.getString("url");
            // Toast.makeText(this, "Url : " + url, Toast.LENGTH_SHORT).show();

            if(url != null) {

                if(!url.isEmpty()) {

                    try{

                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);

                    } catch (ActivityNotFoundException e) {

                    }


                }

            }

        }

        realm = Realm.getDefaultInstance();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        recList = (RecyclerView) findViewById(R.id.texts_list_recyclerview);
        mAddText = (FloatingActionButton) findViewById(R.id.fab_add_text);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setTitle(R.string.app_name);
            getSupportActionBar().setTitle(R.string.app_name);
            mToolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
        }

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Home Screen!");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "List of texts appear here");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Home");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        mAddText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddText.class);
                startActivity(intent);
            }
        });

        final LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(layoutManager);
        recList.setHasFixedSize(true);

        recList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0)
                    mAddText.hide();
                else if (dy < 0)
                    mAddText.show();
            }
        });

    }

    public void getTextsList() {

        listOfTexts.clear();
        RealmResults<Text> textResults =
                realm.where(Text.class).findAll();

        // textResults = textResults.sort("text"); // Default Alphabetically Sorting!

        for (Text t : textResults) {
            final Text tempText = new Text();
            tempText.setId(t.getId());
            tempText.setText(t.getText());
            tempText.setImportant(t.isImportant());
            listOfTexts.add(tempText);
        }

        mAdapter = new TextAdapter(MainActivity.this, listOfTexts);
        recList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        Log.i("totalTexts", " " + listOfTexts.size());
        // Toast.makeText(this, "Texts Size : " + listOfTexts.size(), Toast.LENGTH_SHORT).show();

        if (listOfTexts.isEmpty()) {

            // Adding A Default Entry!

            realm.beginTransaction();

            Text newText = realm.createObject(Text.class);
            int nextKey = getNextKey();
            newText.setId(nextKey);
            newText.setText(getString(R.string.sample_text));
            newText.setImportant(true);
            realm.commitTransaction();

            getTextsList();


        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        getTextsList();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.white));
        searchEditText.setHintTextColor(getResources().getColor(R.color.white));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                if (TextUtils.isEmpty(newText)) {
//                    adapter.filter("");
//                    listView.clearTextFilter();
//                } else {
//                    adapter.filter(newText);
//                }
                mAdapter.filter(newText + "");

                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.settings:
                // Toast.makeText(this, "Show Settings!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;

            case R.id.refresh:
                getTextsList();
                return true;

            case R.id.star:

                listOfTexts.clear();
                RealmResults<Text> textResults =
                        realm.where(Text.class).equalTo("isImportant", true).findAll();

                // textResults = textResults.sort("text"); // Default Alphabetically Sorting!

                for (Text t : textResults) {
                    final Text tempText = new Text();
                    tempText.setId(t.getId());
                    tempText.setText(t.getText());
                    tempText.setImportant(t.isImportant());
                    listOfTexts.add(tempText);
                }

                mAdapter = new TextAdapter(MainActivity.this, listOfTexts);
                recList.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public int getNextKey() {
        try {
            return realm.where(Text.class).max("id").intValue() + 1;
        } catch (ArrayIndexOutOfBoundsException e) {
            return 0;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.this.finish();
    }


}
