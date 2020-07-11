package roadcondition.cynsore.cyient.com.cynsore.SQLDataBase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import roadcondition.cynsore.cyient.com.cynsore.dao.DaoAccess;
import roadcondition.cynsore.cyient.com.cynsore.model.FileData;
import roadcondition.cynsore.cyient.com.cynsore.model.SensorFileData;

@Database(entities = {FileData.class, SensorFileData.class}, version = 3, exportSchema = false)
public abstract class RRDatabase extends RoomDatabase {

    public abstract DaoAccess daoAccess();

//    @NonNull
//    @Override
//    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
//        return null;
//    }
//
//    @NonNull
//    @Override
//    protected InvalidationTracker createInvalidationTracker() {
//        return null;
//    }
//
//    @Override
//    public void clearAllTables() {
//
//    }

}