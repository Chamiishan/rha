package roadcondition.cynsore.cyient.com.cynsore.utility;

import roadcondition.cynsore.cyient.com.cynsore.BuildConfig;

/**
 * Created by sn34434 on 12-04-2017.
 */

public class Constants {

    //cyient login validation url
    public static final String test_cyient_login = "https://iptools.cyient.com/Login/Login/CheckLogin";
    //noida
//    public static final String base_url = "http://14.140.190.119:443/loginws_1.0_test/";
    //Hyderabad
//    public static final String base_url = "http://172.17.1.70:9090/loginws_1.0_test/";
    public static final String base_url = "https://iptools.cyient.com/loginws_1.0_test/";

    public static final String fileUploadUri = "https://iptools.cyient.com/IndoorMappingService/webapi/dbfile/upload1";
    //    public static final String fileUploadUri = "https://tnnsolutions.cyient.com/api/rhafeed";
//    public static final String fileUploadUri = "http://172.23.142.12:8087/api/v1/logs";
    public static final String fetch_task_details = "https://iptools.cyient.com/runnerAtRoad/webresource/workflow/taskdetails?userid=";
    public static final String update_task_details = "https://iptools.cyient.com/runnerAtRoad/webresource/workflow/upateTask";

    public static final String apkDwnldURL_MobileL = "https://iptools.cyient.com/apkdownload/app/download?App_Name=RHA_vsm";
    public static final String apkDwnldURL_MobileT = "https://iptools.cyient.com/apkdownload/app/download?App_Name=RHA_MobileT";

    public static final String apkDwnldURL_DongleL = "https://iptools.cyient.com/apkdownload/app/download?App_Name=RHA_Dongle";
    public static final String apkDwnldURL_DongleT = "https://iptools.cyient.com/apkdownload/app/download?App_Name=RHA_DongleT";

    public static final String GOOGLE_PROJECT_ID = "845802393489";

    public static final String MY_PREFS_NAME = "my_prefernces";

    public static final String SENT_FEEDBACK = "sent_feedback";
    public static final String FEEDBACK_RESPONSE = "feedback_response";

    public static final String BUMPS = "Bumps";
    public static final String ROAD_INTERVALS = "RoadIntervals";
    public static final String WEB_URL_CYIENT = "http://www.cyient.com/about-us/who-we-are/";

    public static final String DEFAULT_JOB_ID = "9999";

    public static String getApkDownloadUri() {
        if (BuildConfig.FLAVOR.equals("dongle")) {
            return apkDwnldURL_DongleT;
        }
        return apkDwnldURL_MobileT;
    }

    public static class UPDATE_KEYS {
        public static final String UPDATE_REM_DAYS = "update_rem_days";
        public static final String UPDATE_REQ_KEY = "update_req";
        public static final String UPDATE_CHANGE_LOG = "changelog";
    }

    public static class TASKS_KEY {
        public static final String IS_TASK_AVL = "task_avl";
    }

    public static class LoginStatus {
        public static final int ACTIVE = 1;
        public static final int INACTIVE = 0;
    }

    public static class LoginDomain {
        public static final String cyient = "cyient";
        public static final String gmail = "gmail";
    }

    public static class FileUploadStatus {
        public static final int START_RECORD = 0;
        public static final int END_RECORD = 1;
        public static final int START_UPLOAD = 2;
        public static final int FULLY_UPLOAD = 3;
    }

    public static class FileType {
        public static final int NONVID_SENSOR_DATA = 1;
        public static final int VID_SENSOR_DATA = 2;
    }

    public static class NOTIFICATION_IDS {
        public static final int NOTIFICATION_RHA_SERVICE = 100;
        public static final int NOTIFICATION_VID_UPLOAD = 101;
    }

    public static class MODE {
        public static final int VIEW_MODE = 1;
        public static final int CAPTURE_DATA_MODE = 2;
    }

    public static class FilterKeys {
        public static final int cboxAll = 1;
        public static final int cboxComp = 2;
        public static final int cboxWIP = 3;
        public static final int cboxYTS = 4;
        public static final int cboxHold = 5;
        public static final int cboxReject = 6;
        public static final int upcoming = 7;
        public static final int past = 8;
        public static final int today = 9;
    }

    public static class WorkStatus {
        public static final String YTS = "Yet to Start";
        public static final String WIP = "Work In Progress";
        public static final String COMP = "Completed";
        public static final String REJECTED = "Rejected";
        public static final String HOLD = "Hold";
    }

}