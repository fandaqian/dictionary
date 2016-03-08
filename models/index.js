/**
 * Created by Fandaqian on 2016/3/2.
 */

var mongoose = require('mongoose');

var config = {
    db: 'mongodb://127.0.0.1:12345/dictionary'
};

mongoose.connect(config.db, {
    server: {poolSize: 20}
}, function (err) {
    if (err) {
        console.error('connect to %s error: ', config.db, err.message);
        process.exit(1);
    }
});

// models
require('./dictionary');

exports.Dict         = mongoose.model('Dict');