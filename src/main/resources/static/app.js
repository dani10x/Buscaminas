const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/buscaminas'
});

stompClient.onConnect = (frame) => {
    setConnected(true);
    console.log('Connected: ' + frame);
    stompClient.subscribe('/user/queue/buscaminas', (response) => {
       showGreeting(response);
    });

};

stompClient.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    stompClient.activate();
}

function disconnect() {
    stompClient.deactivate();
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.publish({
        destination: "/app/hello",
        body: JSON.stringify({'nombre': $("#name").val()})
    });
}

function showGreeting(respuesta) {
console.log(respuesta.body);
let format = JSON.parse(respuesta.body);
console.log(format);
    $("#greetings").append("<tr><td>" + respuesta.body + "</td></tr>");

if(format.idUsuario) {
    connectNotifications(format.idUsuario);
}
}
function connectNotifications (idUser) {
    let canal = "/topic/" + idUser + "/queue/notificaciones"
    stompClient.subscribe(canal, (response) => {
           showGreeting(response);
    });
}
function nuevoJuego() {
    stompClient.publish({
        destination: "/app/nuevo",
        body: JSON.stringify({'filas': 3, 'columnas': 3, 'minas': 3})
    });
}

function clickCuadro() {
    stompClient.publish({
        destination: "/app/revelar",
        body: JSON.stringify({'x': $("#x").val(), 'y': $("#y").val()})
    });
}

function iniciar() {
    stompClient.publish({
        destination: "/app/iniciar",
        body: {}
    });
}

function getJuego() {
    stompClient.publish({
        destination: "/app/get",
        body: {}
    });
}

$(function () {
    $("form").on('submit', (e) => e.preventDefault());
    $( "#connect" ).click(() => connect());
    $( "#disconnect" ).click(() => disconnect());
    $( "#send" ).click(() => sendName());
    $("#nuevo").click(() => nuevoJuego());
    $("#juego").click(() => getJuego());
    $("#iniciar").click(() => iniciar());
    $("#click").click(() => clickCuadro());
});