package model;

import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * Model Class for Text
 */

public class Text extends RealmObject {

    @Required
    private String text;
    private int id;
    private boolean isImportant;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isImportant() {
        return isImportant;
    }

    public void setImportant(boolean important) {
        isImportant = important;
    }

}
