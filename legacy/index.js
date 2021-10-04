var jack = require('dnsjack').createServer();

var database = require('./website/database');

var async = require('asyncawait/async');
var await = require('asyncawait/await');

const db = async (function() {
    await (database.load());
    await (database.connect());
	console.log("Loaded models");
	
	jack.listen(); // it listens on the standard DNS port of 53 per default
	
	jack.route(['mco.mineplex.com', 'hivebedrock.network', 'play.inpvp.net', 'mco.lbsg.net', 'mco.cubecraft.net', 'play.pixelparadise.gg'], function(data, callback) {
		
		database.Server.findOne({ 
			where: {
				ip: data.rinfo.address
			}
		}).then(function(server) {
			if(server && server.join) {
				callback(null, server.join);
			} else {
				callback(null, data.domain);
			}
		});
	});
});
db();

