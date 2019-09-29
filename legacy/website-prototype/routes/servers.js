var express = require('express');
var router = express.Router();

const database = require("../database.js");

function ip(req) {
    var ip = (req.headers['x-forwarded-for'] ||
    req.connection.remoteAddress ||
    req.socket.remoteAddress ||
    req.connection.socket.remoteAddress).split(",")[0];
    if(ip.substr(0, 7) == "::ffff:") {
        ip = ip.substr(7);
    }
    return ip;
}

/* GET users listing. */
router.get('/', function(req, res, next) {
  database.Server.findOne({
      where: { ip: ip(req) }
  }).then(function(server) {
      if(server)
        res.send({join: server.join, serverList: JSON.parse(server.serverList)});
      else
        res.send(null);  
  });
});

router.post('/connect', function(req, res, next) {
    if(req.body.ip) {
                database.Server.findOne({
                    where: { ip: ip(req) }
                }).then(function(server) {
                    if(server) {
                        server.join = req.body.ip;
                        server.save().then(function() {
                            res.send("success");
                        });
                    }
                    else
                      res.send({error: "IP does not contain servers"});  
                });
    }
    else
        res.send({error: "Request must contain an IP"});
});

router.post('/remove', function(req, res, next) {
    if(req.body.ip) {
        database.Server.findOne({
            where: { ip: ip(req) }
        }).then(function(server) {
            if(!server) {
                res.send("This IP does not contain any serverlist settings");
            } else {
                var ips = JSON.parse(server.serverList);
                if(ips.indexOf(req.body.ip) != -1) {
                    ips.splice(ips.indexOf(req.body.ip), 1);
                    server.serverList = JSON.stringify(ips);
                    server.save().then(function() {
                        res.send("success");
                    });
                } else {
                    res.send("IP does not exist");
                }
            }
        });
    }
    else
        res.send({error: "Request must contain an IP"});
});
router.post('/add', function(req, res, next) {
    if(req.body.ip) {
      /**  (async () => {
            var reachable = await isReachable(req.body.ip);
            if(reachable) {
                console.log("ye") **/
                database.Server.findOne({
                    where: { ip: ip(req) }
                }).then(function(server) {
                    if(!server) {
                        var ips = [];
                        ips.push(req.body.ip);
                        database.Server.build({
                            ip: ip(req),
                            serverList: JSON.stringify(ips)
                        }).save().then(function() {
                            res.send("success");
                        });
                    }
                    else {
                        var ips = JSON.parse(server.serverList);
                        if(ips.indexOf(req.body.ip) == -1) {
                            ips.push(req.body.ip);
                            server.serverList = JSON.stringify(ips);
                            server.save().then(function() {
                                res.send("success");
                            });
                        } else {
                            res.send("IP already exists");
                        }
                    }
                });
           /** } else {
                console.log("nah")
                res.send("IP is not reachable");
            }
        })(); **/
    }
    else
        res.send({error: "Request must contain an IP"});
});

module.exports = router;
