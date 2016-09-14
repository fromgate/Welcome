package ru.nukkit.welcome.util;

import cn.nukkit.Server;
import cn.nukkit.scheduler.AsyncTask;

public abstract class Task extends AsyncTask {

    public void start(){
        Server.getInstance().getScheduler().scheduleAsyncTask(this);
    }
}
