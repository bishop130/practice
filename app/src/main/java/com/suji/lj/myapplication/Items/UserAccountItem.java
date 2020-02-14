package com.suji.lj.myapplication.Items;

public class UserAccountItem {


    String api_tran_id;
    String api_tran_dtm;
    String rsp_code;
    String rsp_message;
    String user_seq_no;
    String user_ci;
    String user_name;
    String res_cnt;

    public String getRes_cnt() {
        return res_cnt;
    }

    public void setRes_cnt(String res_cnt) {
        this.res_cnt = res_cnt;
    }

    String fintech_use_num; //핀테크 이용번호
    String account_alias; //계좌별명
    String bank_code_std; //
    String bank_code_sub;
    String bank_name;
    String account_num;
    String account_num_masked;
    String account_holder_name;
    String account_type;
    String inquiry_agree_yn;
    String inquiry_agree_dtime;
    String transfer_agree_yn;
    String transfer_agree_dtime;
    String payer_num;
    String account_state;

    public UserAccountItem(){

    }

    public UserAccountItem(String api_tran_id, String api_tran_dtm, String rsp_code, String rsp_message, String user_seq_no, String user_ci, String user_name, String res_cnt, String fintech_use_num, String account_alias, String bank_code_std, String bank_code_sub, String bank_name, String account_num, String account_num_masked, String account_holder_name, String account_type, String inquiry_agree_yn, String inquiry_agree_dtime, String transfer_agree_yn, String transfer_agree_dtime, String payer_num) {
        this.api_tran_id = api_tran_id;
        this.api_tran_dtm = api_tran_dtm;
        this.rsp_code = rsp_code;
        this.rsp_message = rsp_message;
        this.user_seq_no = user_seq_no;
        this.user_ci = user_ci;
        this.user_name = user_name;
        this.res_cnt =res_cnt;
        this.fintech_use_num = fintech_use_num;
        this.account_alias = account_alias;
        this.bank_code_std = bank_code_std;
        this.bank_code_sub = bank_code_sub;
        this.bank_name = bank_name;
        this.account_num = account_num;
        this.account_num_masked = account_num_masked;
        this.account_holder_name = account_holder_name;
        this.account_type = account_type;
        this.inquiry_agree_yn = inquiry_agree_yn;
        this.inquiry_agree_dtime = inquiry_agree_dtime;
        this.transfer_agree_yn = transfer_agree_yn;
        this.transfer_agree_dtime = transfer_agree_dtime;
        this.payer_num = payer_num;
    }

    public String getAccount_state() {
        return account_state;
    }

    public void setAccount_state(String account_state) {
        this.account_state = account_state;
    }

    public String getFintech_use_num() {
        return fintech_use_num;
    }

    public void setFintech_use_num(String fintech_use_num) {
        this.fintech_use_num = fintech_use_num;
    }

    public String getAccount_alias() {
        return account_alias;
    }

    public void setAccount_alias(String account_alias) {
        this.account_alias = account_alias;
    }

    public String getBank_code_std() {
        return bank_code_std;
    }

    public void setBank_code_std(String bank_code_std) {
        this.bank_code_std = bank_code_std;
    }

    public String getBank_code_sub() {
        return bank_code_sub;
    }

    public void setBank_code_sub(String bank_code_sub) {
        this.bank_code_sub = bank_code_sub;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getAccount_num() {
        return account_num;
    }

    public void setAccount_num(String account_num) {
        this.account_num = account_num;
    }

    public String getAccount_num_masked() {
        return account_num_masked;
    }

    public void setAccount_num_masked(String account_num_masked) {
        this.account_num_masked = account_num_masked;
    }

    public String getAccount_holder_name() {
        return account_holder_name;
    }

    public void setAccount_holder_name(String account_holder_name) {
        this.account_holder_name = account_holder_name;
    }

    public String getAccount_type() {
        return account_type;
    }

    public void setAccount_type(String account_type) {
        this.account_type = account_type;
    }

    public String getInquiry_agree_yn() {
        return inquiry_agree_yn;
    }

    public void setInquiry_agree_yn(String inquiry_agree_yn) {
        this.inquiry_agree_yn = inquiry_agree_yn;
    }

    public String getInquiry_agree_dtime() {
        return inquiry_agree_dtime;
    }

    public void setInquiry_agree_dtime(String inquiry_agree_dtime) {
        this.inquiry_agree_dtime = inquiry_agree_dtime;
    }

    public String getTransfer_agree_yn() {
        return transfer_agree_yn;
    }

    public void setTransfer_agree_yn(String transfer_agree_yn) {
        this.transfer_agree_yn = transfer_agree_yn;
    }

    public String getTransfer_agree_dtime() {
        return transfer_agree_dtime;
    }

    public void setTransfer_agree_dtime(String transfer_agree_dtime) {
        this.transfer_agree_dtime = transfer_agree_dtime;
    }

    public String getPayer_num() {
        return payer_num;
    }

    public void setPayer_num(String payer_num) {
        this.payer_num = payer_num;
    }
    public String getApi_tran_id() {
        return api_tran_id;
    }

    public void setApi_tran_id(String api_tran_id) {
        this.api_tran_id = api_tran_id;
    }

    public String getApi_tran_dtm() {
        return api_tran_dtm;
    }

    public void setApi_tran_dtm(String api_tran_dtm) {
        this.api_tran_dtm = api_tran_dtm;
    }

    public String getRsp_code() {
        return rsp_code;
    }

    public void setRsp_code(String rsp_code) {
        this.rsp_code = rsp_code;
    }

    public String getRsp_message() {
        return rsp_message;
    }

    public void setRsp_message(String rsp_message) {
        this.rsp_message = rsp_message;
    }

    public String getUser_seq_no() {
        return user_seq_no;
    }

    public void setUser_seq_no(String user_seq_no) {
        this.user_seq_no = user_seq_no;
    }

    public String getUser_ci() {
        return user_ci;
    }

    public void setUser_ci(String user_ci) {
        this.user_ci = user_ci;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
