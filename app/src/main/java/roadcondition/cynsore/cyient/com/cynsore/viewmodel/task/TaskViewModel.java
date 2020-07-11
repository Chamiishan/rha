package roadcondition.cynsore.cyient.com.cynsore.viewmodel.task;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class TaskViewModel extends ViewModel {

    private MutableLiveData<TaskMessage> messageContainerA;

    public void init() {
        messageContainerA = new MutableLiveData<>();
    }

    public void sendMessage(TaskMessage msg) {
        messageContainerA.setValue(msg);
    }

    public LiveData<TaskMessage> getMessage() {
        return messageContainerA;
    }


}