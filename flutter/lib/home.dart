import 'package:flutter/material.dart';

class Home extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final appBar = AppBar(
      title: Text('Pothole Detection'),
    );

    final body = Container(
      child: Center(
        child: Text('Welcome'),
      ),
    );

    return Scaffold(
      appBar: appBar,
      body: body,
    );
  }
}
