import { setDoc, doc, updateDoc, getDoc } from 'firebase/firestore';
import { auth, db } from '../firebase.config';
import { createUserWithEmailAndPassword, signInWithEmailAndPassword, signOut } from 'firebase/auth';
import { DB_USER, FIREBASE_HOST, STORAGE_AVATAR, STORAGE_USERS } from '../../components/Globals';
import QUser, { userToDto } from '../../models/user/QUser';
import UserDto, { userDtoToDomain, userDtoToMap } from '../../models/user/QUserDto';
import { getImageDownloadURL, uploadImage } from '../storage/StorageService';

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
    return await setDoc(doc(db, DB_USER, currentUser!.uid), userDtoToMap(userToDto(user), currentUser!.uid))
        .then((_) => {
            return null;
        })
        .catch((error) => {
            return error.message;
        });
}

export const updateUser = async (user: QUser): Promise<QUser | string> => {
    const currentUser = auth.currentUser; 
    return await updateDoc(doc(db, DB_USER, currentUser!.uid), userDtoToMap(userToDto(user), null))
        .then(async (_) => {
            if (user.photo.includes(FIREBASE_HOST) && user.photo != "") {
                await uploadImage(user.photo, `${STORAGE_USERS}${user.id}/${STORAGE_AVATAR}`)
                    .then((_) => {
                        return user;
                    })
                    .catch((error) => {
                        return error.message;
                    });
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
        .then(async (res) => {
            const userDto = res.data() as UserDto;
            const user = userDtoToDomain(userDto);
            if (user.hasPhoto) {
                await getImageDownloadURL(`${STORAGE_USERS}${user.id}/${STORAGE_AVATAR}`)
                    .then((url) => {
                        return {
                            ...user,
                            photo: url,
                        }
                    })
                    .catch((error) => {
                        return error.message;
                    });
            }
            return user;
        })
        .catch((error) => {
            return error.message;
        });
}
