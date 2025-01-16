import UserDto from "./QUserDto";

class QUser {
  id: string;
  name: string;
  email: string;
  resetDay: number;
  photo: string;
  language: string;
  messagingToken: string;
  hasPhoto: boolean;

  constructor(
    id: string = "",
    name: string = "",
    email: string = "",
    resetDay: number = 1,
    photo: string = "",
    language: string = "",
    messagingToken: string = "",
    hasPhoto: boolean = false,
  ) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.resetDay = resetDay;
    this.photo = photo;
    this.language = language;
    this.messagingToken = messagingToken;
    this.hasPhoto = hasPhoto;
  }
}

export const userToDto = (user: QUser) => {
  return new UserDto(
    user.id,
    user.name,
    user.email,
    user.resetDay,
    user.language,
    user.messagingToken,
    user.hasPhoto,
  );
}

export default QUser;
