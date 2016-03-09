/**
 * Created by Isreal on 16/3/6.
 */

/**
 * Created by Fandaqian on 2016/2/26.
 */

var http = require('http');
var eventproxy = require('eventproxy');
var proxy = require('../proxy');

exports.initData = function(callback) {
    var postData = {
        "params": {},
        "sortField": "optime",
        "page": 1,
        "sortDir": "desc",
        "size": 10000000,
        "prefix": "filter_"
    };
    var postStr = JSON.stringify(postData);

    var options = {
        hostname: 'www.77cheyou.com',
        path: '/utility/baseInfoRest/findDictionaryByFilter',
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    };

    /*var postData = {"params":{"EQ_vehType":"XC-JBSYC"},"sortField":"optime","page":1,"sortDir":"desc","size":15,"prefix":"filter_"};
     var postStr = JSON.stringify(postData);

     var options = {
     hostname: 'test2.77cheyou.com',
     port: 80,
     path: '/dataService/customerRest/findCustomerByFilter',
     method: 'POST',
     headers: {
     'Content-Type': 'application/json',
     'Content-Length' : Buffer.byteLength(postStr, 'utf8')
     }
     };*/

    var req = http.request(options, function (res) {
        console.log('POSTDATA: ', postStr);
        console.log('STATUS: ', res.statusCode);
        console.log('HEADERS: ', JSON.stringify(res.headers));
        res.setEncoding('utf8');
        var body = '';
        res.on('data', function (chunk) {
            body += chunk;
        });
        res.on('end', function () {
            // console.log('DATA: ', body);
            var data = JSON.parse(body);
            var ep = new eventproxy();
            var len = data.length;

            ep.after('insert', function(list) {
                callback(len);
            });

            for (var i = 0; i < len; i++) {
                data[i].step = i + 1;
                proxy.dictProxy.save(data[i], ep.group('insert', function(dict) {
                    console.log('insert data', dict.step);
                }));
            }
        });
    });

    req.on('error', function (e) {
        console.log('problem with request: ', e.message);
    });

    // write data to request body
    req.write(postStr);
    req.end();
};