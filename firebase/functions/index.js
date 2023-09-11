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
const intlMessagesSP = {
  "EN": {
    "title": "New week, new planning!",
    "body": "Start planning {0} today and make sure to eat healthy!",
  },
  "ES": {
    "title": "¡Nueva semana, nuevo planning!",
    "body": "Empieza a planificar {0} hoy y ¡asegúrate de comer sano!",
  },
};
const intlUpdateSharedPlanningMessages = {
  "EN": {
    "title": "{0} has been updated",
    "body": "{0} has updated the planning for {1}. Look what has been added",
  },
  "ES": {
    "title": "Se ha actualizado {0}",
    "body": "{0} ha actualizado el planning para el {1}. Mira que ha añadido",
  },
};
const mealData = {
  "meal": "",
  "op": "",
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
            {"id": "firstDay", "dayIndex": 0, "lunch": mealData,
              "dinner": mealData},
        );
        batch.set(
            planningRef.doc("secondDay"),
            {"id": "secondDay", "dayIndex": 1, "lunch": mealData,
              "dinner": mealData},
        );
        batch.set(
            planningRef.doc("thirdDay"),
            {"id": "thirdDay", "dayIndex": 2, "lunch": mealData,
              "dinner": mealData},
        );
        batch.set(
            planningRef.doc("fourthDay"),
            {"id": "fourthDay", "dayIndex": 3, "lunch": mealData,
              "dinner": mealData},
        );
        batch.set(
            planningRef.doc("fifthDay"),
            {"id": "fifthDay", "dayIndex": 4, "lunch": mealData,
              "dinner": mealData},
        );
        batch.set(
            planningRef.doc("sixthDay"),
            {"id": "sixthDay", "dayIndex": 5, "lunch": mealData,
              "dinner": mealData},
        );
        batch.set(
            planningRef.doc("seventhDay"),
            {"id": "seventhDay", "dayIndex": 6, "lunch": mealData,
              "dinner": mealData},
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
        const recipesRef = database.collection("USERS").doc(userId)
            .collection("RECIPES");
        const tagsRef = database.collection("USERS").doc(userId)
            .collection("TAGS");

        const planningPromises = [];
        const planning = await planningRef.get();
        planning.forEach((doc) => {
          planningPromises.push(doc.ref.delete());
        });

        const shoppingPromises = [];
        const shopping = await shoppingListRef.get();
        shopping.forEach((doc) => {
          shoppingPromises.push(doc.ref.delete());
        });

        const recipesPromises = [];
        const recipes = await recipesRef.get();
        recipes.forEach((doc) => {
          recipesPromises.push(doc.ref.delete());
        });

        const tagsPromises = [];
        const tags = await tagsRef.get();
        tags.forEach((doc) => {
          tagsPromises.push(doc.ref.delete());
        });

        await Promise.all(planningPromises);
        await Promise.all(shoppingPromises);
        await Promise.all(recipesPromises);
        await Promise.all(tagsPromises);

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
            {"id": "firstDay", "dayIndex": 0, "lunch": mealData,
              "dinner": mealData},
        );
        batch.set(
            planningRef.doc("secondDay"),
            {"id": "secondDay", "dayIndex": 1, "lunch": mealData,
              "dinner": mealData},
        );
        batch.set(
            planningRef.doc("thirdDay"),
            {"id": "thirdDay", "dayIndex": 2, "lunch": mealData,
              "dinner": mealData},
        );
        batch.set(
            planningRef.doc("fourthDay"),
            {"id": "fourthDay", "dayIndex": 3, "lunch": mealData,
              "dinner": mealData},
        );
        batch.set(
            planningRef.doc("fifthDay"),
            {"id": "fifthDay", "dayIndex": 4, "lunch": mealData,
              "dinner": mealData},
        );
        batch.set(
            planningRef.doc("sixthDay"),
            {"id": "sixthDay", "dayIndex": 5, "lunch": mealData,
              "dinner": mealData},
        );
        batch.set(
            planningRef.doc("seventhDay"),
            {"id": "seventhDay", "dayIndex": 6, "lunch": mealData,
              "dinner": mealData},
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

        const planningPromises = [];
        const planning = await planningRef.get();
        planning.forEach((doc) => {
          planningPromises.push(doc.ref.delete());
        });

        const shoppingPromises = [];
        const shopping = await shoppingListRef.get();
        shopping.forEach((doc) => {
          shoppingPromises.push(doc.ref.delete());
        });

        await Promise.all(planningPromises);
        await Promise.all(shoppingPromises);

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
    .schedule("0 7 * * *")
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
                    "lunch": mealData,
                    "dinner": mealData,
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

exports.scheduleRestartSharedPlanningCron = functions.pubsub
    .schedule("0 8 * * *")
    .timeZone("Europe/Madrid")
    .onRun(async (context) => {
      const groupCollection = database.collection("GROUPS");
      const usersCollection = database.collection("USERS");

      try {
        const groupsSnapshot = await groupCollection.get();

        const today = new Date(Date.now());
        let dayOfWeek = today.getDay();

        if (dayOfWeek == 0) dayOfWeek = 6;
        else dayOfWeek--;

        for (const snapshot of groupsSnapshot.docs) {
          if (snapshot.exists) {
            const group = (await groupCollection.doc(snapshot.ref.id).get())
                .data();
            const planningRef = await groupCollection.doc(snapshot.ref.id)
                .collection("PLANNING").get();

            if (group != undefined) {
              const resetDay = group.resetDay;
              const groupName = group.name;

              if (resetDay == dayOfWeek) {
                const batch = database.batch();

                for (const planning of planningRef.docs) {
                  batch.update(groupCollection.doc(snapshot.ref.id)
                      .collection("PLANNING").doc(planning.id), {
                    "lunch": mealData,
                    "dinner": mealData,
                  });
                }

                batch.update(groupCollection.doc(snapshot.ref.id)
                    .collection("SHOPPING_LIST").doc("INGREDIENTS"), {
                  "list": {},
                });

                await batch.commit().then((_) => {
                  console.log("✅ Successfully reset plannings and lists");
                }).catch((_) => {
                  console.log("❌ Some error occurred while resetting");
                });

                const notificationPromises = [];

                for (const user of group.users) {
                  const userData = (await usersCollection.doc(user).get())
                      .data();
                  const payload = {
                    token: userData.messagingToken,
                    notification: {
                      title: userData.language == "ES" ?
                          intlMessagesSP.ES.title :
                          intlMessagesSP.EN.title,
                      body: userData.language == "ES" ?
                          intlMessagesSP.ES.body.format(groupName) :
                          intlMessagesSP.EN.body.format(groupName),
                    },
                  };
                  notificationPromises.push(admin.messaging().send(payload));
                }

                await Promise.all(notificationPromises).then((result) => {
                  console.log("✅ Successfully sent notifications");
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

exports.onUpdateSharedPlanning = functions.firestore
    .document("GROUPS/{groupId}/PLANNING/{dpId}")
    .onUpdate(async (change, context) => {
      const newValue = change.after.data();
      const newLunch = newValue.lunch;
      const newDinner = newValue.dinner;

      const previousValue = change.before.data();
      const previousLunch = previousValue.lunch;
      const previousDinner = previousValue.dinner;

      if ((previousLunch !== newLunch && newLunch.meal.isNotEmpty()) ||
          (previousDinner !== newDinner && newDinner.meal.isNotEmpty())) {
        const groupCollection = database.collection("GROUPS");
        const usersCollection = database.collection("USERS");

        const group = (await groupCollection.doc(context.params.groupId).get())
            .data();
        const dayPlanning = (await groupCollection.doc(context.params.groupId)
            .collection("PLANNING").doc(context.params.dpId).get())
            .data();
        const groupName = group.name;
        let updatedDp = dayPlanning.dayIndex;

        const opData = (await usersCollection.doc(newLunch.op).get())
            .data();

        const notificationPromises = [];

        for (const user of group.users) {
          const userData = (await usersCollection.doc(user).get())
              .data();

          if (newLunch.op != user) {
            switch (updatedDp) {
              case 0:
                updatedDp = userData.language == "ES" ? "Lunes" : "Monday";
                break;
              case 1:
                updatedDp = userData.language == "ES" ? "Martes" : "Tuesday";
                break;
              case 2:
                updatedDp = userData.language == "ES" ?
                  "Miércoles" : "Wednesday";
                break;
              case 3:
                updatedDp = userData.language == "ES" ? "Jueves" : "Thursday";
                break;
              case 4:
                updatedDp = userData.language == "ES" ? "Viernes" : "Friday";
                break;
              case 5:
                updatedDp = userData.language == "ES" ? "Sábado" : "Saturday";
                break;
              case 6:
                updatedDp = userData.language == "ES" ? "Domingo" : "Sunday";
                break;
            }

            const payload = {
              token: userData.messagingToken,
              notification: {
                title: userData.language == "ES" ?
                    intlUpdateSharedPlanningMessages.ES.title.format(groupName):
                    intlUpdateSharedPlanningMessages.EN.title.format(groupName),
                body: userData.language == "ES" ?
                    intlUpdateSharedPlanningMessages.ES.body
                        .format(opData.name, updatedDp) :
                    intlUpdateSharedPlanningMessages.EN.body
                        .format(opData.name, updatedDp),
              },
            };
            notificationPromises.push(admin.messaging().send(payload));
          }
        }

        await Promise.all(notificationPromises).then((result) => {
          console.log("✅ Successfully sent notifications");
          return {success: true};
        }).catch((reason) => {
          console.log("❌ Error while sending the notification: %s",
              reason.toString());
          return {success: false};
        });
      }
    });
