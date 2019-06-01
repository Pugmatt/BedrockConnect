var jack = require('dnsjack').createServer();

jack.listen(); // it listens on the standard DNS port of 53 per default

jack.route(['mco.mineplex.com', 'hivebedrock.network', 'play.inpvp.net', 'mco.lbsg.net', 'mco.cubecraft.net'], function(data, callback) {
	console.log(data);
	callback(null, 'Play.SkyBlockpe.com');
});

