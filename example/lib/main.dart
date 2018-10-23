import 'dart:async';

import 'package:fixnum/fixnum.dart';
import 'package:flutter/material.dart';
import 'package:phone_log/phone_log.dart';

//import 'package:sms/contact.dart';

//Future<ContactSMS>  myContactSMS;
//ContactSMS myContactSMS;

//Future getContactSMS(String searchString) async {
//  ContactQuery contacts = new ContactQuery();
//  try {
//    myContactSMS = await contacts.queryContact(searchString);
//  } catch (Exception) {
//    //myContactSMS.photo = AssetImage();
//  }
//}

void main() => runApp(new MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => new _MyAppState();
}

class _MyAppState extends State<MyApp> {
//  Iterable<CallRecord> _callRecords;
//  var phoneLog = new PhoneLog();
//
//  Future<Null> fetchCallLogs() async {
//    var callLogs = await phoneLog.getPhoneLogs(
//        // startDate: 20180605, duration: 15 seconds
//        startDate: new Int64(1525590000000),
//        duration: new Int64(1));
//    setState(() {
//      _callRecords = callLogs;
//    });

  Iterable<CallRecord> _callRecords;
  var phoneLog = new PhoneLog();

  fetchCallLogs() async {
    var callLogs = await phoneLog.getPhoneLogs(
      // startDate: 20180605, duration: 15 seconds
        startDate: new Int64(1525590000000),
        duration: new Int64(1));
    setState(() {
      _callRecords = callLogs;

    });
  }

  requestPermission() async {
    bool res = await phoneLog.requestPermission();
    print("permission request result is " + res.toString());
  }

  checkPermission() async {
    bool res = await phoneLog.checkPermission();
    print("permission is " + res.toString());
  }

  @override
  void initState() {

    // setState(() {
    fetchCallLogs();
    //});

  }


  @override
  Widget build(BuildContext context) {

    return new MaterialApp(
      home: new Scaffold(
        appBar: new AppBar(title: new Text('CallLog example')),
//        body: new Center(
//          child: new Column(children: children),
        body:
        _callRecords != null
            ?
        ListView.builder(
          itemCount: _callRecords?.length,
          itemBuilder: (context, index) {
            return new FutureBuilder(
              //future: getContactSMS(_callRecords?.elementAt(index)?.number?.toString() ?? '112'),
                builder: (context, snapshot) {
                  return ListTile(
                    leading: (_callRecords != null && _callRecords.length > 0)
                        ?
                    CircleAvatar(
                        radius: 24.0,
                        //backgroundImage: MemoryImage(myContactSMS.thumbnail.bytes))
                        child: Text("Av"))
                        :
                    CircleAvatar(
                      radius: 24.0,
                      child: Text("Av"),
                    ),
                    title: Text(
                      //(_callRecords?.elementAt(index)?.cashed_name?.toString() ?? 'имя')
                        (_callRecords?.elementAt(index)?.cashed_name?.toString() ?? _callRecords?.elementAt(index)?.number?.toString())
                    ),
                    subtitle: Text(
                        (_callRecords?.elementAt(index)?.callType?.toString() ?? 'тип?? ') + ' ' +
                            (_callRecords?.elementAt(index)?.number?.toString() ?? 'неизвестный номер') + ' ' +
                            (_callRecords?.elementAt(index)?.cashed_number_type?.toString() ?? 'неизвестно') + '\n' +
                            (_callRecords?.elementAt(index)?.phone_account_id?.toString() ?? 'ID')
//                            + '\n' +   (myContactSMS.fullName ?? 'fullName')
//                       (_callRecords?.elementAt(index)?.cashed_photo_uri?.toString() ?? 'cashed_photo_uri')
                    ),

                    trailing: Text(
                      //(_callRecords?.elementAt(index)?.dateDay?.toString() ?? 'день') + '/' +
                      //(_callRecords?.elementAt(index)?.dateMonth?.toString() ?? 'месяц') + '/' +
                      //(_callRecords?.elementAt(index)?.dateYear?.toString() ?? 'год') + ' ' +
                        (_callRecords?.elementAt(index)?.dateHour?.toString() ?? 'час') + ':' +
                            (_callRecords?.elementAt(index)?.dateMinute?.toString() ?? 'минута') + '\n' +
                            (_callRecords?.elementAt(index)?.dateDay?.toString() ?? 'день') + '/' +
                            (_callRecords?.elementAt(index)?.dateMonth?.toString() ?? 'месяц') + '/' +
                            (_callRecords?.elementAt(index)?.dateYear?.toString() ?? 'год')
//                         + ' ' +   (_callRecords?.elementAt(index)?.dateHour?.toString() ?? 'час')
                      // + ':' (_callRecords?.elementAt(index)?.dateSecond?.toString() ?? 'секунда')
                    ),
                  );
                }
            );

          },
        )
            :
        Center(
          child: Text("Загрузка контактов...",
              style: TextStyle(color: Colors.black, fontSize: 15.0, fontWeight: FontWeight.w300)),
        ),

//              leading: CircleAvatar(
//                  radius: 24.0,
//                  backgroundImage: MemoryImage(myContactSMS.thumbnail.bytes)
//                  backgroundImage: NetworkImage(_callRecords?.elementAt(index)?.cashed_photo_uri.toString())
//                  backgroundImage: NetworkImage("file://com.android.contacts/display_photo/648")
//                  child: Text("Av"),
//              ),
        floatingActionButton: new FloatingActionButton(
          onPressed: () {
            setState(() {
              fetchCallLogs();
            });

          },
          child: new Icon(Icons.refresh),
        ),

      ),
    );

  }
}

