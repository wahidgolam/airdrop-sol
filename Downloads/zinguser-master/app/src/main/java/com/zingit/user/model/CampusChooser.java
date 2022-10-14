package com.zingit.user.model;

import com.zingit.user.Campus;

public class CampusChooser {
    Campus campus;
    boolean isSelected;

    public CampusChooser(Campus campus, boolean isSelected) {
        this.campus = campus;
        this.isSelected = false;
    }

    public CampusChooser() {
        isSelected = false;
    }

    public Campus getCampus() {
        return campus;
    }

    public void setCampus(Campus campus) {
        this.campus = campus;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
