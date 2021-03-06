package com.jiajiabingcheng.phonelog;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.CallLog;
import android.util.Log;
import android.provider.ContactsContract.CommonDataKinds;
import android.content.Context;


import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * PhoneLogPlugin
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class PhoneLogPlugin implements MethodCallHandler,
        PluginRegistry.RequestPermissionsResultListener {
    private final Registrar registrar;
    private Result pendingResult;

    private PhoneLogPlugin(Registrar registrar) {
        this.registrar = registrar;
    }

    public static void registerWith(Registrar registrar) {
        final MethodChannel channel =
                new MethodChannel(registrar.messenger(), "github.com/jiajiabingcheng/phone_log");
        PhoneLogPlugin phoneLogPlugin = new PhoneLogPlugin(registrar);
        channel.setMethodCallHandler(phoneLogPlugin);
        registrar.addRequestPermissionsResultListener(phoneLogPlugin);
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        if (pendingResult != null) {
            pendingResult.error("multiple_requests", "Cancelled by a second request.", null);
            pendingResult = null;
        }
        pendingResult = result;
        switch (call.method) {
            case "checkPermission":
                pendingResult.success(checkPermission());
                pendingResult = null;
                break;
            case "requestPermission":
                requestPermission();
                break;
            case "getPhoneLogs":
                String startDate = call.argument("startDate");
                String duration = call.argument("duration");
                fetchCallRecords(startDate, duration);
                break;
            case "deleteItem":
                int idOfRowToDelete = call.argument("idOfRowToDelete");
                deleteRow(idOfRowToDelete);
                break;
            default:
                result.notImplemented();
        }
    }

    private void requestPermission() {
        Log.i("PhoneLogPlugin", "Requesting permission : " + Manifest.permission.READ_CALL_LOG);
        String[] perm = {Manifest.permission.READ_CALL_LOG,
                Manifest.permission.WRITE_CALL_LOG};
        registrar.activity().requestPermissions(perm, 0);
        registrar.activity().requestPermissions(perm, 1);
    }

    private boolean checkPermission() {
        Log.i("PhoneLogPlugin", "Checking permission : " + Manifest.permission.READ_CALL_LOG);
        return PackageManager.PERMISSION_GRANTED
                == registrar.activity().checkSelfPermission(Manifest.permission.READ_CALL_LOG);
    }

    @Override
    public boolean onRequestPermissionsResult(int requestCode,
                                              String[] strings,
                                              int[] grantResults) {
        boolean res = false;
        if (requestCode == 0 && grantResults.length > 0) {
            res = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            pendingResult.success(res);
            pendingResult = null;
        }
        return res;
    }

    private static final String[] PROJECTION =
            {CallLog.Calls.CACHED_FORMATTED_NUMBER,
                    //CallLog.Calls.CACHED_MATCHED_NUMBER,
                    CallLog.Calls.NUMBER,
                    CallLog.Calls.TYPE,
                    CallLog.Calls.CACHED_NAME,
                    CallLog.Calls.DATE,
                    CallLog.Calls.DURATION,
                    CallLog.Calls.CACHED_NUMBER_TYPE,
                    CallLog.Calls.PHONE_ACCOUNT_ID,
                    CallLog.Calls.CACHED_PHOTO_URI
            };

    private void deleteRow(int idOfRowToDelete) {
        try {
            // getContentResolver().delete(Uri.withAppendedPath(CallLog.Calls.CONTENT_URI, String.valueOf(idOfRowToDelete)), "", null);
        }
        catch (Exception ex) {
            System.out.print("Exception here ");
        }
        pendingResult.success(null);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void fetchCallRecords(String startDate, String duration) {
        if (registrar.activity().checkSelfPermission(Manifest.permission.READ_CALL_LOG)
                == PackageManager.PERMISSION_GRANTED) {
            String selectionCondition = null;
            if (startDate != null) {
                selectionCondition = CallLog.Calls.DATE + "> " + startDate;
            }
//            if (duration != null) {
//                String durationSelection = CallLog.Calls.DURATION + "> " + duration;
//                if (selectionCondition != null) {
//                    selectionCondition = selectionCondition + " AND " + durationSelection;
//                } else {
//                    selectionCondition = durationSelection;
//                }
//            }
            Cursor cursor = registrar.context().getContentResolver().query(
                    CallLog.Calls.CONTENT_URI, PROJECTION,
                    selectionCondition,
                    null, CallLog.Calls.DATE + " DESC");

            try {
                ArrayList<HashMap<String, Object>> records = getCallRecordMaps(cursor);
                pendingResult.success(records);
                pendingResult = null;
            } catch (Exception e) {
                Log.e("PhoneLog", "Error on fetching call record" + e);
                pendingResult.error("PhoneLog", e.getMessage(), null);
                pendingResult = null;
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

        } else {
            pendingResult.error("PhoneLog", "Permission is not granted", null);
            pendingResult = null;
        }
    }


    /**
     * Builds the list of call record maps from the cursor
     *
     * @param cursor
     * @return the list of maps
     */
    private ArrayList<HashMap<String, Object>> getCallRecordMaps(Cursor cursor) {
        ArrayList<HashMap<String, Object>> records = new ArrayList<>();

        while (cursor != null && cursor.moveToNext()) {
            CallRecord record = new CallRecord();
            record.formattedNumber = cursor.getString(0);
            record.number = cursor.getString(1);
            record.callType = getCallType(cursor.getInt(2));
            record.cashed_name = cursor.getString(3);

            Date date = new Date(cursor.getLong(4));
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            record.dateYear = cal.get(Calendar.YEAR);
            record.dateMonth = cal.get(Calendar.MONTH);
            record.dateDay = cal.get(Calendar.DAY_OF_MONTH);
            record.dateHour = cal.get(Calendar.HOUR_OF_DAY);
            record.dateMinute = cal.get(Calendar.MINUTE);
            record.dateSecond = cal.get(Calendar.SECOND);
            record.duration = cursor.getLong(5);
            record.cashed_number_type = getPhoneType(cursor.getInt(6));
            record.phone_account_id = cursor.getString(7);
            record.cashed_photo_uri = cursor.getString(8);

            records.add(record.toMap());
        }
        return records;
    }

    private String getCallType(int anInt) {
        switch (anInt) {
            case CallLog.Calls.INCOMING_TYPE:
                return "входящий";
            case CallLog.Calls.OUTGOING_TYPE:
                return "исходящий";
            case CallLog.Calls.MISSED_TYPE:
                return "пропущенный";
//            case CommonDataKinds.Phone.BLOCKED_TYPE:
//                return "блокированный";
//            case CommonDataKinds.Phone.REJECTED_TYPE:
//                return "отклоненный";
            default:
                 break;
        }
        return null;

    }


    private String getPhoneType(int anInt) {
        switch (anInt) {
            case CommonDataKinds.Phone.TYPE_HOME:
                return "дом";
            case CommonDataKinds.Phone.TYPE_MOBILE:
                return "моб";
            case CommonDataKinds.Phone.TYPE_WORK:
                return "раб";
            case CommonDataKinds.Phone.TYPE_OTHER:
                return "другой";
            default:
                break;
        }
        return null;
    }
}
