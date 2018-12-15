package com.ehear.aiot.cloud.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;



import com.realrelax.alexa.bean.CustomTask;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SocketService {

    private ScheduledExecutorService scheduExec = Executors.newScheduledThreadPool(1);
    public static long customTaskcount = 0;
    public static volatile List<CustomTask> taskList = new ArrayList<CustomTask>();

    public static List<CustomTask> getTaskList() {
        return taskList;
    }

    public static void setTaskList(List<CustomTask> taskList) {
        SocketService.taskList = taskList;
    }

    // public static List<String> to_be_handled_index = new ArrayList<String>();
    public static HashMap<String, String> last_index = new HashMap<>();
    public static int count = 0;

    Runnable pushTask = new Runnable() {
        public void run() {
            ServerMainThread mMainThread = new ServerMainThread();
            mMainThread.start();
        }
    };

    Runnable customTask = new Runnable() {
        @SuppressWarnings("unlikely-arg-type")
        public void run() {
            while (true) {
                try {
                    customTask.wait(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                taskList = SocketService.getTaskList();

                if (!taskList.isEmpty()) {
                    System.out.println("************************************************************");
                    Collections.sort(taskList);
                }
                for (CustomTask cTask : taskList) {
                    if (cTask.getSecond() == customTaskcount) {
                        // 首先判断上一次任务有没有完成
                        if (!last_index.equals("")) {
                            count++;
                            if (count == 1) {
                                // last_index = "";
                                count = 0;
                            }
                        } else {
                            // to_be_handled_index.add(cb.getIndex());
                            SessionConnection.addCmdList(cTask.getMac_address(), cTask.getCmd());
                            log.info("excuteCMD" + cTask.getCmd());
                            // to_be_handled_index.remove(cb.getIndex());
                            taskList.remove(cTask);
                            // last_index = cb.getIndex();
                        }
                        customTaskcount--;
                    }

                    customTaskcount++;

                    break;
                }

            }

        }

    };

    public void start() {
        log.info("Startup push service...");
        scheduExec.scheduleAtFixedRate(pushTask, 5, 9999999, TimeUnit.SECONDS);
        // scheduExec.scheduleAtFixedRate(customTask, 5, 9999999,
        // TimeUnit.SECONDS);
        log.info("Startup push service success.");
    }

    public void stop() {
        log.info("Shutdown push service...");
        scheduExec.shutdownNow();
        log.info("Shutdown push service success.");
    }

}
