package com.btn.pronotes.Checklist;
import com.btn.pronotes.Checklist.ChecklistNotesActivity;
import com.btn.pronotes.Checklist.ChecklistActivity;
import com.btn.pronotes.Checklist.ChecklistAdapter;
import com.btn.pronotes.Checklist.ChecklistItem;
public class ChecklistItem {
    private String text;
    private boolean isChecked;

    public ChecklistItem(String text, boolean isChecked) {
        this.text = text;
        this.isChecked = isChecked;
    }
    public ChecklistItem(String text, boolean isChecked, boolean isCompleted) {
        this.text = text;
        this.isChecked = isChecked;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
