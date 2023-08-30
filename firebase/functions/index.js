const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp({
  databaseURL: "https://qook-app.firebaseio.com",
});

const database = admin.firestore();

const message = {
  token: "",
  notification: {
    title: "",
    body: "",
  },
};

const intlMessages = {
  "EN": {
    "title": "New week, new planning!",
    "body": "Start planning your week today and make sure to eat healthy!",
  },
  "ES": {
    "title": "¡Nueva semana, nuevo planning!",
    "body": "Empieza a planificar tu semana hoy y ¡asegúrate de comer sano!",
  },
};

exports.scheduleRestartPlanningCron = functions.pubsub
    .schedule("0 10 * * *")
    .timeZone("Europe/Madrid")
    .onRun(async () => {
      const userCollection = database.collection("USERS");

      try {
        const usersSnapshot = await userCollection.get();

        const today = new Date(Date.now());
        let dayOfWeek = today.getDay();

        if (dayOfWeek == 0) dayOfWeek = 6;
        else dayOfWeek--;

        for (const snapshot of usersSnapshot.docs) {
          if (snapshot.exists) {
            const userRef = await userCollection.doc(snapshot.ref.id).get();
            const planningRef = await userCollection.doc(snapshot.ref.id)
                .collection("PLANNING").get();

            const user = userRef.data();

            if (user != undefined) {
              const batch = database.batch();

              for (const planning of planningRef.docs) {
                batch.update(database.collection("USERS").doc(snapshot.ref.id)
                    .collection("PLANNING").doc(planning.id), {
                  "lunch": "",
                  "dinner": "",
                });
              }

              batch.update(userCollection.doc(snapshot.ref.id)
                  .collection("SHOPPING_LIST").doc("INGREDIENTS"), {
                "list": {},
              });

              await batch.commit();

              const resetDay = user.resetDay;
              const language = user.language;

              if (resetDay == dayOfWeek) {
                message.token = user.messagingToken;
                if (language == "ES") {
                  message.notification.title = intlMessages.ES.title;
                  message.notification.body = intlMessages.ES.body;
                } else {
                  message.notification.title = intlMessages.EN.title;
                  message.notification.body = intlMessages.EN.body;
                }

                admin.messaging().send(message)
                    .then((response) => {
                      console.log("Successfully sent message: " + response);
                    })
                    .catch((error) => {
                      console.log("Error sending notification: " + error);
                    });
              }
            }
          }
        }
      } catch (error) {
        console.log("Error setting up notifications");
      }
    });
