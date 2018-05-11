// Types of requests

// Register new user
var requestSendMessage = {
    type: "set_name",
    data: {
        content: "userNickname"
    }
};

// Send message private user
var requestSendMesage2 = {
    type: "send_message",
    data : {
        to: "exampleUser1",
        content: "text text text text"
    }
};

// Request message to all connected users
var requestSendMessage3 = {
    type: "send_message",
    data : {
        to: "all",
        content: "text text text text"
    }
};

// Request connected users
var requestList = {
    type: "list_users"
};

// Request user disconnected
var requestExit = {
    type: "exit"
};

var requestSendFile = {
    type: "send_file",
    data: {
        receiver: "user"
    }
};

var requestEndFile = {
    type: "file_sent"
};

// Reques block given username
var requestBlockUser = {
    type: "block_user",
    data: {
        content: "userNickname"
    }
}

// Response Everything ok
var responseOk = {
    type: "response",
    resultCode: 200
};

// Message to client
var responseMessage = {
    type : "message_received",
    resultCode: 200,
    data: {
        from: "user2",
        content: "text text text"
    }
};

// List of users
var responseUsers = {
    type: "list_users",
    resultCode: 200,
    data: {
        users: ["user1", "user2", "user3"]
    }
};

// Internal server error
var responseBad = {
    type: "response",
    resultCode: 500,
    error: "error description"
};

var responseReceiveFile = {
    type: "receive_file",
    data: {
        from: "user1"
    }
};
