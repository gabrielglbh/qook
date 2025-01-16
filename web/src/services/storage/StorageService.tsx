import { deleteObject, getDownloadURL, ref, uploadBytes } from "firebase/storage";
import { auth, storage } from "../firebase.config";


export const uploadImage = async (file: ArrayBuffer, path: string): Promise<string> => {
    const currentUser = auth.currentUser;
    if (currentUser == null) return "";

    const storageRef = ref(storage, path);
    await uploadBytes(storageRef, file);
    return await getDownloadURL(storageRef)
        .then((url) => {
            return url;
        })
        .catch((error) => {
            return error.message;
        });;       
}

export const getImageDownloadURL = async (path: string): Promise<string> => {
    const currentUser = auth.currentUser;
    if (currentUser == null) return "";

    const storageRef = ref(storage, path);
    return await getDownloadURL(storageRef)
        .then((url) => {
            return url;
        })
        .catch((error) => {
            return error.message;
        });;       
}

export const deleteImage = async (path: string): Promise<string> => {
    const currentUser = auth.currentUser;
    if (currentUser == null) return "Error";

    const storageRef = ref(storage, path);
    return await deleteObject(storageRef)
        .then((_) => {
            return "";
        })
        .catch((error) => {
            return error.message;
        });;       
}