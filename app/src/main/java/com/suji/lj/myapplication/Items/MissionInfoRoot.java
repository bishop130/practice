package com.suji.lj.myapplication.Items;

import java.util.ArrayList;
import java.util.Map;

public class MissionInfoRoot {

    public int penalty_amt;
    public String mission_registered_date_time;
    public Map<String, Object> children_id;
    public ArrayList<ContactItem> friends_selected_list;

    public MissionInfoRoot(int penalty_amt, String mission_registered_date_time, Map<String, Object> children_id, ArrayList<ContactItem> friends_selected_list) {
        this.penalty_amt = penalty_amt;
        this.mission_registered_date_time = mission_registered_date_time;
        this.children_id = children_id;
        this.friends_selected_list = friends_selected_list;
    }

    public MissionInfoRoot() {

    }

    public int getPenalty_amt() {
        return penalty_amt;
    }

    public void setPenalty_amt(int penalty_amt) {
        this.penalty_amt = penalty_amt;
    }

    public String getMission_registered_date_time() {
        return mission_registered_date_time;
    }

    public void setMission_registered_date_time(String mission_registered_date_time) {
        this.mission_registered_date_time = mission_registered_date_time;
    }

    public Map<String, Object> getChildren_id() {
        return children_id;
    }

    public void setChildren_id(Map<String, Object> children_id) {
        this.children_id = children_id;
    }

    public ArrayList<ContactItem> getFriends_selected_list() {
        return friends_selected_list;
    }

    public void setFriends_selected_list(ArrayList<ContactItem> friends_selected_list) {
        this.friends_selected_list = friends_selected_list;
    }
}
