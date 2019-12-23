package com.suji.lj.myapplication.Items;

public class ItemForServer {

    public String user_id;
    public boolean is_success;
    public String root_id;
    public String children_id;

    public ItemForServer() {

    }

    public ItemForServer(String user_id, boolean is_success, String root_id, String children_id) {
        this.user_id = user_id;
        this.is_success = is_success;
        this.root_id = root_id;
        this.children_id = children_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public boolean isIs_success() {
        return is_success;
    }

    public void setIs_success(boolean is_success) {
        this.is_success = is_success;
    }

    public String getRoot_id() {
        return root_id;
    }

    public void setRoot_id(String root_id) {
        this.root_id = root_id;
    }

    public String getChildren_id() {
        return children_id;
    }

    public void setChildren_id(String children_id) {
        this.children_id = children_id;
    }
}
