import UserDto from "./QUserDto";

interface QUser {
  toDto(): UserDto;
}

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

QUser.prototype.toDto = function() {
  return new UserDto(
    this.id,
    this.name,
    this.email,
    this.resetDay,
    this.language,
    this.messagingToken,
    this.hasPhoto,
  );
}

export default QUser;
