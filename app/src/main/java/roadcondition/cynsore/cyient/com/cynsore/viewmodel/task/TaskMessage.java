package roadcondition.cynsore.cyient.com.cynsore.viewmodel.task;

public class TaskMessage {

    private String fromClass;
    private String msg;
    private Object tag;
    private Object tag2;
    public final static String SHOW_TASK_DETAIL = "show_task_detail";
    public final static String SHOW_TASK_LIST = "show_task_list";
    public final static String CAPTURE_SENSOR = "capture_sensor";
    public final static String POP_STACK = "pop_stack";
//    public final static String GET_DETAILS = "get_details";
    public final static String CHANGE_STATUS = "change_Status";

    public String getFromClass() {
        return fromClass;
    }

    public void setFromClass(String fromClass) {
        this.fromClass = fromClass;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag2(Object tag2) {
        this.tag2 = tag2;
    }

    public Object getTag2() {
        return tag2;
    }
}