const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp({
  databaseURL: "https://qook-app.firebaseio.com",
});

const database = admin.firestore();
const storage = admin.storage();

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

exports.onCreateUser = functions.firestore
    .document("USERS/{uid}")
    .onCreate(async (snap, context) => {
      try {
        const userId = context.params.uid;

        const planningRef = database.collection("USERS").doc(userId)
            .collection("PLANNING");
        const shoppingListRef = database.collection("USERS").doc(userId)
            .collection("SHOPPING_LIST").doc("INGREDIENTS");

        const batch = database.batch();

        batch.set(
            planningRef.doc("firstDay"),
            {"id": "firstDay", "dayIndex": 0, "lunch": "", "dinner": ""},
        );
        batch.set(
            planningRef.doc("secondDay"),
            {"id": "secondDay", "dayIndex": 1, "lunch": "", "dinner": ""},
        );
        batch.set(
            planningRef.doc("thirdDay"),
            {"id": "thirdDay", "dayIndex": 2, "lunch": "", "dinner": ""},
        );
        batch.set(
            planningRef.doc("fourthDay"),
            {"id": "fourthDay", "dayIndex": 3, "lunch": "", "dinner": ""},
        );
        batch.set(
            planningRef.doc("fifthDay"),
            {"id": "fifthDay", "dayIndex": 4, "lunch": "", "dinner": ""},
        );
        batch.set(
            planningRef.doc("sixthDay"),
            {"id": "sixthDay", "dayIndex": 5, "lunch": "", "dinner": ""},
        );
        batch.set(
            planningRef.doc("seventhDay"),
            {"id": "seventhDay", "dayIndex": 6, "lunch": "", "dinner": ""},
        );
        batch.set(
            shoppingListRef,
            {"list": {}},
        );

        await batch.commit().then(() => {
          console.log("✅ Successfully initiated user");
        }).catch((reason) => {
          console.log("❌ Error while initializing user %s", reason.toString());
        });
      } catch (error) {
        console.log("❌ Error while initializing user %s", error);
      }
    });

exports.onRemoveUser = functions.firestore
    .document("USERS/{uid}")
    .onDelete(async (snap, context) => {
      try {
        const userId = context.params.uid;

        const planningRef = database.collection("USERS").doc(userId)
            .collection("PLANNING");
        const shoppingListRef = database.collection("USERS").doc(userId)
            .collection("SHOPPING_LIST");

        const batch = database.batch();

        batch.delete(planningRef);
        batch.delete(shoppingListRef);

        const recipesRef = database.collection("USERS").doc(userId)
            .collection("RECIPES");
        batch.delete(recipesRef);

        const tagsRef = database.collection("USERS").doc(userId)
            .collection("TAGS");
        batch.delete(tagsRef);

        await batch.commit().then(() => {
          console.log("✅ Successfully removed user");
        }).catch((reason) => {
          console.log("❌ Error while removing user %s", reason.toString());
        });

        const bucket = storage.bucket();
        await bucket.deleteFiles({
          prefix: "users/" + userId,
        }).then(() => {
          console.log("✅ Successfully removed user images");
        }).catch((reason) => {
          console.log("❌ Error while removing user images %s",
              reason.toString());
        });
      } catch (error) {
        console.log("❌ Error while removing user %s", error);
      }
    });

exports.onCreateSharedPlanning = functions.firestore
    .document("GROUPS/{id}")
    .onCreate(async (snap, context) => {
      try {
        const groupId = context.params.id;

        const planningRef = database.collection("GROUPS").doc(groupId)
            .collection("PLANNING");
        const shoppingListRef = database.collection("GROUPS").doc(groupId)
            .collection("SHOPPING_LIST").doc("INGREDIENTS");

        const batch = database.batch();

        batch.set(
            planningRef.doc("firstDay"),
            {"id": "firstDay", "dayIndex": 0, "lunch": "", "dinner": ""},
        );
        batch.set(
            planningRef.doc("secondDay"),
            {"id": "secondDay", "dayIndex": 1, "lunch": "", "dinner": ""},
        );
        batch.set(
            planningRef.doc("thirdDay"),
            {"id": "thirdDay", "dayIndex": 2, "lunch": "", "dinner": ""},
        );
        batch.set(
            planningRef.doc("fourthDay"),
            {"id": "fourthDay", "dayIndex": 3, "lunch": "", "dinner": ""},
        );
        batch.set(
            planningRef.doc("fifthDay"),
            {"id": "fifthDay", "dayIndex": 4, "lunch": "", "dinner": ""},
        );
        batch.set(
            planningRef.doc("sixthDay"),
            {"id": "sixthDay", "dayIndex": 5, "lunch": "", "dinner": ""},
        );
        batch.set(
            planningRef.doc("seventhDay"),
            {"id": "seventhDay", "dayIndex": 6, "lunch": "", "dinner": ""},
        );
        batch.set(
            shoppingListRef,
            {"list": {}},
        );

        await batch.commit().then(() => {
          console.log("✅ Successfully initiated shared planning");
        }).catch((reason) => {
          console.log("❌ Error while initializing shared planning %s",
              reason.toString());
        });
      } catch (error) {
        console.log("❌ Error while initializing shared planning %s", error);
      }
    });

exports.onRemoveSharedPlanning = functions.firestore
    .document("GROUPS/{id}")
    .onDelete(async (snap, context) => {
      try {
        const groupId = context.params.id;

        const planningRef = database.collection("GROUPS").doc(groupId)
            .collection("PLANNING");
        const shoppingListRef = database.collection("GROUPS").doc(groupId)
            .collection("SHOPPING_LIST");

        const batch = database.batch();

        batch.delete(planningRef);
        batch.delete(shoppingListRef);

        await batch.commit().then(() => {
          console.log("✅ Successfully removed shared planning");
        }).catch((reason) => {
          console.log("❌ Error while removing shared planning %s",
              reason.toString());
        });

        const bucket = storage.bucket();
        await bucket.deleteFiles({
          prefix: "groups/" + groupId,
        }).then(() => {
          console.log("✅ Successfully removed shared planning images");
        }).catch((reason) => {
          console.log("❌ Error while removing shared planning images %s",
              reason.toString());
        });
      } catch (error) {
        console.log("❌ Error while removing shared planning %s", error);
      }
    });

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
        console.log("❌ Error setting up notifications %s", error);
      }
    });
