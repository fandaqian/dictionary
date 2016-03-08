/**
 * Created by Fandaqian on 2016/3/4.
 */

var _ = require('lodash');

var models  = require('../models');
var Dict    = models.Dict;

var changeKeyNames = function(obj, namesMap) {
    return _.transform(obj, function(result, value, key) {
        result[namesMap[key] || key] = value;
    });
};

var getDate = function(optime) {
    return optime.getFullYear();
};

var appendList = function(data) {
    var list = [];
    data.forEach(function(d) {
        var temp = d.toObject();
        delete temp.__v;
        temp.optime = getDate(temp.optime);
        list.push(changeKeyNames(temp, {'_id': 'id'}));
    });
    return list;
};

exports.save = function(dict, callback) {
    var dic = new Dict();
    dic._id = dict.id;
    if (dict.optime) {
        dic.optime = new Date(Date.parse(dict.optime));
    }
    dic.parentId = dict.parentId;
    dic.code = dict.code;
    dic.name = dict.name;
    dic.note = dict.note;
    dic.pathCode = dict.pathCode;
    dic.classification = dict.classification;
    dic.dicPath = dict.dicPath;
    dic.dicLevel = dict.dicLevel;
    dic.sortNum = dict.sortNum;
    dic.step = dict.step;
    dic.save(callback);
};

exports.getCount = function (callback) {
    Dict.count(callback);
};

exports.findDictionaryById = function (id, callback) {
    Dict.findById(id, function(err, data) {
        var temp = null;
        if (data) {
            temp = data.toObject();
            temp.id = temp._id;
            delete temp.__v;
            delete temp._id;
        }
        callback(err, temp);
    });
};

exports.findDictionaryByPathCode = function(patchCode, level, callback) {
    Dict.find({'pathCode': new RegExp(patchCode), 'dicLevel': level}).sort({'sortNum': 1}).exec(function(err, data) {
        callback(err, appendList(data));
    });
};

exports.findDictionaryItemByPathCode = function(patchCode, callback) {
    Dict.find({'pathCode': patchCode}).sort({'sortNum': 1}).exec(function(err, data) {
        callback(err, appendList(data));
    });
};

exports.findDictionaryByIds = function(ids, callback) {
    Dict.find({'_id': {'$in': ids}}).sort('sortNum').exec(function(err, data) {
        callback(err, appendList(data));
    });
};

exports.findDictionaryByFilter = function(filter, callback) {

    var params = filter.params;
    var par = {};
    for (var param in params) {
        var key = param;
        var val = params[param];
        var temp = key.split("_");
        if (temp[0] === 'EQ') {
            eval('par.' + temp[1] + ' = val;');
        } else if (temp[0] === 'LIKE') {
            eval('par.' + temp[1] + ' = new RegExp(val);');
        } else if (temp[0] === 'LLIKE') {
            eval('par.' + temp[1] + ' = new RegExp("^" + ' + 'val);');
        }
    }
    var limit = {limit: filter.size };
    if (filter.sortDir && filter.sortField) {
        var sc = filter.sortDir === 'asc' ? '' : '-';
        limit.sort = sc + filter.sortField;
    }
    if (filter.page !== 1) {
        limit.skip = (filter.page - 1) * filter.size;
    }

    console.log('where:', par);
    console.log('limit:', limit);
    Dict.find(par, null, limit).exec(function(err, data) {
        callback(err, appendList(data));
    });
};