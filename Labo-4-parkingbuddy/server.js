var express = require('express');
var logfmt = require('logfmt');
var port = process.env.PORT || 3000;
var app = express();
app.use(logfmt.requestLogger());
app.use(express.compress());
app.use(express.static(__dirname + '/public'));
app.listen(port);
