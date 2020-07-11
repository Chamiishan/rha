package roadcondition.cynsore.cyient.com.cynsore.viewmodel.main;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class StatsViewModel extends ViewModel {

    private static final String TAG = "SharedViewModel";

    private MutableLiveData<Object> messageContainerA;

    public void init() {
        messageContainerA = new MutableLiveData<>();
    }

    public void sendMessage(Object msg) {
        messageContainerA.setValue(msg);
    }

    public LiveData<Object> getMessage() {
        return messageContainerA;
    }


}