Dictionary
=

## 介绍

Dictionary 是使用 [**Nodeclub**](https://github.com/cnodejs/nodeclub) 框架开发的缓存系统，将外网的Dictionary 数据保存在本地 **MongoDB** 中，达到访问加速和外网重启不影响本地使用的效果。

## 安装部署

版本 [node.js](https://nodejs.org) v5.6.0，[MongoDB](https://www.mongodb.org) v2.6.1.1。

```
1. 安装 `Node.js[必须]` `MongoDB[必须]`
2. 启动 MongoDB 修改 models/index.js 数据库配置
3. 修改 api/dict_init.js 并添加 hostname
4. `$ npm install` 安装 Dictionary 依赖包
5. `$ node bin/www`
6. 执行 http://localhost:30000/dict/init 初始化数据
7. done!
```

## 实现API

```
findDictionaryByPathCode
findDictionaryByIds
findDictionaryById
findDictionaryItemByPathCode
findDictionaryByFilter
findByUserName
count
```

## 已知问题

```
1. 新数据获取问题
2. 亲友团Job报错
3. 代码重构
```

## License

MIT