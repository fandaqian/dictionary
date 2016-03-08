/**
 * Created by Fandaqian on 2016/3/3.
 */
var mongoose  = require('mongoose');
var Schema    = mongoose.Schema;

var DictSchema = new Schema({
    _id: { type: String},
    optime: { type: Date},
    parentId: { type: String },
    code: { type: String},
    name: { type: String },
    note: {type: String},
    pathCode: { type: String },
    classification: { type: String },
    dicPath: { type: String },
    dicLevel: { type: Number, default: 0 },
    sortNum: { type: Number, default: 0 }
});

mongoose.model('Dict', DictSchema);