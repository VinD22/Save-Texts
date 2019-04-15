package adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import model.Text;
import paste.copy.save.texts.app.v.savetexts_copypaste.EditTextActivity;
import paste.copy.save.texts.app.v.savetexts_copypaste.R;

/*
 *  Adapter class to manage each text from the list of texts! 
 */

public class TextAdapter extends RecyclerView.Adapter<TextAdapter.RecyclerViewHolder> {

    private List<Text> data;
    private List<Text> dataCopy;
    private Context mContext;
    Realm realm;

    public TextAdapter(Context context, ArrayList<Text> data) {
        this.mContext = context;
        this.data = data;
        dataCopy = new ArrayList<Text>();
        dataCopy.addAll(data);
        realm = Realm.getDefaultInstance();
        // setHasStableIds(true);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.text_list_item, parent, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder viewHolder, final int position) {

        final Text tempText = data.get(viewHolder.getAdapterPosition());
        viewHolder.mTextName.setText(capitalizeFirstLetter(tempText.getText()));

        if (tempText.isImportant()) {
            viewHolder.mStar.setImageResource(R.drawable.star);
        } else {
            viewHolder.mStar.setImageResource(R.drawable.un_star);
        }

        viewHolder.mStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Change Images!
                if (tempText.isImportant()) {
                    viewHolder.mStar.setImageResource(R.drawable.un_star);
                } else {
                    viewHolder.mStar.setImageResource(R.drawable.star);
                }

                boolean isImportant = !tempText.isImportant();
                tempText.setImportant(isImportant);

                // Update Values in Realm
                Text toEdit = realm.where(Text.class).equalTo("id", tempText.getId()).findFirst();
                realm.beginTransaction();
                toEdit.setImportant(isImportant);
                realm.commitTransaction();


            }
        });

        viewHolder.mCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Copy to Clipboard!

                ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", tempText.getText());
                clipboard.setPrimaryClip(clip);


                Toast.makeText(mContext, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
            }
        });

        viewHolder.mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Share Text with Other Apps

                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    String sAux = tempText.getText();
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    mContext.startActivity(Intent.createChooser(i, mContext.getString(R.string.share_text)));
                } catch (Exception e) {
                    e.toString();
                }


            }
        });

        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(mContext, R.style.MyDialogTheme)
                        .setMessage(capitalizeFirstLetter(tempText.getText()))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();


            }
        });

        viewHolder.mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Go to Edit Screen and Edit the text and update the text. Pass Id using Intent!

                Intent intent = new Intent(mContext, EditTextActivity.class);
                intent.putExtra("id", tempText.getId());
                mContext.startActivity(intent);


            }
        });

        viewHolder.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 1. Delete from Arraylist

                data.remove(viewHolder.getAdapterPosition());
                notifyDataSetChanged();

                // 2. Delete from Realm Database

                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                Text category = realm.where(Text.class).equalTo("id", tempText.getId()).findFirst();
                category.deleteFromRealm();
                realm.commitTransaction();

                // TODO : SHow popup of confirm delete! - V2

            }
        });


    }


    public void filter(String text) {
        // Toast.makeText(mContext, "" + text + " /// " + dataCopy.size()  , Toast.LENGTH_SHORT).show();
        data.clear();
        if (text.isEmpty()) {
            data.addAll(dataCopy);
        } else {
            data.clear();
            text = text.toLowerCase();
            for (Text item : dataCopy) {
                if (item.getText().toLowerCase().contains(text)) {
                    data.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout mLinearLayout;
        protected TextView mTextName;
        protected ImageButton mStar, mCopy, mShare, mView, mEdit, mDelete;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            mLinearLayout = (LinearLayout) itemView.findViewById(R.id.lin);
            mTextName = (TextView) itemView.findViewById(R.id.text);
            mStar = (ImageButton) itemView.findViewById(R.id.star);
            mCopy = (ImageButton) itemView.findViewById(R.id.copy);
            mShare = (ImageButton) itemView.findViewById(R.id.share);
            mView = (ImageButton) itemView.findViewById(R.id.view);
            mEdit = (ImageButton) itemView.findViewById(R.id.edit);
            mDelete = (ImageButton) itemView.findViewById(R.id.delete);
        }

    }


}
