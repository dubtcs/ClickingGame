// Edited Version

let username = "";
const socket = io.connect("http://localhost:8080", {transports: ['websocket']});

setupSocket();

function setupSocket() {

    socket.on('initialize', function (event) {

        const gameSetup = JSON.parse(event);

        document.getElementById("clickerbutton").innerText = gameSetup['currency']

        let TableHTML = "<tr>\n" +
            "                <th></th>\n" +
            "                <th>Name</th>\n" +
            "                <th>Owned</th>\n" +
            "                <th>Cost</th>\n" +
            "                <th>Per Click</th>\n" +
            "                <th>Per Second</th>\n" +
            "            </tr>"

        for (let item of gameSetup["equipment"]) {
            let id = item['id']
            TableHTML += "<tr><th><button onclick='buyEquipment(\"" + id + "\")'>Buy</button></th>"
            TableHTML += "<th>" + item["name"] + "</th>"
            TableHTML += '<th id=\"' + item['id'] + '_owned\">0</th>'
            TableHTML += '<th id=\"' + item['id'] + '_cost\">0</th>'
            TableHTML += '<th>' + item["incomePerClick"] + '</th>'
            TableHTML += '<th>' + item["incomePerSecond"] + '</th>'
            TableHTML += "</tr>"
        }
        TableHTML += "</tr>"

        document.getElementById("poop").innerHTML = TableHTML

    });

    socket.on('gameState', function (event) {
        const gameState = JSON.parse(event);
        document.getElementById("displayCurrency").innerHTML = gameState['currency'].toFixed(0);
        const allEquipment = gameState['equipment'];
        for (const equipment of allEquipment) {
            const eqId = equipment['id'];
            document.getElementById(eqId + '_owned').innerHTML = equipment['numberOwned'];
            document.getElementById(eqId + '_cost').innerHTML = equipment['cost'].toFixed(0);
        }
    });
}


function submitUsername() {
    const enteredUsername = document.getElementById("username").value;
    if (enteredUsername !== "") {
        username = enteredUsername;
        socket.emit("startGame", enteredUsername);
    }
    document.body.removeChild(document.getElementById("username").parentNode);
}


function clickCurrency() {
    socket.emit("click");
}


function buyEquipment(equipmentID) {
    socket.emit("buy", equipmentID);
}
