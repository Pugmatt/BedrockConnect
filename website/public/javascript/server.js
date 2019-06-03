$(document).ready(function() {

    $.get("/servers/",
    {},
    function(data,status){
        if(data) {
            for(var i=0;i<data.serverList.length;i++)
                $("#list").append('<option value="' + data.serverList[i] + '">' + data.serverList[i] + '</option>');
        }
    });

    $("#connect").click(function(){
        $.post("/servers/connect",
        {
        ip: "play.skyblockpe.com"
        },
        function(data,status){
        console.log(data);
        });
    });

    $("#add").click(function(){
        var ip = prompt("Enter a server IP", "play.skyblockpe.com");
        $.post("/servers/add",
        {
        ip: ip
        },
        function(data,status){
            if(data == "success")
            $("#list").append('<option value="' + ip + '" selected="selected">' + ip + '</option>');
        });
    });

});