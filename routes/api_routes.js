/**
 * Created by Fandaqian on 2016/3/3.
 */
var express = require('express');
var dictAPI = require('../api/dictionary_api');
var router  = express.Router();


router.get('/dict/count', dictAPI.count);
router.get('/dict/init', dictAPI.init);

router.get('/dict/findDictionaryByPathCode', dictAPI.findDictionaryByPathCode);
router.post('/dict/findDictionaryByIds', dictAPI.findDictionaryByIds);
router.get('/dict/findDictionaryById', dictAPI.findDictionaryById);
router.get('/dict/findDictionaryItemByPathCode', dictAPI.findDictionaryItemByPathCode);
router.post('/dict/findDictionaryByFilter', dictAPI.findDictionaryByFilter);

module.exports = router;