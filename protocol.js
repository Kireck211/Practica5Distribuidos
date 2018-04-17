// types of requests
// Register new user
var request1 = {
    "type": "setName",
    "data": {
        "content": "userNickname"
    }
};

// Send message private user
var request2 = {
    "type": "sendMessage",
    "data" : {
        "to": "exampleUser1",
        "content": "text text text text"
    }
};

// Send message to all connected users
var request3 = {
    "type": "sendMessage",
    "data" : {
        "to": "all",
        "content": "text text text text"
    }
};

// User disconnected
var request3 = {
    "type": "exit"
};

// Internal server error
var response = {
    "type": "response",
    "resultCode": 500,
    "error": "error description"
};
