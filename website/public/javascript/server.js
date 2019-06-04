$(document).ready(function() {

    $.get("/servers/",
    {},
    function(data,status){
        if(data) {
            for(var i=0;i<data.serverList.length;i++)
                $("#list").append('<option value="' + data.serverList[i] + '">' + data.serverList[i] + '</option>');
            if(data.join)
                $("#server").text(data.join);
            else
                $("#server").text($('#list').val());    
        }
    });

    $('#list').click(function() {
        var selected = $('#list').val();
        if(selected) {
            $('#connect').prop('disabled', false);
        }
        else {
            $('#connect').prop('disabled', true);
        }
    });

    $("#connect").click(function(){
        $.post("/servers/connect",
        {
            ip: $('#list').val()
        },
        function(data,status){
            if(data == "success") {
                $("#server").text($('#list').val());
            } else {
                alert(data.error);
            }
        });
    });

    $("#add").click(function(){
        var ip = prompt("Enter a server IP", "play.skyblockpe.com");
        $.post("/servers/add",
        {
        ip: ip
        },
        function(data,status){
            if(data == "success") {
                $("#list").append('<option value="' + ip + '" selected="selected">' + ip + '</option>');
            }
            else {
                alert(data.error);
            }
        });
    });

});