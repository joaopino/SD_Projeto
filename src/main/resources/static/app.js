var stompClient = null;

function setConnected(connected) {
    // TODO: Implement setConnected function
    if (connected) {
        $("#messages").show();
    }
    else {
        $("#messages").hide();
    }

}

function connect() {
    var socket = new SockJS('/SocketD');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/status', function(message) {
            var status = JSON.parse(message.body);
            updateStatusTable(status);
        });
    });
}
function updateStatusTable(statusArray) {
    var table = $('#messages');
    table.find('tbody').empty(); // Clear existing table rows
    
    statusArray.forEach(function(status) {
      var row = $('<tr>');
      row.append($('<td>').text(status.name));
      row.append($('<td>').text(status.status));
      row.append($('<td>').text(status.priority));
      row.append($('<td>').text(status.isDaemon));
      table.find('tbody').append(row);
    });
  }

 

function disconnect() {
    // TODO: Implement disconnect function
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");

}
/*
function sendMessage() {
    stompClient.send("/app/message", {}, JSON.stringify({'content': $("#message").val()}));
}

function showMessage(message) {
    // TODO: Implement showMessage function
    $("#messages").append("<tr><td>" + message + "</td></tr>");
}
*/
$(function () {
   
    connect();

});

