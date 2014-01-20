var net = require('net');
var cluster = require('cluster');
var numCPUS = require('os').cpus().length;
var sockets = [];
	

if(cluster.isMaster){
	for(var i=0;i<numCPUS;i++){
		cluster.fork();
	}
	console.log('there are '+numCPUS+' CPU');
	cluster.on('exit',function(worker,code,signal){
		console.log('worker'+worker.process.pid+' died');
	});
}else{
	var server = net.createServer();

	server.on('close',function(){
		console.log('server close');
	});

	
	server.on('connection',function(socket){
		sockets.push(socket);
		console.log('client come '+sockets.length);
		socket.on('data',function(data){
			sockets.forEach(function(othersocket){
				if(socket!=othersocket){
					othersocket.write(data);	
				}
			});
		});
		socket.on('close',function(){
			console.log('client leave');
			var index = sockets.indexOf(socket);
			sockets.splice(index,1);
		});
		socket.on('error',function(err){
			console.log('client has error:'+err.message+','+sockets.length);
		});
	});
	
	server.listen(1234);
}

