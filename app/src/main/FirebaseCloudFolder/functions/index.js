const functions = require('firebase-functions');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

//import firebase admin module
const admin=require('firebase-admin')

admin.initializeApp(functions.config().firebase)

/* Listens for new messages added to /notifications /:pushId and sends a notification to subscribed users */
exports.pushNotifications=functions.database.ref('/Notifications/{cityStateName}/{pushId}').onWrite((change,context)=>{

    console.log("push notification event triggered")

    /*Grab the current value  of what was written to the real time database*/
    var valueObject=change.after.val();

    console.log(valueObject.title);
    console.log(valueObject.description);
    console.log(valueObject.cityState);

    /* create a notification  and data payload .They contains the notification information and message to be sent respectively*/

    const payload={
            // notification:{
            //     title:valueObject.title,
            //     body:valueObject.description,
            //     sound:"default"
            // }
            // ,
            data:{
               title: valueObject.title,
               description:valueObject.description,
               timeDate:valueObject.timeDate
           }
        };
    const options={
            priority:"high",
            timeToLive:60*60*24 //24 hours
        };
    const cityStateName=context.params.cityStateName;
    console.log(cityStateName);
//    var topic=valueObject.cityState;
    return admin.messaging().sendToTopic(cityStateName,payload,options)
    .then(function(response){
            return console.log("Sucessfully sent message:",response);
    })
    .catch(function(error){
            return console.log("Error sending message: ",error);
    });
});
