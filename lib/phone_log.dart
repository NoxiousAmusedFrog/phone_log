import 'dart:async';

import 'package:fixnum/fixnum.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

/// Provide methods to access and fetch the phone log.
class PhoneLog {
  final MethodChannel _channel;

  static final PhoneLog _instance = new PhoneLog.private(
      const MethodChannel('github.com/jiajiabingcheng/phone_log'));

  /// Provides an instance of this class.
  factory PhoneLog() => _instance;

  @visibleForTesting
  PhoneLog.private(MethodChannel platformChannel):_channel = platformChannel;

  /// Check a [permission] and return a [Future] with the result
  Future<bool> checkPermission() async {
    final bool isGranted = await _channel.invokeMethod("checkPermission", null);
    return isGranted;
  }

  /// Request a [permission] and return a [Future] with the result
  Future<bool> requestPermission() async {
    final bool isGranted =
        await _channel.invokeMethod("requestPermission", null);
    return isGranted;
  }

  ///Fetches phone logs
  ///
  ///The unit of [startDate] is the Milliseconds of date.
  ///The unit of [duration] is second.
  Future<Iterable<CallRecord>> getPhoneLogs(
      {Int64 startDate, Int64 duration}) async {
    var _startDate = startDate?.toString();
    var _duration = duration?.toString();
    Iterable records = await _channel.invokeMethod(
        'getPhoneLogs', {"startDate": _startDate, "duration": _duration});
    return records?.map((m) => new CallRecord.fromMap(m));
  }


  Future<bool> deleteItem(
      {Int64 idOfRowToDelete}) async {
    final bool isDone = await _channel.invokeMethod(
        'deleteItem', {"idOfRowToDelete": idOfRowToDelete});
    return isDone;
  }

}

/// The class that carries all the data for one call history entry.
class CallRecord {
  CallRecord({
    this.formattedNumber,
    this.number,
    this.callType,
    this.cashed_name,
    this.dateYear,
    this.dateMonth,
    this.dateHour,
    this.dateMinute,
    this.dateSecond,
    this.duration,
    this.cashed_number_type,
    this.phone_account_id,
    this.cashed_photo_uri
  });

  String formattedNumber, number, callType, cashed_name, cashed_number_type, phone_account_id, cashed_photo_uri;
  int dateYear, dateMonth, dateDay, dateHour, dateMinute, dateSecond, duration;


  CallRecord.fromMap(Map m) {
    formattedNumber = m['formattedNumber'];
    number = m['number'];
    callType = m['callType'];
    cashed_name = m['cashed_name'];
    dateYear = m['dateYear'];
    dateMonth = m['dateMonth'];
    dateDay = m['dateDay'];
    dateHour = m['dateHour'];
    dateMinute = m['dateMinute'];
    dateSecond = m['dateSecond'];
    duration = m['duration'];
    cashed_number_type = m['cashed_number_type'];
    phone_account_id = m['phone_account_id'];
    cashed_photo_uri = m['cashed_photo_uri'];
  }
}
