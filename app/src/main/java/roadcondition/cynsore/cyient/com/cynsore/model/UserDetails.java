package roadcondition.cynsore.cyient.com.cynsore.model;

/**
 * Created by ij39559 on 7/24/2018.
 */

public class UserDetails {

    private String email_id;
    private String passw_hash;
    private String name;
    private String mob_num;
    private String msg;
    private String data_of_create;
    private String date_of_expiry;
    private boolean accExpires;
    private String gcm_id;
    private boolean acc_verified;
    private int login_type;
    private String version;
    private String curr_version;
    private long remain_days;
    private String changelog;
    private String reg_email;
    private boolean reg_email_verified;
    private boolean tasks_avl;

    public void setTasks_avl(boolean tasks_avl) {
        this.tasks_avl = tasks_avl;
    }

    public boolean isTasks_avl() {
        return tasks_avl;
    }

    public void setLogin_type(int login_type) {
        this.login_type = login_type;
    }

    public int getLogin_type() {
        return login_type;
    }

    public void setGcm_id(String gcm_id) {
        this.gcm_id = gcm_id;
    }

    public void setAcc_verified(boolean acc_verified) {
        this.acc_verified = acc_verified;
    }

    public String getGcm_id() {
        if (gcm_id == null) {
            return "";
        }
        return gcm_id;
    }

    public boolean isAcc_verified() {
        return acc_verified;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getPassw_hash() {
        return passw_hash;
    }

    public void setPassw_hash(String passw_hash) {
        this.passw_hash = passw_hash;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMob_num() {
        return mob_num;
    }

    public void setMob_num(String mob_num) {
        this.mob_num = mob_num;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getData_of_create() {
        return data_of_create;
    }

    public void setData_of_create(String data_of_create) {
        this.data_of_create = data_of_create;
    }

    public String getDate_of_expiry() {
        return date_of_expiry;
    }

    public void setDate_of_expiry(String date_of_expiry) {
        this.date_of_expiry = date_of_expiry;
    }

    public boolean isAccExpires() {
        return accExpires;
    }

    public void setAccExpires(boolean accExpires) {
        this.accExpires = accExpires;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setCurr_version(String curr_version) {
        this.curr_version = curr_version;
    }

    public String getCurr_version() {
        return curr_version;
    }

    public void setRemain_days(long remain_days) {
        this.remain_days = remain_days;
    }

    public long getRemain_days() {
        return remain_days;
    }

    public void setChangelog(String changelog) {
        this.changelog = changelog;
    }

    public String getChangelog() {
        return changelog;
    }

    public void setReg_email(String reg_email) {
        this.reg_email = reg_email;
    }

    public String getReg_email() {
        return reg_email;
    }

    public void setReg_email_verified(boolean reg_email_verified) {
        this.reg_email_verified = reg_email_verified;
    }

    public boolean isReg_email_verified() {
        return reg_email_verified;
    }
}
