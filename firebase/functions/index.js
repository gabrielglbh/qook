const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp({
  databaseURL: "https://qook-app.firebaseio.com",
});

const database = admin.firestore();

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
    .schedule("0 9 * * *")
    .timeZone("Europe/Madrid")
    .onRun(async (context) => {
      const userCollection = database.collection("USERS");

      try {
        const usersSnapshot = await userCollection.get();

        const today = new Date(Date.now());
        let dayOfWeek = today.getDay();

        if (dayOfWeek == 0) dayOfWeek = 6;
        else dayOfWeek--;

        for (const snapshot of usersSnapshot.docs) {
          if (snapshot.exists) {
            const user = (await userCollection.doc(snapshot.ref.id).get())
                .data();
            const planningRef = await userCollection.doc(snapshot.ref.id)
                .collection("PLANNING").get();

            if (user != undefined) {
              const resetDay = user.resetDay;
              const language = user.language;

              if (resetDay == dayOfWeek) {
                console.log("🥷 UID %s", snapshot.ref.id);

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

                await batch.commit().then((_) => {
                  console.log("✅ Successfully reset plannings and lists");
                }).catch((_) => {
                  console.log("❌ Some error occurred while resetting");
                });

                const payload = {
                  token: user.messagingToken,
                  notification: {
                    title: language == "ES" ?
                        intlMessages.ES.title : intlMessages.EN.title,
                    body: language == "ES" ?
                        intlMessages.ES.body : intlMessages.EN.body,
                  },
                };

                await admin.messaging().send(payload).then((result) => {
                  console.log("✅ Successfully sent notification to %s",
                      payload.token);
                  return {success: true};
                }).catch((reason) => {
                  console.log("❌ Error while sending the notification: %s",
                      reason.toString());
                  return {success: false};
                });
              }
            }
          }
        }
      } catch (error) {
        console.log("❌ Error setting up notifications");
      }
    });
