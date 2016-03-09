/**
 * Created by Isreal on 16/3/6.
 */

exports.sendOKJSON = function(data, msg) {
    var result = {};
    result.status = 200;
    var msg1 = msg || 'OK';
    result.msg = msg1;
    result.result = data;
    return result;
};

exports.sendErrJSON = function(err, msg) {
    var result = {};
    result.status = 500;
    result.msg = msg;
    result.result = err;
    return result;
};