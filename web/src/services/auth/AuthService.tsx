import { setDoc, doc, updateDoc, getDoc } from 'firebase/firestore';
import { auth, db } from '../firebase.config';
import { createUserWithEmailAndPassword, signInWithEmailAndPassword, signOut } from 'firebase/auth';
import { DB_USER, FIREBASE_HOST } from '../../components/Globals';
import QUser from '../../models/user/QUser';
import UserDto from '../../models/user/QUserDto';

export const signInUser = async (email: string, password: string): Promise<string | number> => {
    return await signInWithEmailAndPassword(auth, email, password)
        .then((userCredential) => {
            return userCredential.user.uid;
        })
        .catch((_) => {
            return -1;
        });
}

export const registerUser = async (email: string, password: string): Promise<string | number> => {
    return await createUserWithEmailAndPassword(auth, email, password)
        .then((userCredential) => {
            return userCredential.user.uid;
        })
        .catch((_) => {
            return -1;
        });
}

export const signUserOut = async () => {
    await signOut(auth);
}

export const createUserInDB = async (user: QUser): Promise<null | string> => {
    const currentUser = auth.currentUser; 
    return await setDoc(doc(db, DB_USER, currentUser!.uid), user.toDto().toMap(currentUser!.uid))
        .then((_) => {
            return null;
        })
        .catch((error) => {
            return error.message;
        });
}

export const updateUser = async (user: QUser): Promise<QUser | string> => {
    const currentUser = auth.currentUser; 
    return await updateDoc(doc(db, DB_USER, currentUser!.uid), user.toDto().toMap(null))
        .then((_) => {
            if (user.photo.host != FIREBASE_HOST && user.photo != new URL("")) {
                // TODO: uploadImage in storage
                /*storage.uploadImage(
                    user.photo,
                    "${STORAGE_USERS}${user.id}/${STORAGE_AVATAR}"
                )*/
            }
            return user;
        })
        .catch((error) => {
            return error.message;
        });
}

export const getUser = async (): Promise<QUser | string> => {
    const currentUser = auth.currentUser; 
    return await getDoc(doc(db, DB_USER, currentUser!.uid))
        .then((res) => {
            const userDto = res.data() as UserDto;
            const user = userDto.toDomain();
            if (user.hasPhoto) {
                // TODO: get the download url from storage
                /*val result =
                    storage.getDownloadUrl("${STORAGE_USERS}${it.uid}/${STORAGE_AVATAR}")
                result.fold(
                    ifLeft = {},
                    ifRight = { uri -> user = user.copy(photo = uri) }
                )*/
            }
            return user;
        })
        .catch((error) => {
            return error.message;
        });
}
