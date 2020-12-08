import 'dart:async';
import 'package:flutter/material.dart';
import 'package:pothole_project/home.dart';

class SplashScreen extends StatefulWidget {
  @override
  _SplashScreenState createState() => _SplashScreenState();
}

class _SplashScreenState extends State<SplashScreen> {
  @override
  void initState() {
    super.initState();
    Timer(
      Duration(milliseconds: 2000),
      () => Navigator.pushReplacement(context, MaterialPageRoute(builder: (context) => Home()))
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Center(
            child: SizedBox(height: 70.0, width: 70.0,
              child: Image.asset('assets/icon/icon.png')
            ),
          ),
          SizedBox(height: 10.0,),
          Text(
            'Pothole Detection\nVisualization',
            textAlign: TextAlign.center,
            style: TextStyle(
              fontSize: 15.0,
            ),
          ),
        ],
      ),
    );
  }
}
