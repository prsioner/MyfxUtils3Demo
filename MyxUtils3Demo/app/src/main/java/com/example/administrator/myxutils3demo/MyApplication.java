package com.example.administrator.myxutils3demo;

import android.app.Application;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;

/**
 * Created by Administrator on 2016/5/11.
 */
public class MyApplication extends Application{

    private DbManager.DaoConfig daoConfig;
    public DbManager.DaoConfig getDaoConfig(){

        return daoConfig;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);

        //自定义了一个数据库文件的存储路径android.os.Environment.getExternalStorageDirectory().getPath() + "/tempImage/";
        daoConfig = new DbManager.DaoConfig()
                .setDbName("myUtils_db")
                .setDbVersion(1)
                .setDbDir(new File(android.os.Environment
                        .getExternalStorageDirectory().getPath() + "/tempImage/"))
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {

                    }
                });
        DbManager dbManager = x.getDb(daoConfig);

        xUtilsPerson person1=new xUtilsPerson();
        person1.setName("xiaoming");
        person1.setAge("23");
        xUtilsPerson person2=new xUtilsPerson();
        person2.setName("xiaofu");
        person2.setAge("24");
        try {
            dbManager.save(person1);
            dbManager.save(person2);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
