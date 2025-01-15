import QUser from "./QUser";

interface UserDto {
  toDomain(): QUser;
  toMap(uid: string | null): {};
}

class UserDto {
  id: string;
  name: string;
  email: string;
  resetDay: number;
  language: string;
  messagingToken: string;
  hasPhoto: boolean;

  constructor(
    id: string = "",
    name: string = "",
    email: string = "",
    resetDay: number = 1,
    language: string = "",
    messagingToken: string = "",
    hasPhoto: boolean = false,
  ) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.resetDay = resetDay;
    this.language = language;
    this.messagingToken = messagingToken;
    this.hasPhoto = hasPhoto;
  }
}

UserDto.prototype.toDomain = function() {
  return new QUser(
    this.id,
    this.name,
    this.email,
    this.resetDay,
    new URL(""),
    this.language,
    this.messagingToken,
    this.hasPhoto,
  );
}

UserDto.prototype.toMap = function(uid: string | null) {
    return  {
      "id": uid ?? this.id,
      "name": this.name,
      "email": this.email,
      "resetDay": this.resetDay,
      "language": this.language,
      "messagingToken": this.messagingToken,
      "hasPhoto": this.hasPhoto,
    };
}

export default UserDto;
