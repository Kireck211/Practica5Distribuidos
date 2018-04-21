// Types of requests

// Register new user
var request1 = {
    type: "set_name",
    data: {
        content: "userNickname"
    }
};

// Send message private user
var request2 = {
    type: "send_message",
    data : {
        to: "exampleUser1",
        content: "text text text text"
    }
};

// Request message to all connected users
var request3 = {
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
var request3 = {
    type: "exit"
};

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
