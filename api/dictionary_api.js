/**
 * Created by Fandaqian on 2016/3/3.
 */

var eventproxy = require('eventproxy');
var validator  = require('validator');

var proxy      = require('../proxy');
var util       = require('../util/util');

exports.init = function(req, res, next) {
    var ep = new eventproxy();
    ep.fail(next);

    require('./dictInit').initData(function(len) {
        res.send(util.sendOKJSON(null, 'insert ' + len + ' data!'));
    });
};

exports.count = function(req, res, next) {
    var ep = new eventproxy();
    ep.fail(next);

    proxy.dictProxy.getCount(
        ep.done(function (count) {
            res.send(util.sendOKJSON({
                count: count
            }));
        })
    );
};

exports.findDictionaryById = function(req, res, next) {
    var ep = new eventproxy();
    ep.fail(next);

    var id  = req.query.id;

    proxy.dictProxy.findDictionaryById(id, function (err, data) {
        if (!err) {
            res.send(data);
        }
    });
};

exports.findDictionaryByPathCode = function(req, res, next) {
    var ep = new eventproxy();
    ep.fail(next);

    var pathCode  = req.query.pathCode;
    var level  = parseInt(req.query.level);

    proxy.dictProxy.findDictionaryByPathCode(pathCode, level, function(err, list) {
        if (!err) {
            res.send(list);
        }
    });
};

exports.findDictionaryItemByPathCode = function(req, res, next) {
    var ep = new eventproxy();
    ep.fail(next);

    var pathCode  = req.query.pathCode;

    proxy.dictProxy.findDictionaryItemByPathCode(pathCode, function(err, list) {
        if (!err) {
            res.send(list);
        }
    });
};

exports.findDictionaryByIds = function(req, res, next) {
    var ep = new eventproxy();
    ep.fail(next);

    console.log('POST DATA:', JSON.stringify(req.body));

    proxy.dictProxy.findDictionaryByIds(req.body, function(err, list) {
        if (!err) {
            res.send(list);
        }
    });
};

exports.findDictionaryByFilter = function(req, res, next) {
    var ep = new eventproxy();
    ep.fail(next);

    console.log('POST DATA:', JSON.stringify(req.body));

    var postBody = req.body;
    var filter = {};
    filter.params = postBody.params;
    filter.page = postBody.page || 1;
    filter.size = postBody.size || 15;
    filter.sortField = postBody.sortField;
    filter.sortDir = postBody.sortDir;
    filter.prefix = postBody.prefix || 'filter_';

    proxy.dictProxy.findDictionaryByFilter(filter,  function(err, list) {
        if (!err) {
            var len = list.length;
            var resData = {};
            resData.status = 200;
            resData.filter = filter;
            resData.count = len;
            resData.result = list;
            res.send(resData);
        }
    });
};